package it.skarafaz.download.controller;

import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import it.skarafaz.download.service.MainService;

@Controller
public class MainController {
    @Autowired
    private MainService mainService;

    @GetMapping("/")
    public String main(Map<String, Object> model, Locale locale) {
        this.mainService.fillTemplateModel(model, locale);

        return "main";
    }
}
