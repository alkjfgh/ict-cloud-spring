package org.hoseo.ictcloudspring.controller;

import org.hoseo.ictcloudspring.dao.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping
    public List<String> getLogFiles() {
        return logService.getLogFiles();
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getLogFile(@PathVariable String fileName) throws MalformedURLException {
        Path file = logService.getLogFilePath(fileName);
        Resource resource = new UrlResource(file.toUri());
        return ResponseEntity.ok().body(resource);
    }
}
