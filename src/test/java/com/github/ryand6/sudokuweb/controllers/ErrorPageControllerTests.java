package com.github.ryand6.sudokuweb.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorPageControllerTests {

    private final MockMvc mockMvc;

    @Autowired
    public ErrorPageControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void userNotFoundPage_returnsCorrectView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/error/user-not-found"))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Expect HTTP 200
                .andExpect(MockMvcResultMatchers.view().name("error/user-not-found")); // Expect correct view name
    }

}
