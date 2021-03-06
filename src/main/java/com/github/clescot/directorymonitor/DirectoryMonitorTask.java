package com.github.clescot.directorymonitor;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.clescot.directorymonitor.DirectoryMonitorTaskConfig.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.time.Instant.EPOCH;

public class DirectoryMonitorTask extends SourceTask {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryMonitorTask.class);

    public static final String CREATE_EVENT = "C";
    public static final String DELETE_EVENT = "D";
    public static final String MODIFY_EVENT = "M";
    public static final String ALL_KINDS = "CDM";
    public static final String DIRECTORY_SEPARATOR = ";";
    private WatchService watchService;
    private AtomicBoolean stop;
    private Map<WatchKey,DirectoryMonitor> watchKeys = Maps.newHashMap();
    private String topic;
    @Override
    public String version() {
        return null;
    }

    /**
     * create a new {@link WatchService}, and register with it for each directory watched, a watch key.
     * Each watch key is linked with a {@link DirectoryMonitor} which host the watch configuration.
     * @param map
     */
    @Override
    public void start(Map<String, String> map) {
        if (context == null) {
            throw new IllegalStateException("sourceTaskContext is null");
        }
        topic = map.get(TOPIC);
        if (topic == null) {
            throw new IllegalStateException("topic is null");
        }
        DirectoryMonitorTaskConfig config = new DirectoryMonitorTaskConfig(map);
        Map<Map<String, String>, Map<String, Object>> offsets = null;
        List<Map<String, String>> partitions = new ArrayList<>();
        try {
            final FileSystem fileSystem = FileSystems.getDefault();
            watchService = fileSystem.newWatchService();
            final List<String> directories = Arrays.asList(config.getString(DIRECTORIES).split(DIRECTORY_SEPARATOR));
            directories.forEach(directoryPath -> {
                final List<String> params = Arrays.asList(directoryPath.split(","));
                if (params.isEmpty()) {
                    throw new IllegalArgumentException("one argument is needed for the directory path");
                }
                String directory = params.get(0);
                Path path = Paths.get(directory);
                String pathMatcherAsString = null;
                PathMatcher pathMatcher;
                if (params.size() > 1) {
                    pathMatcherAsString = params.get(1);

                }
                pathMatcherAsString = pathMatcherAsString!=null?"regex:"+pathMatcherAsString:"regex:.*";
                pathMatcher = fileSystem.getPathMatcher(pathMatcherAsString);
                String kindsAsString =null;
                if (params.size() > 2) {
                    kindsAsString = params.get(2);
                }
                kindsAsString = MoreObjects.firstNonNull(kindsAsString,"CMD");
                WatchEvent.Kind[] kinds = getKinds(kindsAsString);
                WatchKey watchKey;
                //we get a watchKey for the directory with the watchService
                try {
                    watchKey = path.register(watchService, kinds);
                    final DirectoryMonitor directoryMonitor = new DirectoryMonitor(path,pathMatcher, kinds);
                    watchKeys.put(watchKey,directoryMonitor);
                } catch (IOException e) {
                    throw new ConnectException(e);
                }
            });

        } catch (IOException e) {
            throw new ConnectException("watchService cannot be created", e);
        }

        offsets = context.offsetStorageReader().offsets(partitions);
        stop = new AtomicBoolean(false);
    }


    private WatchEvent.Kind[] getKinds(String kinds) {
        String attributes = kinds;
        if (kinds == null || kinds.isEmpty()) {
            attributes = ALL_KINDS;
        }
        List<WatchEvent.Kind> list = Lists.newArrayList();
        if (attributes.contains(CREATE_EVENT)) {
            list.add(ENTRY_CREATE);
        }
        if (attributes.contains(DELETE_EVENT)) {
            list.add(ENTRY_DELETE);
        }
        if (attributes.contains(MODIFY_EVENT)) {
            list.add(ENTRY_MODIFY);
        }
        return list.toArray(new WatchEvent.Kind[list.size()]);
    }

    /**
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        return getSourceRecords(watchService,stop);
    }

    protected List<SourceRecord> getSourceRecords(WatchService watchService,AtomicBoolean stop) {
        List<SourceRecord> records = Lists.newArrayList();

        while (!stop.get()) {
            WatchKey key;
            try {
                // wait for a key to be available
                key = watchService.take();
            } catch (InterruptedException ex) {
                return records;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                // get event type
                WatchEvent.Kind<?> kind = event.kind();

                // get file name
                @SuppressWarnings("unchecked")
                WatchEvent<Path> watchEvent = (WatchEvent<Path>) event;
                Path path = watchEvent.context();
                if (kind == OVERFLOW) {
                    continue;
                }
                final DirectoryMonitor directoryMonitor = watchKeys.get(key);
                if (isWatched(directoryMonitor.getPathMatcher(), directoryMonitor.getKinds(), watchEvent)) {
                    records.add(extractSourceRecord(directoryMonitor.getDirectoryPath(),watchEvent));
                }
                logger.debug(kind.name() + ": " + path);

            }

            // IMPORTANT: The key must be reset after processed
            boolean valid = key.reset();
//            if (!valid) {
                break;
//            }
        }
        return records;
    }

    private Timestamp getLastRecordedOffset(Map<String, Object> partition) {
        Map<String, Object> offset = context.offsetStorageReader().offset(partition);
        Timestamp lastRecordedOffset = Timestamp.from(EPOCH);
        if (offset != null) {
            lastRecordedOffset = new Timestamp((Long) offset.getOrDefault(POSITION, Timestamp.from(EPOCH)));
        }
        return lastRecordedOffset;
    }

    protected SourceRecord extractSourceRecord(Path directoryPath,WatchEvent<Path> event) {
        final String uri = event.context().toUri().toString();
        Map<String, ?> sourcePartition = Collections.singletonMap(FILE, uri);
        final long lastModified = event.context().toFile().lastModified();
        final long mySourceOffset = lastModified != 0 ? lastModified : System.currentTimeMillis();
        Map<String, ?> sourceOffset = Collections.singletonMap(POSITION, mySourceOffset);
        Object value = uri + ";;" + event.kind().name() + ";;" + mySourceOffset;
        return new SourceRecord(sourcePartition, sourceOffset, topic, Schema.STRING_SCHEMA, value);
    }

    protected boolean isWatched(PathMatcher pathMatcher, WatchEvent.Kind<Path>[] kindsWanted, WatchEvent<Path> event) {
        final Path context = event.context();
        final WatchEvent.Kind<Path> kind = event.kind();
        final int count = event.count();
        final long lastModified = context.toFile().lastModified();

        if (!pathMatcher.matches(context)) {
            logger.debug("file {} does not match pathMatcher", context.toFile().getAbsolutePath());
            return false;
        }
        if (!Arrays.asList(kindsWanted).contains(kind)) {
            logger.debug("file {} does not match kindsWanted {}", context.toFile().getAbsolutePath(), kindsWanted);
            return false;
        }
        return true;
    }


    @Override
    public void stop() {
        if (stop != null) {
            stop.set(true);
        }
        watchKeys.keySet().forEach(WatchKey::cancel);
    }
}
