package com.romanpulov.piastriawss;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ControllerMockMvcTest {

    protected MockMvc mvc;

    protected List<String> logResult = new ArrayList<>();

    protected void addResult (MvcResult mvcResult) throws UnsupportedEncodingException {
        logResult.add(mvcResult.getRequest().getRequestURI());
        logResult.add("Request:");
        try {
            mvcResult.getRequest().setCharacterEncoding(StandardCharsets.UTF_8.name());
            logResult.add(mvcResult.getRequest().getContentAsString());
        } catch (UnsupportedEncodingException e) {
            logResult.add("Unable to log request");
        }
        logResult.add("Response:");
        mvcResult.getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());
        logResult.add(mvcResult.getResponse().getContentAsString());
        logResult.add("==========================");
    }

    protected JsonMapper mapper = new JsonMapper();
    protected String json;

    protected final WebApplicationContext context;

    public ControllerMockMvcTest(WebApplicationContext context) {
        this.context = context;
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
}
