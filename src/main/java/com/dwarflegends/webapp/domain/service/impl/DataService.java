package com.dwarflegends.webapp.domain.service.impl;

import com.dwarflegends.webapp.domain.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.dwarflegends.webapp.domain.component.ICacheComponent;
import com.dwarflegends.webapp.domain.service.IDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class DataService implements IDataService {
    private final ICacheComponent cacheComponent;
    private final XmlMapper objectMapper;
    private final Map<String, CacheConfig> baseCacheConfiguration;
    private final Map<String, CacheNestingRule> baseCachingElementsRule;
    private final String defaultCacheKey;
    private final List<Object> defaultCache;

    @Autowired
    public DataService(
            ObjectMapper objectMapper,
            ICacheComponent cacheComponent,
            @Value("#{${base.caching.config.per.key}}") Map<String, Object> baseCacheConfigMap,
            @Value("#{${base.caching.elements.by.key}}") Map<String, Object> baseCachingElementsRule,
            @Value("${default.cache.key}") String defaultCacheKey
    ) {
        this.baseCacheConfiguration = objectMapper.convertValue(baseCacheConfigMap, new TypeReference<>() {
        });
        this.baseCachingElementsRule = objectMapper.convertValue(baseCachingElementsRule, new TypeReference<>() {
        });
        this.cacheComponent = cacheComponent;
        this.objectMapper = new XmlMapper();
        this.defaultCacheKey = defaultCacheKey;
        this.defaultCache = new ArrayList<>();
    }

    @Override
    public void analyzeData(MultipartFile file, DataType dataType) throws IOException {
        HashMap<String, Map<String, Object>> data = new HashMap<>();
        Map<String, Object> mappedXmlDump = objectMapper.readValue(file.getBytes(), new TypeReference<HashMap<String, Object>>() {
        });
        if (DataType.LEGEND.equals(dataType)) {
            data.put(defaultCacheKey, new HashMap<>(Map.of(defaultCacheKey, defaultCache)));
            analyzeDataPerConfiguration(mappedXmlDump, baseCachingElementsRule, new HashMap<>(), data);
            cacheComponent.saveBaseData((Map) data);
        } else {
            cacheComponent.savePlusData(mappedXmlDump);
        }
    }

    @Override
    public Map<String, Object> getBaseData() {
        return cacheComponent.getBaseData();
    }

    private void analyzeDataPerConfiguration(Map<String, Object> baseData,
                                             Map<String, CacheNestingRule> cacheNestingRuleMap,
                                             Map<String, Object> dataToPass,
                                             Map<String, Map<String, Object>> allCache) {
        //cycles the map to analyze it
        baseData.forEach((key, value) -> {
            //takes the cacheRule if it exists
            CacheNestingRule cacheRule = cacheNestingRuleMap.get(key);
            CacheConfig cacheConfig;
            //checks if there is a nesting in case there is a caching on the main object
            Map<String, CacheNestingRule> cacheNestingMap = cacheRule != null ? cacheRule.getNesting() : null;
            List<Map<String, Object>> nestingToCycle = new ArrayList<>();

            //in case the rule and the key configuration exist
            //it proceed to get the relative configuration for the cache
            if (cacheRule != null && cacheRule.getKeyConfig() != null) {
                cacheConfig = baseCacheConfiguration.get(cacheRule.getKeyConfig());
            } else {
                cacheConfig = null;
            }

            if (cacheConfig != null) {
                analyzeCacheElements(
                        cacheConfig,
                        value,
                        dataToPass,
                        allCache);
            }

            //in case there is are nesting to cache, it adds the main obj to the list
            if (cacheNestingMap != null) {
                addToList(value, nestingToCycle, new TypeReference<>() {
                });
            }

            //proceed to cycle the nesting doing recursions on the method
            if (!ObjectUtils.isEmpty(nestingToCycle)) {
                nestingToCycle.forEach(val -> {
                    Map<String, Object> dataNestingPass = new HashMap<>();
                    if(cacheConfig != null && cacheConfig.getDataToPassMap() != null){
                        dataNestingPass = getDataToPass(dataNestingPass,val,dataToPass,cacheConfig);
                    } else if (cacheRule != null && cacheRule.getDataToPassMap() != null) {
                        dataNestingPass = getDataToPass(dataNestingPass,val,dataToPass,cacheRule);
                    }
                    analyzeDataPerConfiguration(val, cacheNestingMap, dataNestingPass, allCache);
                });
            }
        });
    }

    private void analyzeCacheElements(CacheConfig cacheConfig, Object object, Map<String, Object> dataToPass, Map<String, Map<String, Object>> allCache) {
        Map<String, Object> mappedEl = new HashMap<>();
        List<Map<String, Object>> elementsToCache = new ArrayList<>();
        addToList(object, elementsToCache, new TypeReference<>() {
        });
        elementsToCache
                .forEach(snglElement -> {
                    //Map to which the values to generate the key are taken
                    Map<String, Object> dataForKey = new HashMap<>(snglElement);
                    //data passed from the parent has been put to be available for the key generation
                    dataForKey.putAll(dataToPass);
                    String keyToUse = generateKeyPerConf(dataForKey, cacheConfig.getKeyElements());

                    //the map of relations per relation "type" is created
                    Map<String, List<DataLink>> dataLinksMap = new HashMap<>();

                    //data to put in the relative cache
                    CachingData cachingData = new CachingData(new HashMap<>(snglElement), dataLinksMap);
                    mappedEl.put(keyToUse, cachingData);
                    Optional.ofNullable(cacheConfig.getLinksPerKey())
                            .ifPresent(links -> links.forEach((keyLink, link) -> {
                                Object linkData = dataForKey.get(keyLink);
                                cachingData.getData().remove(keyLink);
                                if (linkData instanceof Map || linkData instanceof List) {
                                    analyzeLink(keyLink, link, linkData, dataLinksMap, cacheConfig.getDataToPassMap() != null ? cacheConfig.getInheritedData(dataForKey) : new HashMap<>());
                                }else if(linkData != null){
                                    analyzeLink(keyLink, link, Map.of(keyLink,linkData), dataLinksMap, cacheConfig.getDataToPassMap() != null ? cacheConfig.getInheritedData(dataForKey) : new HashMap<>());
                                }
                            }));
                });
        Map<String, Object> cache = allCache.get(cacheConfig.getCache());
        if (cache != null) {
            cache.putAll(mappedEl);
        } else {
            allCache.put(cacheConfig.getCache(), mappedEl);
        }
    }

    private void analyzeLink(String relationKey, KeyLinkDefinition link, Object linkData, Map<String, List<DataLink>> dataLinkMap, Map<String, Object> dataToPass) {
        List<Map<String, Object>> elementsOnLink = new ArrayList<>();
        addToList(linkData, elementsOnLink, new TypeReference<>() {
        });

        String cache = link.getCache();
        Map<Integer, String> keyLinkDefinition = link.getKeyElements();
        AtomicReference<Map<String, Object>> dataForKeyAndLinks = new AtomicReference<>(new HashMap<>(dataToPass));
        if (cache != null && keyLinkDefinition != null) {
            elementsOnLink.forEach(element -> {
                Map<String,Object> dataForKey = new HashMap<>(dataForKeyAndLinks.get());
                dataForKey.putAll(element);
                String keyElement = generateKeyPerConf(dataForKey, keyLinkDefinition);
                addLinkToMap(relationKey, new DataLink(keyElement, cache), dataLinkMap);
            });
        }
        if (link.getNestedLink() != null) {
            elementsOnLink.forEach(element -> link.getNestedLink().forEach((keyLink, nestedLink) -> {
                Map<String,Object> dataForKeyToPass = new HashMap<>(dataForKeyAndLinks.get());
                dataForKeyToPass.putAll(element);
                dataForKeyToPass = link.getDataToPassMap() != null ? link.getInheritedData(dataForKeyToPass) : new HashMap<>();
                final Map<String, Object> finalDataForKeyToPass = dataForKeyToPass;
                Object linkObject = element.get(keyLink);
                List<Map<String, Object>> nestedLinkList = new ArrayList<>();
                if (linkObject != null) {
                    addToList(linkObject, nestedLinkList, new TypeReference<>() {
                    });
                }
                nestedLinkList.forEach(el -> analyzeLink(
                                keyLink,
                                nestedLink,
                                el,
                                dataLinkMap,
                                link.getDataToPassMap() != null ? link.getInheritedData(finalDataForKeyToPass) : new HashMap<>()));
            }));
        }
    }

    private String generateKeyPerConf(Map<String, Object> el, Map<Integer, String> keys) {
        List<String> keyList = new ArrayList<>();
        keys.entrySet().stream().sorted((a, b) -> a.getKey() > b.getKey() ? 1 : -1)
                .forEach(key -> keyList.add(String.valueOf(el.get(key.getValue()))));
        return String.join("-", keyList);
    }

    private <T> void addToList(Object el, List<T> elList, TypeReference<T> type) {
        if (el instanceof Map) {
            elList.add((T) el);
        } else if (el instanceof List) {
            elList.addAll((List<T>) el);
        }
    }

    private void addLinkToMap(String key, DataLink singleDataLink, Map<String, List<DataLink>> dataLinkMap) {
        List<DataLink> dataLinkList = dataLinkMap.computeIfAbsent(key, k -> new ArrayList<>());
        dataLinkList.add(singleDataLink);
    }

    private Map<String,Object> getDataToPass(Map<String,Object> dataNestingPass, Map<String,Object> val, Map<String,Object> dataToPass, TransferableData transferableManager){
        dataNestingPass.putAll(val);
        dataNestingPass.putAll(dataToPass);
        return transferableManager.getInheritedData(dataNestingPass);
    }
}
