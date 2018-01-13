package it.skarafaz.download.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.skarafaz.download.model.DownloadType;
import it.skarafaz.download.model.OnDemandListResponse;
import it.skarafaz.download.model.entity.IncomingFile;
import it.skarafaz.download.service.IncomingFileService;

@Controller
@RequestMapping("/file")
public class IncomingFileController {
    @Autowired
    private IncomingFileService incomingFileService;

    @GetMapping("/list")
    public @ResponseBody OnDemandListResponse<IncomingFile> list(@RequestParam Integer start,
            @RequestParam Integer count, @RequestParam String sort, @RequestParam Boolean showHidden,
            @RequestParam String search) {
        return this.incomingFileService.list(start, count, sort, showHidden, search);
    }

    @PostMapping("/hide")
    public @ResponseBody Boolean hide(@RequestBody List<Long> ids) {
        this.incomingFileService.hide(ids);
        return true;
    }

    @PostMapping("/show")
    public @ResponseBody Boolean show(@RequestBody List<Long> ids) {
        this.incomingFileService.show(ids);
        return true;
    }

    @PostMapping("/add")
    public @ResponseBody Boolean add(@RequestBody List<Long> ids) {
        this.incomingFileService.add(ids);
        return true;
    }

    @PostMapping("/remove")
    public @ResponseBody Boolean remove(@RequestBody List<Long> ids) {
        this.incomingFileService.remove(ids);
        return true;
    }

    @GetMapping("/download/{type}/{id}")
    public void download(@PathVariable DownloadType type, @PathVariable Long id, HttpServletRequest request,
            HttpServletResponse response) {
        this.incomingFileService.download(id, type, request, response);
    }

    @GetMapping("/feed")
    public void feed(HttpServletResponse response) {
        this.incomingFileService.feed(response);
    }
}
