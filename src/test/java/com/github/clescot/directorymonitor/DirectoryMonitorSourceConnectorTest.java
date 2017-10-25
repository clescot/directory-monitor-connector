package com.github.clescot.directorymonitor;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.kafka.common.config.ConfigException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.HashMap;

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
            final HashMap<String, String> properties = Maps.newHashMap();
            properties.put(DIRECTORIES, Files.createTempDir().getAbsolutePath());
            connector.start(properties);
            connector.taskConfigs(0);
        }

        @Test(expected = IllegalArgumentException.class)
        public void test_negative(){
            DirectoryMonitorSourceConnector connector= new DirectoryMonitorSourceConnector();
            final HashMap<String, String> properties = Maps.newHashMap();
            properties.put(DIRECTORIES, Files.createTempDir().getAbsolutePath());
            connector.start(properties);
            connector.taskConfigs(-1);
        }

        @Test(expected = IllegalStateException.class)
        public void test_task_configs_without_call_start_before(){
            DirectoryMonitorSourceConnector connector= new DirectoryMonitorSourceConnector();
            connector.taskConfigs(5);
        }

        @Test
        public void test_nominal(){
            DirectoryMonitorSourceConnector connector= new DirectoryMonitorSourceConnector();
            final HashMap<String, String> properties = Maps.newHashMap();
            final String directoryAbsolutePath = Files.createTempDir().getAbsolutePath();
            properties.put(DIRECTORIES, directoryAbsolutePath);
            connector.start(properties);
            connector.taskConfigs(5);
        }

    }
}