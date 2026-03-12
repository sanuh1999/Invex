package com.example.log;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LogApplicationTests {

	  @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddLog() throws Exception {
        mockMvc.perform(post("/logs")
                .param("level", "INFO")
                .param("message", "user login successful"))
                .andExpect(status().isOk())
                .andExpect(content().string("Log added"));
    }

    @Test
    void testGetLogs() throws Exception {
        mockMvc.perform(get("/logs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testSearchLogs() throws Exception {
        mockMvc.perform(post("/logs")
                .param("level", "INFO")
                .param("message", "user login successful"));

        mockMvc.perform(post("/logs")
                .param("level", "ERROR")
                .param("message", "user login failed"));

        mockMvc.perform(get("/logs/search")
                .param("keyword", "login"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

	@Test
void testGetLogsWithPagination() throws Exception {
    // Add 15 logs
    for (int i = 0; i < 15; i++) {
        mockMvc.perform(post("/logs")
                .param("level", "INFO")
                .param("message", "log " + i));
    }

    // Request first page (10 logs)
    mockMvc.perform(get("/logs")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(10));

    
    mockMvc.perform(get("/logs")
            .param("page", "2")
            .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
}

}
