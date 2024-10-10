package com.dwarflegends.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.config.location=./test/config.properties",
        "-Dlogback.configurationFile=./config/logback.xml"
})
class WebAppApplicationTests {

    @Test
    void contextLoads() {
    }

}
