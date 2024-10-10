package com.dwarflegends.webapp.domain.service;

import com.dwarflegends.webapp.domain.exception.AURException;
import com.dwarflegends.webapp.domain.model.TagDetails;
import com.dwarflegends.webapp.domain.service.impl.PageService;
import com.dwarflegends.webapp.domain.component.ICacheComponent;
import com.dwarflegends.webapp.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@Slf4j
@ExtendWith(SpringExtension.class)
@Import({PageService.class})
public class PageServiceTest {

    @Autowired
    private PageService pageService;
    @MockBean
    private ICacheComponent iCacheComponent;
    @Test
    void getPath() throws AURException {
        when(iCacheComponent.getAvailableTag())
                .thenReturn(Map.of(
                        Util.getTag("main"),
                        new TagDetails(Util.getTag("main"),"")
                ));
        assertNotNull(pageService.getPath("main"));
    }

    @Test
    void getPathThrows(){
        assertThrows(AURException.class,()->pageService.getPath(Util.getTag("main")));
    }
}
