package it.skarafaz.download.controller;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @Value("${app.name}")
    private String appName;
    @Value("${app.version}")
    private String appVersion;
    @Value("${app.url}")
    private String appUrl;

    @GetMapping("/")
    public String main(Map<String, Object> model, Locale locale) {
        model.put("locale", locale.getLanguage());
        model.put("appName", appName);
        model.put("appVersion", appVersion);
        model.put("appUrl", appUrl);

        return "main";
    }
}
