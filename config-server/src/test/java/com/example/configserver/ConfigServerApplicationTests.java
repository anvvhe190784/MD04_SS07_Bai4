package com.example.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "server.port=0",
    "spring.cloud.config.server.git.uri=https://github.com/anvvhe190784/MD04_SS07_Bai4",
    "spring.cloud.config.server.git.search-paths=medical-config-repo",
    "spring.cloud.config.server.git.default-label=main"
})
class ConfigServerApplicationTests {

    @Test
    void contextLoads() {
    }

}