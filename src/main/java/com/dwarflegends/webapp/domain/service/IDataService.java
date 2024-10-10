package com.dwarflegends.webapp.domain.service;

import com.dwarflegends.webapp.domain.model.DataType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IDataService {
    void analyzeData(MultipartFile file, DataType dataType) throws IOException;
    Map<String,Object> getBaseData();
}
