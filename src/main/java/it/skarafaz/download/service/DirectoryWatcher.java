package it.skarafaz.download.service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryWatcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);

    // @formatter:off
    private static final Kind<?>[] events = {
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.ENTRY_DELETE,
        StandardWatchEventKinds.OVERFLOW
    };
    // @formatter:on

    private final Path root;
    private final WatchService watcher;
    private final Map<WatchKey, Path> paths;

    private OnCreateListener onCreateListener;
    private OnModifyListener onModifyListener;
    private OnDeleteListener onDeleteListener;
    private OnOverflowListener onOverflowListener;

    public DirectoryWatcher(Path root) throws IOException {
        this.root = root;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.paths = new HashMap<>();

        registerAll(this.root);
    }

    public void setOnCreateListener(OnCreateListener onCreateListener) {
        this.onCreateListener = onCreateListener;
    }

    public void setOnModifyListener(OnModifyListener onModifyListener) {
        this.onModifyListener = onModifyListener;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void setOnOverflowListener(OnOverflowListener onOverflowListener) {
        this.onOverflowListener = onOverflowListener;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        logger.info("Starting directory watcher with root: {}", this.root);

        for (;;) {

            WatchKey key;
            try {
                key = this.watcher.take();
            } catch (InterruptedException e) {
                logger.info("Stopping directory watcher");
                return;
            }

            Path dir = paths.get(key);
            if (dir == null) {
                logger.warn("Watch key not recognized");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                Kind<?> kind = event.kind();

                logger.debug("Filesystem event: {}", kind);

                if (kind.equals(StandardWatchEventKinds.OVERFLOW)) {
                    if (this.onOverflowListener != null) {
                        this.onOverflowListener.onOverflow(event.context());
                    }
                    continue;
                }

                Path path = dir.resolve(((WatchEvent<Path>) event).context());

                if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                    if (this.onCreateListener != null) {
                        this.onCreateListener.onCreate(path);
                    }
                    if (Files.isDirectory(path)) {
                        registerAll(path);
                    }
                } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    if (this.onModifyListener != null) {
                        this.onModifyListener.onModify(path);
                    }
                } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    if (this.onDeleteListener != null) {
                        this.onDeleteListener.onDelete(path);
                    }
                }
            }

            if (!key.reset()) {
                logger.debug("Watch key for {} is no longer valid, unregistering directory", paths.get(key));

                this.paths.remove(key);

                if (this.paths.isEmpty()) {
                    logger.warn("Stopping watcher, no valid watch keys left");
                    break;
                }
            }
        }
    }

    private void registerAll(Path root) {
        try (Stream<Path> paths = Files.walk(root, FileVisitOption.FOLLOW_LINKS)) {
            paths.filter(p -> Files.isDirectory(p)).forEach(this::register);
        } catch (IOException e) {
            logger.error("Cannot register directory tree with root: {}", root);
            logger.error("{}: {}", e.getClass().getName(), e.getMessage());
        }
    }

    private void register(Path dir) {
        logger.debug("Registering directory: {}", dir);

        try {
            this.paths.put(dir.register(watcher, events), dir);
        } catch (IOException e) {
            logger.error("Cannot register directory: {}", dir);
            logger.error("{}: {}", e.getClass().getName(), e.getMessage());
        }
    }

    public interface OnCreateListener {

        public void onCreate(Path path);
    }

    public interface OnModifyListener {

        public void onModify(Path path);
    }

    public interface OnDeleteListener {

        public void onDelete(Path path);
    }

    public interface OnOverflowListener {

        public void onOverflow(Object context);
    }
}
