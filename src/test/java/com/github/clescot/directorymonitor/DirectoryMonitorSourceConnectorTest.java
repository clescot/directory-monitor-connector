package com.github.clescot.directorymonitor;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.kafka.common.config.ConfigException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.clescot.directorymonitor.DirectoryMonitorConnectorConfig.DIRECTORIES;

@RunWith(Enclosed.class)
public class DirectoryMonitorSourceConnectorTest {


    public static class TestStart{

        @Test(expected = IllegalArgumentException.class)
        public void test_null(){
            DirectoryMonitorSourceConnector connector= new DirectoryMonitorSourceConnector();
            connector.start(null);
        }
        @Test(expected = ConfigException.class)
        public void test_empty(){
            DirectoryMonitorSourceConnector connector= new DirectoryMonitorSourceConnector();
            connector.start(Maps.newHashMap());
        }

        @Test
        public void test_nominal(){
            DirectoryMonitorSourceConnector connector= new DirectoryMonitorSourceConnector();
            final HashMap<String, String> properties = Maps.newHashMap();
            properties.put(DIRECTORIES, Files.createTempDir().getAbsolutePath());
            connector.start(properties);
        }
    }

    public static class TestTaskConfigs{
        @Test(expected = IllegalArgumentException.class)
        public void test_zero(){
            DirectoryMonitorSourceConnector connector= new DirectoryMonitorSourceConnector();
            final List<Map<String, String>> maps = connector.taskConfigs(0);
        }

    }
}