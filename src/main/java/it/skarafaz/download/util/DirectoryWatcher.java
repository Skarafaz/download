package it.skarafaz.download.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryWatcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);

    private final Path root;
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;

    private OnCreateListener onCreateListener;
    private OnModifyListener onModifyListener;
    private OnDeleteListener onDeleteListener;
    private OnOverflowListener onOverflowListener;

    public DirectoryWatcher(Path root) throws IOException {
        this.root = root;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();

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

            Path dir = keys.get(key);
            if (dir == null) {
                logger.warn("Watch key not recognized");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                Kind<?> kind = event.kind();

                logger.debug("Filesystem event: {}", kind);

                if (kind.equals(StandardWatchEventKinds.OVERFLOW)) {
                    if (this.onOverflowListener != null) {
                        this.onOverflowListener.onOverflow();
                    }
                    continue;
                }

                Path path = dir.resolve(((WatchEvent<Path>) event).context());

                if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                    if (this.onCreateListener != null) {
                        this.onCreateListener.onCreate(path);
                    }
                    if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                        try {
                            registerAll(path);
                        } catch (IOException e) {
                            logger.error("Cannot register directory: {}", path);
                            logger.error("{}: {}", e.getClass().getName(), e.getMessage());
                        }
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
                logger.debug("Watch key for {} is no longer valid, unregistering directory", keys.get(key));

                this.keys.remove(key);

                if (this.keys.isEmpty()) {
                    logger.warn("Stopping watcher, no valid watch keys left");
                    break;
                }
            }
        }
    }

    private void registerAll(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void register(Path dir) throws IOException {
        logger.debug("Registering directory: {}", dir);
        keys.put(dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE), dir);
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

        public void onOverflow();
    }
}
