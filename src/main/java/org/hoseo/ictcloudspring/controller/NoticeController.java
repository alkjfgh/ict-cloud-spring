package org.hoseo.ictcloudspring.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.dao.NoticeService;
import org.hoseo.ictcloudspring.dto.Notice;
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
import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    private NoticeService noticeService;
    private static final Logger logger = LogManager.getLogger(LogController.class);

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/getList")
    public ResponseEntity<List<Notice>> getNoticeList() {
        logger.info("Notice Controller get notice list");
        List<Notice> list = noticeService.getNoticeList();;
        return ResponseEntity.ok().body(list);
    }
}
