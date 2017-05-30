package it.skarafaz.download.model;

import java.util.List;

public class OnDemandListResponse<O> {
    private List<O> items;
    private Long total;

    public OnDemandListResponse(List<O> items, Long total) {
        this.items = items;
        this.total = total;
    }

    public List<O> getItems() {
        return this.items;
    }

    public long getTotal() {
        return this.total;
    }
}
