package com.dwarflegends.webapp.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyLinkDefinition implements TransferableData{
    @JsonProperty(value = "cache",required = true)
    private String cache;
    @JsonProperty(value = "key_elements",required = true)
    private Map<Integer,String> keyElements;
    @JsonProperty(value = "nested_link")
    private Map<String,KeyLinkDefinition> nestedLink;
    @JsonProperty("data_to_pass")
    private Map<String, DataToPass> dataToPassMap;

    @Override
    public Map<String, Object> getInheritedData(Map<String, Object> data) {
        return baseInheritedDataMapping(data,dataToPassMap);
    }
}
