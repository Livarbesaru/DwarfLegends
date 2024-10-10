package com.dwarflegends.webapp.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CacheNestingRule implements TransferableData{
    @JsonProperty("config_id")
    private String keyConfig;
    @JsonProperty("nesting")
    private Map<String,CacheNestingRule> nesting;
    @JsonProperty("data_to_pass")
    private Map<String, DataToPass> dataToPassMap;

    @Override
    public Map<String, Object> getInheritedData(Map<String, Object> data) {
        return baseInheritedDataMapping(data,dataToPassMap);
    }
}
