package it.skarafaz.download.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.skarafaz.download.service.IncomingFileService;

@Controller
@RequestMapping("/file")
public class IncomingFileController {
    @Autowired
    private IncomingFileService service;

    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public void download(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        service.download(id, request, response);
    }
}
