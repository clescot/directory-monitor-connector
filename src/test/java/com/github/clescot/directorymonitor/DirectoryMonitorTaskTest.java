package com.github.clescot.directorymonitor;

import com.google.common.collect.Maps;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;


@RunWith(Enclosed.class)
public class DirectoryMonitorTaskTest {

    public static class TestStart{
        @Test(expected = ConnectException.class)
        public void test_null() {
            DirectoryMonitorTask task = new DirectoryMonitorTask();
            task.start(null);
        }

        @Test(expected = ConfigException.class)
        public void test_empty_map() {
            DirectoryMonitorTask task = new DirectoryMonitorTask();
            task.start(Maps.newHashMap());
        }


    }
}