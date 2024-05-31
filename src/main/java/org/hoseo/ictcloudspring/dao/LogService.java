package org.hoseo.ictcloudspring.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {

    private final Path logDir = Paths.get("logs");
    private static final Logger logger = LogManager.getLogger(LogService.class);


    public List<String> getLogFiles() {
        logger.info("Log Service get log files");
        try {
            return Files.list(logDir)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Could not read log files", e);
        }
        return null;
    }

    public Path getLogFilePath(String fileName) {
        logger.info("Log Service get log file path: " + fileName);
        return logDir.resolve(fileName).normalize();
    }
}
