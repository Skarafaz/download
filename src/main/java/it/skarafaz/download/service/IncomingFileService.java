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

    public OnDemandListResponse<IncomingFile> list(Integer start, Integer count, String sort, Boolean showHidden) {
        return new OnDemandListResponse<>(this.incomingFileRepository.list(start, count, new Sort(sort), showHidden),
                this.incomingFileRepository.count(showHidden));
    }

    public void hide(List<Long> ids) {
        this.incomingFileRepository.updateHidden(ids, true);
    }

    public void show(List<Long> ids) {
        this.incomingFileRepository.updateHidden(ids, false);
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
