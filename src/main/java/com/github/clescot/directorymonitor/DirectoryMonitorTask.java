package com.github.clescot.directorymonitor;

import com.google.common.collect.Lists;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryMonitorTask extends SourceTask {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryMonitorTask.class);
    private WatchService watchService;
    private AtomicBoolean stop;
    private WatchKey watchKey;
    private PathMatcher pathMatcher;
    private WatchEvent.Kind[] kinds;
    @Override
    public String version() {
        return null;
    }

    @Override
    public void start(Map<String, String> map) {
        Map<Map<String, String>, Map<String, Object>> offsets = null;
        List<Map<String, String>> partitions = new ArrayList<>();
        try {
            final FileSystem fileSystem = FileSystems.getDefault();
            watchService = fileSystem.newWatchService();
            final String directoryPath = map.get("directory");
            Path path = Paths.get(directoryPath);
            pathMatcher = fileSystem.getPathMatcher(map.get("pathmatcher"));
            String kindsAsString = map.get("kinds");
            kinds = getKinds(kindsAsString);
            //we get a watchKey for the directory with the watchService
            watchKey = path.register(watchService, kinds);
        } catch (IOException e) {
            throw new ConnectException("watchService cannot be created",e);
        }
        offsets = context.offsetStorageReader().offsets(partitions);
        stop = new AtomicBoolean(false);
    }


    private WatchEvent.Kind[] getKinds(String kinds){
        String attributes = kinds;
        if(kinds==null||kinds.isEmpty()){
            attributes="CDM";
        }
        List<WatchEvent.Kind> list = Lists.newArrayList();
        if(attributes.contains("C")){
            list.add(ENTRY_CREATE);
        }
        if(attributes.contains("D")){
            list.add(ENTRY_DELETE);
        }
        if(attributes.contains("M")){
            list.add(ENTRY_MODIFY);
        }
        return list.toArray(new WatchEvent.Kind[list.size()]);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

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
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path path = ev.context();
                if (kind == OVERFLOW) {
                    continue;
                }
                if(isWatched(pathMatcher, kinds, ev)){
                    //TODO put into kafka
                }
                logger.debug(kind.name() + ": " + path);

            }

            // IMPORTANT: The key must be reset after processed
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
        return null;
    }

    private boolean isWatched(PathMatcher pathMatcher, WatchEvent.Kind<Path>[] kindsWanted, WatchEvent<Path> event) {
        final Path context = event.context();
        final WatchEvent.Kind<Path> kind = event.kind();
//        final int count = event.count();
        final long lastModified = context.toFile().lastModified();
        if(!pathMatcher.matches(context)){
            return false;
        }
        if(!Arrays.asList(kindsWanted).contains(kind)){
           return false;
        }
        return true;
    }


    @Override
    public void stop() {
        if (stop != null) {
            stop.set(true);
        }
        watchKey.cancel();
    }
}
