package it.skarafaz.download.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "incoming_file_path_key", columnNames = { "path" }) })
public class IncomingFile {
    private Long id;
    private String path;
    private Boolean feed = true;
    private Boolean shared = false;
    private Boolean hidden = false;

    public IncomingFile() {
    }

    public IncomingFile(String path, Boolean feed, Boolean shared, Boolean hidden) {
        this.path = path;
        this.feed = feed;
        this.shared = shared;
        this.hidden = hidden;
    }

    @Id
    @GenericGenerator(name = "genId", strategy = "it.skarafaz.download.hibernate.SequenceGenerator")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genId")
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @Column(nullable = false)
    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NotNull
    @Column(nullable = false)
    public Boolean getFeed() {
        return this.feed;
    }

    public void setFeed(Boolean feed) {
        this.feed = feed;
    }

    @NotNull
    @Column(nullable = false)
    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    @NotNull
    @Column(nullable = false)
    public Boolean getHidden() {
        return this.hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
