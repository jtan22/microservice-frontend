package com.bw.petclinic.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(OopsController.class)
public class OopsControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testOops() throws Exception {
        mockMvc
                .perform(get("/oops").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "Expected OOPS Exception"));
    }

    @Test
    public void testCatchAll() throws Exception {
        mockMvc
                .perform(get("/catch-all").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "Internal Error [No static resource catch-all.]"));
    }

}
