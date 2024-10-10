package com.dwarflegends.webapp.domain.component;

import com.dwarflegends.webapp.domain.model.TagDetails;

import java.util.Map;

public interface ICacheComponent {
    Map<String, TagDetails> getAvailableTag();
    void saveBaseData(Map<String,Object> data);

    void savePlusData(Map<String, Object> data);

    Map<String, Object> getBaseData();

    Map<String, Object> getPlusData();
}
