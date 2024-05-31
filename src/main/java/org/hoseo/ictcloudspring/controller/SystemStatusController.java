package org.hoseo.ictcloudspring.controller;

import org.hoseo.ictcloudspring.dao.SystemStatusService;
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

    @GetMapping("/server")
    public ResponseEntity<ServerStatus> getServerStatus() {
        ServerStatus status = systemStatusService.getServerStatus();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/database")
    public ResponseEntity<DatabaseStatus> getDatabaseStatus() {
        DatabaseStatus status = systemStatusService.getDatabaseStatus();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/storage")
    public ResponseEntity<StorageUsage> getStorageUsage() {
        StorageUsage usage = systemStatusService.getStorageUsage();
        return ResponseEntity.ok(usage);
    }
}
