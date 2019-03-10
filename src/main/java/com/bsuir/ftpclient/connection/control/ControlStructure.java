package com.bsuir.ftpclient.connection.control;

public class ControlStructure {
    private String request;
    private String response;

    public ControlStructure(String request, String response) {
        this.request = request;
        this.response = response;
    }

    public String getRequest() {
        return request;
    }

    public String getResponse() {
        return response;
    }
}
