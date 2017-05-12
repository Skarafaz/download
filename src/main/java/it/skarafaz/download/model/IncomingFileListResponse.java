package it.skarafaz.download.model;

import java.util.List;

import org.springframework.data.domain.Page;

import it.skarafaz.download.model.db.IncomingFile;

public class IncomingFileListResponse {
    private List<IncomingFile> items;
    private long total;

    public IncomingFileListResponse(Page<IncomingFile> page) {
        items = page.getContent();
        total = page.getTotalElements();
    }

    public List<IncomingFile> getItems() {
        return items;
    }

    public void setItems(List<IncomingFile> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
