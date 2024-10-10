package com.dwarflegends.webapp.domain.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CachingData {
    private Map<String,Object> data;
    private Map<String,List<DataLink>> links;
}
