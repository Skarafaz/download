package it.skarafaz.download.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("app")
public class AppPropertiesConfiguration {
    private String name;
    private String version;
    private String url;
    private String watchDirectory;

    public AppPropertiesConfiguration() {
    }

    public AppPropertiesConfiguration(AppPropertiesConfiguration props) {
        this.name = props.getName();
        this.version = props.getVersion();
        this.url = props.getUrl();
        this.watchDirectory = props.getWatchDirectory();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWatchDirectory() {
        return watchDirectory;
    }

    public void setWatchDirectory(String watchDirectory) {
        this.watchDirectory = watchDirectory;
    }

    @JsonIgnore
    public Path getWatchDirectoryAsPath() {
        return Paths.get(watchDirectory);
    }

    @JsonIgnore
    public AppPropertiesConfiguration getThis() {
        return new AppPropertiesConfiguration(this);
    }
}
