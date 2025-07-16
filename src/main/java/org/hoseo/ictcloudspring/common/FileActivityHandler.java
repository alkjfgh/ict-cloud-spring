// FileActivityHandler.java
package org.hoseo.ictcloudspring.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class FileActivityHandler {

    private static final Logger logger = LogManager.getLogger(FileActivityHandler.class);

    @MessageMapping("/fileActivity")
    @SendTo("/topic/fileActivity")
    public String handleFileActivity(String activity) {
        logger.info("Received file activity: " + activity);
//        return activity;
        return "{\"activity\": \"" + activity + "\"}";
    }
}
