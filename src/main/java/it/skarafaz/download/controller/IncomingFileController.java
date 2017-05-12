package it.skarafaz.download.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.skarafaz.download.model.IncomingFileListResponse;
import it.skarafaz.download.service.IncomingFileService;

@Controller
@RequestMapping("/file")
public class IncomingFileController {
    @Autowired
    private IncomingFileService service;

    @GetMapping("/list")
    public @ResponseBody IncomingFileListResponse list(@RequestParam String[] sort, @RequestParam int start,
            @RequestParam int count) {
        return new IncomingFileListResponse(service.list(sort, start, count));
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        service.download(id, request, response);
    }
}
