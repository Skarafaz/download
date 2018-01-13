package it.skarafaz.download.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Configuration
@ConfigurationProperties("app")
public class AppProperties {
    private String name;
    private String version;
    private String url;
    private String watchDirectory;
    private String noFeedDirectoryName;

    public AppProperties() {
    }

    public AppProperties(AppProperties props) {
        this.name = props.getName();
        this.version = props.getVersion();
        this.url = props.getUrl();
        this.watchDirectory = props.getWatchDirectory();
        this.noFeedDirectoryName = props.getNoFeedDirectoryName();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWatchDirectory() {
        return this.watchDirectory;
    }

    public void setWatchDirectory(String watchDirectory) {
        this.watchDirectory = watchDirectory;
    }

    public String getNoFeedDirectoryName() {
        return noFeedDirectoryName;
    }

    public void setNoFeedDirectoryName(String noFeedDirectoryName) {
        this.noFeedDirectoryName = noFeedDirectoryName;
    }

    @JsonIgnore
    public Path getWatchDirectoryAsPath() {
        return Paths.get(this.watchDirectory);
    }

    @JsonIgnore
    public AppProperties getThis() {
        return new AppProperties(this);
    }
}
