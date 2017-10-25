package com.github.clescot.directorymonitor;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchEvent;

public class DirectoryMonitor {
    private Path directoryPath;
    private PathMatcher pathMatcher;
    private WatchEvent.Kind[] kinds;

    public DirectoryMonitor(Path directoryPath,PathMatcher pathMatcher, WatchEvent.Kind[] kinds) {
        this.directoryPath = directoryPath;
        this.pathMatcher = pathMatcher;
        this.kinds = kinds;
    }


    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public WatchEvent.Kind[] getKinds() {
        return kinds;
    }

    public Path getDirectoryPath() {
        return directoryPath;
    }
}
