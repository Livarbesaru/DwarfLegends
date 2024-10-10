package com.dwarflegends.webapp.domain.component.impl;

import com.dwarflegends.webapp.domain.model.TagDetails;
import com.dwarflegends.webapp.domain.component.ICacheComponent;
import com.dwarflegends.webapp.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CacheComponent implements ICacheComponent {
    private final Map<String, TagDetails> configurationPath;
    private Map<String, Object> baseData;
    private Map<String, Object> plusData;

    @Autowired
    public CacheComponent(@Value("#{${page.tag-path}}") Map<String, String> tagToPath){
        this.configurationPath = new HashMap<>();
        for (Map.Entry<String, String> entry : tagToPath.entrySet()) {
            final String tag = Util.getTag(entry.getKey());
            this.configurationPath.put(tag,new TagDetails(tag, entry.getValue()));
        }
    }

    @Override
    public Map<String, TagDetails> getAvailableTag() {
        final String methodName = Util.getMethodName("getAvailableTag");
        log.info("{} fetching data", methodName);
        return configurationPath;
    }

    @Override
    public void saveBaseData(Map<String, Object> data) {
        this.baseData = data;
    }
    @Override
    public void savePlusData(Map<String, Object> data) {
        this.plusData = data;
    }

    @Override
    public Map<String, Object> getBaseData() {
        return baseData;
    }
    @Override
    public Map<String, Object> getPlusData() {
        return plusData;
    }
}
