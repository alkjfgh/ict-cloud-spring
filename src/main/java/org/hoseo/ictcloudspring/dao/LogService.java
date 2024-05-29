package org.hoseo.ictcloudspring.dao;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {

    private final Path logDir = Paths.get("logs");

    public List<String> getLogFiles() {
        try {
            return Files.list(logDir)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Could not read log files", e);
        }
    }

    public Path getLogFilePath(String fileName) {
        return logDir.resolve(fileName).normalize();
    }
}
