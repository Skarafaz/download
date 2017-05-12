package it.skarafaz.download.model;

import java.util.List;

import org.springframework.data.domain.Page;

import it.skarafaz.download.model.entity.IncomingFile;

public class IncomingFileListResponse {
    private List<IncomingFile> items;
    private long total;

    public IncomingFileListResponse(Page<IncomingFile> page) {
        this.items = page.getContent();
        this.total = page.getTotalElements();
    }

    public List<IncomingFile> getItems() {
        return this.items;
    }

    public void setItems(List<IncomingFile> items) {
        this.items = items;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
