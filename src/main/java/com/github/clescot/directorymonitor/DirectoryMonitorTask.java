package com.github.clescot.directorymonitor;

import org.apache.kafka.connect.connector.Task;

import java.util.Map;

public class DirectoryMonitorTask implements Task {
    @Override
    public String version() {
        return null;
    }

    @Override
    public void start(Map<String, String> map) {

    }

    @Override
    public void stop() {

    }
}
