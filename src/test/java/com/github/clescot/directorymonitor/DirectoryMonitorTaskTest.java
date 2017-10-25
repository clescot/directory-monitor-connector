package com.github.clescot.directorymonitor;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(Enclosed.class)
public class DirectoryMonitorTaskTest {

    public static class TestStart{
        @Test(expected = ConnectException.class)
        public void test_null() {
            DirectoryMonitorTask task = getTask();
            task.start(null);
        }

        @Test(expected = ConfigException.class)
        public void test_empty_map() {
            DirectoryMonitorTask task = getTask();
            task.start(Maps.newHashMap());
        }



        @Test
        public void test_nominal_case() {
            DirectoryMonitorTask task = getTask();
            final Map<String, String> parameters = getNominalParameters();
            task.start(parameters);
        }

    }

    private static Map<String, String> getNominalParameters() {
        final HashMap<String, String> map = Maps.newHashMap();
        final File tempDir = Files.createTempDir();
        map.put(DirectoryMonitorTaskConfig.DIRECTORIES,tempDir.getAbsolutePath());
        return map;
    }

    private static DirectoryMonitorTask getTask() {
        DirectoryMonitorTask task = new DirectoryMonitorTask();
        SourceTaskContext context = mock(SourceTaskContext.class);
        OffsetStorageReader reader = mock(OffsetStorageReader.class);
        when(reader.offset(anyMapOf(String.class,Object.class))).thenReturn(Maps.newHashMap());
        when(context.offsetStorageReader()).thenReturn(reader);
        task.initialize(context);
        return task;
    }

    public static class TestPoll{
        @Test
        public void test_nominal_case() throws InterruptedException, IOException {
            final DirectoryMonitorTask task = getTask();
            final Map<String, String> parameters = getNominalParameters();
            final String path = parameters.get(DirectoryMonitorTaskConfig.DIRECTORIES);
            final Path directoryPath = Paths.get(path);
            task.start(parameters);
            FileScheduler.scheduleFileCreation(directoryPath);
            final List<SourceRecord> sourceRecords = task.poll();
            assertThat(sourceRecords.size()).isEqualTo(1);

        }
    }


}