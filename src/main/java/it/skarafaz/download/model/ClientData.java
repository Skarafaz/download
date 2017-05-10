package it.skarafaz.download.model;

import java.util.Map;

import it.skarafaz.download.configuration.AppPropertiesConfiguration;

public class ClientData {
    private AppPropertiesConfiguration properties;
    private Map<String, String> messages;

    public AppPropertiesConfiguration getProperties() {
        return properties;
    }

    public void setProperties(AppPropertiesConfiguration properties) {
        this.properties = properties;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
}
