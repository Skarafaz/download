package it.skarafaz.download.model.db;

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
    private Boolean hidden = false;

    public IncomingFile() {
    }

    public IncomingFile(String path) {
        this.path = path;
    }

    @Id
    @GenericGenerator(name = "genId", strategy = "it.skarafaz.download.hibernate.SequenceGenerator")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genId")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @Column(nullable = false)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NotNull
    @Column(nullable = false)
    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
