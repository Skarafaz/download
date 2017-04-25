package it.skarafaz.download.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import it.skarafaz.download.util.DirectoryWatcher;
import it.skarafaz.download.util.DirectoryWatcher.OnCreateListener;
import it.skarafaz.download.util.DirectoryWatcher.OnDeleteListener;

@Service
public class FileSystemMonitorService implements ApplicationRunner, OnCreateListener, OnDeleteListener {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemMonitorService.class);

    @Autowired
    private TaskExecutor executor;

    @Value("${application.watch-directory}")
    private String watchDirectory;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        DirectoryWatcher watcher = new DirectoryWatcher(Paths.get(this.watchDirectory));
        watcher.setOnCreateListener(this);
        watcher.setOnDeleteListener(this);

        this.executor.execute(watcher);
    }

    @Override
    public void onCreate(Path path) {
        logger.debug("File system entry created: {}", path);

        // TODO
    }

    @Override
    public void onDelete(Path path) {
        logger.debug("File system entry deleted: {}", path);

        // TODO
    }
}
