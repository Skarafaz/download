package it.skarafaz.download.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.skarafaz.download.model.db.IncomingFile;
import it.skarafaz.download.repository.IncomingFileRepository;
import it.skarafaz.download.util.DirectoryWatcher;
import it.skarafaz.download.util.DirectoryWatcher.OnCreateListener;
import it.skarafaz.download.util.DirectoryWatcher.OnDeleteListener;

@Service
@Transactional
public class FileSystemMonitorService implements ApplicationRunner, OnCreateListener, OnDeleteListener {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemMonitorService.class);

    @Autowired
    private TaskExecutor executor;
    @Autowired
    private IncomingFileRepository repository;
    @Resource
    private FileSystemMonitorService thisProxy;

    @Value("${application.watch-directory}")
    private String watchDirectory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        DirectoryWatcher watcher = new DirectoryWatcher(Paths.get(this.watchDirectory));
        watcher.setOnCreateListener(thisProxy);
        watcher.setOnDeleteListener(thisProxy);

        this.executor.execute(watcher);
    }

    @Override
    public void onCreate(Path path) {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (Stream<Path> paths = Files.walk(path)) {
                paths.filter(p -> !p.getFileName().toString().startsWith(".")
                        && Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS) == false).forEach(this::saveIncomingFile);
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

        if (this.repository.findByPath(relativePath.toString()) == null) {
            logger.debug("Saving incoming file for path: {}", relativePath);
            this.repository.save(new IncomingFile(relativePath.toString()));
        }
    }

    @Override
    public void onDelete(Path path) {
        Path relativePath = relativize(path);

        logger.debug("Deleting obsolete incoming files related to path: {}", relativePath);
        FileSystemMonitorService.this.repository.deleteByPath(relativePath.toString());
        FileSystemMonitorService.this.repository.deleteDirectoryChildren(relativePath.toString() + File.separator);
    }

    private Path relativize(Path path) {
        return Paths.get(watchDirectory).relativize(path);
    }
}
