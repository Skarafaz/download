package it.skarafaz.download.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    private static final int BUFFER_SIZE = 4096;
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

    public void download(Long id, HttpServletRequest request, HttpServletResponse response) {
        IncomingFile incomingFile = this.incomingFileRepository.findOne(id);

        if (incomingFile == null) {
            throw new IncomingFileNotFoundException(id);
        }

        incomingFile.setHidden(true);

        try {
            File file = new File(this.appProperties.getWatchDirectory(), incomingFile.getPath());

            FileInputStream inputStream;
            inputStream = new FileInputStream(file);

            String mimeType = request.getServletContext().getMimeType(file.getAbsolutePath());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            response.setContentLength((int) file.length());

            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
            response.setHeader(headerKey, headerValue);

            OutputStream outStream = response.getOutputStream();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
