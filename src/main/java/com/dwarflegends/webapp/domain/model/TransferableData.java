package com.dwarflegends.webapp.domain.model;

import java.util.Map;
import java.util.stream.Collectors;

public interface TransferableData {
    Map<String, Object> getInheritedData(Map<String, Object> data);

    default Map<String, Object> baseInheritedDataMapping(Map<String, Object> data,Map<String, DataToPass> dataToPassMap) {
        return data.entrySet().stream()
                .filter(entry -> dataToPassMap.get(entry.getKey()) != null)
                .map(entry -> {
                    DataToPass dataToPass = dataToPassMap.get(entry.getKey());
                    return Map.entry(dataToPass.getAlias(), entry.getValue());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
