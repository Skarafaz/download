package it.skarafaz.download.model;

import java.util.Map;

import it.skarafaz.download.configuration.AppProperties;

public class ClientData {
    private AppProperties properties;
    private Map<String, String> messages;

    public AppProperties getProperties() {
        return this.properties;
    }

    public void setProperties(AppProperties properties) {
        this.properties = properties;
    }

    public Map<String, String> getMessages() {
        return this.messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
}
