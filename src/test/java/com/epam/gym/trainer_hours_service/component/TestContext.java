package com.epam.gym.trainer_hours_service.component;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
public class TestContext {

    private MvcResult result;
    private String expectedAccessToken;
    private String expectedErrorMessageDetail;

    public void reset() {
        this.result = null;
        this.expectedAccessToken = null;
        this.expectedErrorMessageDetail = null;
    }

    public MvcResult getResult() {
        return result;
    }

    public void setResult(MvcResult result) {
        this.result = result;
    }

    public String getExpectedAccessToken() {
        return expectedAccessToken;
    }

    public void setExpectedAccessToken(String expectedAccessToken) {
        this.expectedAccessToken = expectedAccessToken;
    }

    public String getExpectedErrorMessageDetail() {
        return expectedErrorMessageDetail;
    }

    public void setExpectedErrorMessageDetail(String expectedErrorMessageDetail) {
        this.expectedErrorMessageDetail = expectedErrorMessageDetail;
    }
}
