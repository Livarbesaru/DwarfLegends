package com.dwarflegends.webapp.domain.service;

import com.dwarflegends.webapp.domain.exception.AURException;

public interface IPageService {
    String getPath(String tag) throws AURException;
}
