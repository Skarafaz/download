package it.skarafaz.download.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.skarafaz.download.configuration.AppProperties;
import it.skarafaz.download.model.entity.IncomingFile;
import it.skarafaz.download.repository.IncomingFileRepository;
import it.skarafaz.download.service.DirectoryWatcher.OnCreateListener;
import it.skarafaz.download.service.DirectoryWatcher.OnDeleteListener;

@Service
@Transactional
public class FileSystemMonitorService implements ApplicationRunner, OnCreateListener, OnDeleteListener {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemMonitorService.class);
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private IncomingFileRepository incomingFileRepository;
    @Autowired
    private AppProperties appProperties;
    @Resource
    private FileSystemMonitorService proxy;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sync();

        DirectoryWatcher watcher = new DirectoryWatcher(this.appProperties.getWatchDirectoryAsPath());
        watcher.setOnCreateListener(this.proxy);
        watcher.setOnDeleteListener(this.proxy);

        this.taskExecutor.execute(watcher);
    }

    private void sync() {
        logger.info("Syncing database...");

        for (IncomingFile incomingFile : this.incomingFileRepository.findAll()) {
            File file = new File(this.appProperties.getWatchDirectory(), incomingFile.getPath());

            if (!file.canRead()) {
                logger.debug("Deleting obsolete incoming file: {}", incomingFile.getPath());
                this.incomingFileRepository.delete(incomingFile);
            }
        }

        saveAll(this.appProperties.getWatchDirectoryAsPath());
    }

    @Override
    public void onCreate(Path path) {
        if (Files.isDirectory(path)) {
            saveAll(path);
        } else {
            save(path);
        }
    }

    private void saveAll(Path dir) {
        try (Stream<Path> paths = Files.walk(dir, FileVisitOption.FOLLOW_LINKS)) {
            paths.filter(p -> !Files.isDirectory(p)).forEach(this::save);
        } catch (IOException e) {
            logger.error("{}: {}", e.getClass().getName(), e.getMessage());
        }
    }

    private void save(Path file) {
        if (!file.getFileName().toString().startsWith(".") && !file.getFileName().toString().endsWith(".meta")) {
            Path relativePath = relativize(file);

            if (this.incomingFileRepository.findByPath(relativePath.toString()) == null) {
                logger.debug("Saving incoming file for path: {}", relativePath);

                Boolean feed = !pathContainsDirectoryName(relativePath, this.appProperties.getNoFeedDirectoryName());
                Boolean shared = pathContainsDirectoryName(relativePath, this.appProperties.getSharedDirectoryName());
                Boolean hidden = pathContainsDirectoryName(relativePath, this.appProperties.getHiddenDirectoryName());

                this.incomingFileRepository.save(new IncomingFile(relativePath.toString(), feed, shared, hidden));
            }
        }
    }

    private Boolean pathContainsDirectoryName(Path path, String directoryName) {
        String pattern1 = File.separator + directoryName + File.separator;
        String pattern2 = directoryName + File.separator;
        return path.toString().contains(pattern1) || path.toString().contains(pattern2);
    }

    @Override
    public void onDelete(Path path) {
        Path relativePath = relativize(path);

        logger.debug("Deleting obsolete incoming files related to path: {}", relativePath);
        this.incomingFileRepository.deleteByPath(relativePath.toString());
        this.incomingFileRepository.deletePathContent(relativePath.toString() + File.separator);
    }

    private Path relativize(Path path) {
        return this.appProperties.getWatchDirectoryAsPath().relativize(path);
    }
}
