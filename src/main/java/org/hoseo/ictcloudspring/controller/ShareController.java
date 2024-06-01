package org.hoseo.ictcloudspring.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hoseo.ictcloudspring.dao.ShareService;
import org.hoseo.ictcloudspring.dto.ShareInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Date;
import java.time.LocalDate;
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
    public ModelAndView getShareInfo(@PathVariable String shareId) {
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

    @PostMapping("/create")
    public ResponseEntity<String> createShare(@RequestBody ShareInfo shareInfo) {
        logger.info("Share Controller create share");
        logger.info("shareInfo: " + shareInfo);
        String shareId = shareService.createShare(shareInfo);
        return ResponseEntity.ok("Share created with ID: " + shareId);
    }
}