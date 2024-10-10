package com.dwarflegends.webapp.infrastructure.web;

import com.dwarflegends.webapp.domain.exception.AURException;
import com.dwarflegends.webapp.domain.exception.ErrorCodes;
import com.dwarflegends.webapp.domain.model.DataType;
import com.dwarflegends.webapp.domain.service.IDataService;
import com.dwarflegends.webapp.domain.service.IPageService;
import com.dwarflegends.webapp.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Controller
public class MainController {
    private final String badRequestTemplate;
    private final IPageService pageService;
    private final IDataService dataService;

    @Autowired
    public MainController(IPageService pageService,
                          @Value("${page.bad-template-name}") String badRequestTemplate,
                          IDataService dataService) {
        this.pageService = pageService;
        this.badRequestTemplate = badRequestTemplate;
        this.dataService = dataService;
    }

    @GetMapping(URIConstants.MAIN)
    public ModelAndView main() {
        final String methodName = Util.getMethodName("main");
        log.info("{} Received a call at main",methodName);
        ModelAndView modelAndView = null;
        try {
            modelAndView = new ModelAndView(pageService.getPath(Util.getTag("main")));
        } catch (AURException ex) {
            log.error("{} not found page for main",methodName);
            if (ErrorCodes.ERROR_400.equals(ex.getErrorCodes())) {
                return new ModelAndView(badRequestTemplate);
            }
        }
        return modelAndView;
    }

    @GetMapping(URIConstants.TAG)
    public ModelAndView page(@PathVariable(name = "tag") String tag) {
        final String methodName = Util.getMethodName("page");
        log.info("{} Received a call at page={}",methodName,tag);
        ModelAndView modelAndView = null;
        final String tagToUse=Util.getTag(tag);
        try {
            modelAndView = new ModelAndView(pageService.getPath(tagToUse));
            modelAndView.addObject("ciao",new Object());
        } catch (AURException ex) {
            log.error("{} not found page={}",methodName,tag);
            if (ErrorCodes.ERROR_400.equals(ex.getErrorCodes())) {
                return new ModelAndView(badRequestTemplate);
            }
        }
        return modelAndView;
    }



    @PostMapping("/files/upload")
    public String uploadFile(@RequestParam(value = "legends") MultipartFile legends,
                           @RequestParam(value = "legendsPlus",required = false) MultipartFile legendsPlus) throws IOException {
        dataService.analyzeData(legends, DataType.LEGEND);
        Optional.ofNullable(legendsPlus).ifPresent(data-> {
            try {
                dataService.analyzeData(data,DataType.LEGEND_PLUS);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return "redirect:/";
    }
}
