package it.skarafaz.download.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import it.skarafaz.download.model.db.IncomingFile;
import it.skarafaz.download.repository.IncomingFileRepository;
import it.skarafaz.download.util.DirectoryWatcher;
import it.skarafaz.download.util.DirectoryWatcher.OnCreateListener;

@Service
public class FileSystemMonitorService implements ApplicationRunner, OnCreateListener {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemMonitorService.class);

    @Autowired
    private TaskExecutor executor;
    @Autowired
    private IncomingFileRepository repository;

    @Value("${application.watch-directory}")
    private String watchDirectory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        DirectoryWatcher watcher = new DirectoryWatcher(Paths.get(this.watchDirectory));
        watcher.setOnCreateListener(this);

        this.executor.execute(watcher);
    }

    @Override
    public void onCreate(Path path) {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (Stream<Path> paths = Files.walk(path)) {
                paths.filter(p -> !p.getFileName().toString().startsWith(".") && Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS) == false)
                     .forEach(this::saveIncomingFile);
            } catch (IOException e) {
                logger.error("I/O Error");
                logger.error("{}: {}", e.getClass().getName(), e.getMessage());
            }
        } else {
            saveIncomingFile(path);
        }
    }

    private void saveIncomingFile(Path path) {
        Path relativePath = relativize(path);

        if (this.repository.findByPath(relativePath) == null) {
            logger.debug("Saving incoming file: {}", relativePath);
            this.repository.save(new IncomingFile(relativePath));
        }
    }

    private Path relativize(Path path) {
        return Paths.get(watchDirectory).relativize(path);
    }
}
