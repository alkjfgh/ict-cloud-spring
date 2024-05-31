package org.hoseo.ictcloudspring.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.dao.ShareService;
import org.hoseo.ictcloudspring.dto.ShareInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/share")
public class ShareController {

    private final ShareService shareService;
    private static final Logger logger = LogManager.getLogger(ShareController.class);

    @Autowired
    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @GetMapping("/{shareId}")
    public ResponseEntity<ShareInfo> getShareInfo(@PathVariable String shareId) {
        logger.info("Share Controller get share info");
        Optional<ShareInfo> shareInfo = shareService.getShareInfo(shareId);
        return shareInfo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<String> createShare(@RequestBody ShareInfo shareInfo) {
        logger.info("Share Controller create share");
        String shareId = shareService.createShare(shareInfo);
        return ResponseEntity.ok("Share created with ID: " + shareId);
    }
}