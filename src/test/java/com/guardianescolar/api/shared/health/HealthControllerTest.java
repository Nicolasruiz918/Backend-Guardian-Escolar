package com.guardianescolar.api.shared.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class HealthControllerTest {

    @Test
    void healthReturnsServiceStatus() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("version", "test");
        BuildProperties buildProperties = new BuildProperties(properties);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HealthController(buildProperties)).build();

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("guardian-escolar-api"))
                .andExpect(jsonPath("$.version").value("test"));
    }
}
