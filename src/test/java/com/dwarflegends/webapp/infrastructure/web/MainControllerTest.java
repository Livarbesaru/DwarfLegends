package com.dwarflegends.webapp.infrastructure.web;

import com.dwarflegends.webapp.domain.component.impl.CacheComponent;
import com.dwarflegends.webapp.domain.service.IPageService;
import com.dwarflegends.webapp.domain.service.impl.DataService;
import com.dwarflegends.webapp.util.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import static org.mockito.Mockito.when;
@ExtendWith(SpringExtension.class)
@WebMvcTest({MainController.class})
@Import({MainController.class, DataService.class, CacheComponent.class})
@TestPropertySource(
        properties = {
                "spring.config.location=./test/config.properties"
        }
)
@ImportAutoConfiguration(ThymeleafAutoConfiguration.class)
public class MainControllerTest {
    @MockBean
    private IPageService pageService;
    @Autowired
    private MockMvc mockMvc;
    private static final String MAIN_TEMPLATE = "template";

    @TestConfiguration
    static class Conf{
        @Bean
        public SpringTemplateEngine templateEngine() {
            SpringTemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(thymeleafTemplateResolver());
            return templateEngine;
        }

        @Bean
        public FileTemplateResolver thymeleafTemplateResolver() {
            FileTemplateResolver templateResolver
                    = new FileTemplateResolver();
            templateResolver.setPrefix("./test/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML5");
            return templateResolver;
        }
    }

    @Test
    void main() throws Exception {
        when(pageService.getPath(Util.getTag("main")))
                .thenReturn(MAIN_TEMPLATE); mockMvc.perform(MockMvcRequestBuilders
                .get(URIConstants.MAIN))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    void page() throws Exception {
        when(pageService.getPath(Util.getTag("main")))
                .thenReturn(MAIN_TEMPLATE);
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URIConstants.TAG.replace("{tag}","main")))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }
}
