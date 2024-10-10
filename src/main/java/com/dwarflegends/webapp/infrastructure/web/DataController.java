package com.dwarflegends.webapp.infrastructure.web;

import com.dwarflegends.webapp.domain.service.impl.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
public class DataController {

    private final DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/fetch/base")
    public Map<String,Object> getBase(){
        return dataService.getBaseData();
    }
}
