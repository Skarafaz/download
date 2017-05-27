package it.skarafaz.download.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.skarafaz.download.configuration.AppProperties;
import it.skarafaz.download.exception.IncomingFileNotFoundException;
import it.skarafaz.download.model.entity.IncomingFile;
import it.skarafaz.download.repository.IncomingFileRepository;

@Service
@Transactional
public class IncomingFileService {
    @Autowired
    private IncomingFileRepository incomingFileRepository;
    @Autowired
    private AppProperties appProperties;

    public Page<IncomingFile> list(Boolean showHidden, String[] sort, int start, int count) {
        Page<IncomingFile> result = null;

        if (showHidden != null && showHidden == true) {
            result = this.incomingFileRepository.findAll(new PageRequest(start / count, count, createSort(sort)));
        } else {
            result = this.incomingFileRepository.findVisible(new PageRequest(start / count, count, createSort(sort)));
        }

        return result;
    }

    private Sort createSort(String[] sort) {
        List<Order> orders = new ArrayList<>();
        for (String str : sort) {
            String[] splitStr = str.split("-");
            orders.add(new Order(Direction.valueOf(splitStr[0]), splitStr[1]));
        }

        return new Sort(orders);
    }

    public void hide(List<Long> ids) {
        this.incomingFileRepository.hide(ids);
    }

    public void show(List<Long> ids) {
        this.incomingFileRepository.show(ids);
    }

    public void download(Long id, HttpServletRequest request, HttpServletResponse response) {
        IncomingFile incomingFile = this.incomingFileRepository.findOne(id);

        if (incomingFile == null) {
            throw new IncomingFileNotFoundException(id);
        }

        File file = new File(this.appProperties.getWatchDirectory(), incomingFile.getPath());

        String mime = request.getServletContext().getMimeType(file.getAbsolutePath());
        if (mime == null) {
            mime = "application/octet-stream";
        }

        response.setContentType(mime);
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
        response.setHeader("Content-Length", new Long(file.length()).toString());

        FileInputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(file);
            out = response.getOutputStream();
            IOUtils.copyLarge(in, out);
        } catch (ClientAbortException e) {
            // ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}
