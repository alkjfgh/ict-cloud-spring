package org.hoseo.ictcloudspring.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.service.SystemStatusService;
import org.hoseo.ictcloudspring.dto.DatabaseStatus;
import org.hoseo.ictcloudspring.dto.ServerStatus;
import org.hoseo.ictcloudspring.dto.StorageUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system-status")
public class SystemStatusController {

    @Autowired
    private SystemStatusService systemStatusService;
    private static final Logger logger = LogManager.getLogger(SystemStatusController.class);


    @GetMapping("/server")
    public ResponseEntity<ServerStatus> getServerStatus() {
        logger.info("System Status Controller get server status");
        ServerStatus status = systemStatusService.getServerStatus();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/database")
    public ResponseEntity<DatabaseStatus> getDatabaseStatus() {
        logger.info("System Status Controller get database status");
        DatabaseStatus status = systemStatusService.getDatabaseStatus();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/storage")
    public ResponseEntity<StorageUsage> getStorageUsage() {
        logger.info("System Status Controller get storage status");
        StorageUsage usage = systemStatusService.getStorageUsage();
        return ResponseEntity.ok(usage);
    }
}
