package com.romanpulov.piastriawss;

import com.romanpulov.piastriawss.controller.HelloController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(HelloController.class)
public class ControllerHelloTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void testHello() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/hello")
                .accept(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello from HelloController"))
        ;
    }
}
