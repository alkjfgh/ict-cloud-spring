package org.hoseo.ictcloudspring.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.dao.FileService;
import org.hoseo.ictcloudspring.dao.ShareService;
import org.hoseo.ictcloudspring.dto.File;
import org.hoseo.ictcloudspring.dto.Folder;
import org.hoseo.ictcloudspring.dto.ShareInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/share")
public class ShareController {

    private final ShareService shareService;
    private final FileService fileService;
    private static final Logger logger = LogManager.getLogger(ShareController.class);

    @Autowired
    public ShareController(ShareService shareService, FileService fileService) {
        this.shareService = shareService;
        this.fileService = fileService;
    }

    @GetMapping("/{shareId}")
    public ModelAndView enterShare(@PathVariable String shareId) {
        logger.info("Share Controller get share info");
        Optional<ShareInfo> shareInfo = shareService.getShareInfo(shareId);
        if (shareInfo.isPresent()) {
            ShareInfo info = shareInfo.get();
            if (info.getExpirationDate() != null && info.getExpirationDate().before(Date.valueOf(LocalDate.now()))) {
                return new ModelAndView("share/expired");
            }
            ModelAndView mav = new ModelAndView("share/share");
            mav.addObject("shareInfo", info);
            return mav;
        } else {
            return new ModelAndView("share/notfound");
        }
    }

    @GetMapping("/info/{shareId}")
    public ResponseEntity<Object> getShareInfo(@PathVariable String shareId) {
        logger.info("Share Controller get share info");
        Optional<ShareInfo> shareInfo = shareService.getShareInfo(shareId);
        if (shareInfo.isPresent()) {
            ShareInfo info = shareInfo.get();
            if (info.getExpirationDate() != null && info.getExpirationDate().before(Date.valueOf(LocalDate.now()))) {
                return ResponseEntity.status(410).build(); // 410 Gone 상태 코드
            }
            if (info.getPermissionType().equals("protected")) {
                ShareInfo newInfo = new ShareInfo();
                newInfo.setPermissionType("protected");
                return ResponseEntity.ok(newInfo);
            }
            logger.warn("info => " + info);
            if ("file".equals(info.getItemType())) {
                File file = fileService.getFileById(info.getItemID());
                return ResponseEntity.ok(Map.of("name", file.getFilename(), "size", file.getFileSize()));
            } else if ("folder".equals(info.getItemType())) {
                Folder folder = fileService.getFolderById(info.getItemID());
                logger.warn(folder);
                long folderSize = fileService.calculateFolderSize(new java.io.File("C://uploads/" + fileService.getStoragePath(info.getOwnerID(), folder.getFolderID())));
                return ResponseEntity.ok(Map.of("name", folder.getFolderName(), "size", folderSize));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<String> createShare(@RequestBody ShareInfo shareInfo) {
        logger.info("Share Controller create share");
        logger.info("shareInfo: " + shareInfo);
        String shareId = shareService.createShare(shareInfo);
        return ResponseEntity.ok(shareId);
    }

    @PostMapping("/checkPassword")
    public ResponseEntity<?> checkPassword(@RequestBody Map<String, String> request) {
        logger.info("Share Controller check password");
        String shareId = request.get("shareId");
        String password = request.get("password");

        logger.info("Checking password for share ID: " + shareId);
        if (shareService.checkPassword(shareId, password)) {
            Optional<ShareInfo> shareInfo = shareService.getShareInfo(shareId);
            if (shareInfo.isPresent()) {
                ShareInfo info = shareInfo.get();
                if ("file".equals(info.getItemType())) {
                    File file = fileService.getFileById(info.getItemID());
                    return ResponseEntity.ok(Map.of("name", file.getFilename(), "size", file.getFileSize()));
                } else if ("folder".equals(info.getItemType())) {
                    Folder folder = fileService.getFolderById(info.getItemID());
                    long folderSize = fileService.calculateFolderSize(new java.io.File("C://uploads/" + fileService.getStoragePath(info.getOwnerID(), folder.getFolderID())));
                    return ResponseEntity.ok(Map.of("name", folder.getFolderName(), "size", folderSize));
                }
                return ResponseEntity.ok(info);
            } else {
                return ResponseEntity.status(401).build(); // 401 Unauthorized 상태 코드
            }
        } else {
            return ResponseEntity.status(401).build(); // 401 Unauthorized 상태 코드
        }
    }

    @PostMapping("/download")
    public ResponseEntity<?> download(@RequestBody Map<String, String> request) {
        logger.info("Share Controller download");
        String shareId = request.get("shareId");
        String password = request.get("password");

        logger.info("Downloading share ID: " + shareId);
        if (shareService.checkPassword(shareId, password)) {
            Optional<ShareInfo> shareInfo = shareService.getShareInfo(shareId);
            if (shareInfo.isPresent()) {
                ShareInfo info = shareInfo.get();
                try {
                    if ("file".equals(info.getItemType())) {
                        File file = fileService.getFileById(info.getItemID());
                        logger.warn(file);
                        java.io.File realFile = new java.io.File("C://uploads/" + fileService.getStoragePath(info.getOwnerID(), file.getFolderID()) + java.io.File.separator + file.getFilename());
                        InputStreamResource resource = new InputStreamResource(new FileInputStream(realFile));

                        String encodedFilename = URLEncoder.encode(file.getFilename(), StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .contentLength(realFile.length())
                                .body(resource);
                    } else if ("folder".equals(info.getItemType())) {
                        java.io.File folder = new java.io.File("C://uploads/" + fileService.getStoragePath(info.getOwnerID(), info.getItemID()));
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        fileService.zipFolder(folder, byteArrayOutputStream);
                        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

                        String encodedFolderName = URLEncoder.encode(folder.getName() + ".zip", StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFolderName)
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .contentLength(byteArrayOutputStream.size())
                                .body(resource);
                    }
                } catch (IOException e) {
                    logger.error("Error during file download: ", e);
                    return ResponseEntity.status(500).build();
                }
            } else {
                return ResponseEntity.status(401).build(); // 401 Unauthorized 상태 코드
            }
        } else {
            return ResponseEntity.status(401).build(); // 401 Unauthorized 상태 코드
        }
        return ResponseEntity.status(401).build(); // 401 Unauthorized 상태 코드
    }

    @PostMapping("/existing")
    public ResponseEntity<ShareInfo> checkExistingShare(@RequestBody Map<String, Object> request) {
        logger.info("Share Controller create existing share");
        int ownerId = Integer.parseInt((String) request.get("ownerId"));
        int itemId = Integer.parseInt((String) request.get("itemId"));
        String itemType = (String) request.get("itemType");

        Optional<ShareInfo> existingShare = shareService.getExistingShare(ownerId, itemId, itemType);
        if (existingShare.isPresent()) {
            System.out.println("===================123=====================");
            return ResponseEntity.ok(existingShare.get());
        } else {
            System.out.println("===================321=====================");
            return ResponseEntity.status(201).build();
        }
    }

    @DeleteMapping("/stop")
    public ResponseEntity<?> stopShare(@RequestParam String shareId) {
        logger.info("Share Controller stop share");
        boolean deleted = shareService.deleteShare(shareId);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(500).build();
        }
    }
}