package it.skarafaz.download.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.skarafaz.download.configuration.AppProperties;
import it.skarafaz.download.exception.IncomingFileNotFoundException;
import it.skarafaz.download.exception.IncomingFileNotSharedException;
import it.skarafaz.download.model.DownloadType;
import it.skarafaz.download.model.OnDemandListResponse;
import it.skarafaz.download.model.Sort;
import it.skarafaz.download.model.entity.IncomingFile;
import it.skarafaz.download.repository.IncomingFileRepository;

@Service
@Transactional
public class IncomingFileService {
    @Autowired
    private IncomingFileRepository incomingFileRepository;
    @Autowired
    private AppProperties appProperties;

    public OnDemandListResponse<IncomingFile> list(Integer start, Integer count, String sort, Boolean showHidden,
            String search) {
        return new OnDemandListResponse<>(
                this.incomingFileRepository.list(start, count, new Sort(sort), showHidden, search),
                this.incomingFileRepository.count(showHidden, search));
    }

    public void updateHidden(List<Long> ids, Boolean hidden) {
        this.incomingFileRepository.updateHidden(ids, hidden);
    }

    public void updateFeed(List<Long> ids, Boolean feed) {
        this.incomingFileRepository.updateFeed(ids, feed);
    }

    public void updateShared(List<Long> ids, Boolean shared) {
        this.incomingFileRepository.updateShared(ids, shared);
    }

    public void download(Long id, DownloadType type, HttpServletRequest request, HttpServletResponse response) {
        IncomingFile incomingFile = this.incomingFileRepository.findOne(id);

        if (incomingFile == null) {
            throw new IncomingFileNotFoundException(id);
        }

        if (type == DownloadType.shared && incomingFile.getShared() == false) {
            throw new IncomingFileNotSharedException(id);
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

            if (type == DownloadType.feed) {
                incomingFile.setFeed(false);
            }
        } catch (ClientAbortException e) {
            // ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    public void feed(HttpServletResponse response) {
        List<IncomingFile> incomingFiles = this.incomingFileRepository.findByFeed(true);

        String content = "";
        for (int i = 0; i < incomingFiles.size(); i++) {
            IncomingFile incomingFile = incomingFiles.get(i);

            content += String.format("%sfile/download/%s/%d?%s", appProperties.getUrl(), DownloadType.feed.name(),
                    incomingFile.getId(), incomingFile.getPath());

            if (i != incomingFiles.size() - 1) {
                content += "\n";
            }
        }

        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"feed.txt\"");
        response.setHeader("Content-Length", new Long(content.length()).toString());

        OutputStream out = null;

        try {
            out = response.getOutputStream();
            IOUtils.write(content, out, "UTF-8");
        } catch (ClientAbortException e) {
            // ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
