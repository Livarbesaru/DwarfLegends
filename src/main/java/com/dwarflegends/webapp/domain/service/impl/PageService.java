package com.dwarflegends.webapp.domain.service.impl;

import com.dwarflegends.webapp.domain.exception.AURException;
import com.dwarflegends.webapp.domain.exception.ErrorCodes;
import com.dwarflegends.webapp.domain.model.TagDetails;
import com.dwarflegends.webapp.domain.component.ICacheComponent;
import com.dwarflegends.webapp.domain.service.IPageService;
import com.dwarflegends.webapp.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
public class PageService implements IPageService {
    private ICacheComponent cacheComponent;
    @Autowired
    public PageService(ICacheComponent cacheComponent) {
        this.cacheComponent = cacheComponent;
    }
    @Override
    public String getPath(String tag) throws AURException {
        final String methodName = Util.getMethodName("getPath");
        log.info("{} loading path for tag={}",methodName,tag);
        String path = Optional.ofNullable(cacheComponent.getAvailableTag().get(tag))
                .map(TagDetails::path).orElse(null);
        if(path == null){
            log.info("Path not found for tag={}",tag);
            throw new AURException("Page not found", ErrorCodes.ERROR_400);
        }
        return path;
    }
}
