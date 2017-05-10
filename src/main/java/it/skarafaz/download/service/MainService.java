package it.skarafaz.download.service;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.skarafaz.download.configuration.AppPropertiesConfiguration;
import it.skarafaz.download.model.ClientData;
import it.skarafaz.download.spring.ExtendedMessageSource;

@Service
@Transactional
public class MainService {
    @Autowired
    private ExtendedMessageSource messageSource;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AppPropertiesConfiguration appProperties;

    public void fillTemplateModel(Map<String, Object> model, Locale locale) {
        model.put("locale", locale.getLanguage());
        model.put("title", appProperties.getName());
        model.put("data", stringifyClientData(locale));
    }

    private String stringifyClientData(Locale locale) {
        ClientData data = new ClientData();
        data.setProperties(appProperties.getThis());
        data.setMessages(messageSource.getMessages(locale));

        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
