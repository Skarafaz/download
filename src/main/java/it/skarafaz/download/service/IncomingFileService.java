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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.skarafaz.download.exception.IncomingFileNotFoundException;
import it.skarafaz.download.model.db.IncomingFile;
import it.skarafaz.download.repository.IncomingFileRepository;

@Service
@Transactional
public class IncomingFileService {
    private static final int BUFFER_SIZE = 4096;

    @Autowired
    private IncomingFileRepository repo;

    @Value("${app.watch-directory}")
    private String watchDirectory;

    public Page<IncomingFile> list(String[] sort, int start, int count) {
        return repo.findAll(new PageRequest(start / count, count, createSort(sort)));
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
        IncomingFile incomingFile = repo.findOne(id);

        if (incomingFile == null) {
            throw new IncomingFileNotFoundException(id);
        }

        incomingFile.setHidden(true);

        try {
            File file = new File(watchDirectory, incomingFile.getPath());

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
