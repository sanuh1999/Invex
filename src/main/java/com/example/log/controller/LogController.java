package com.example.log.controller;

import com.example.log.model.LogEntry;
import com.example.log.service.LogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public String addLog(@RequestParam String level,
                         @RequestParam String message) {
        logService.addLog(level, message);
        return "Log added";
    }

    // GET logs with pagination
    @GetMapping
    public List<LogEntry> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<LogEntry> allLogs = logService.getAllLogs();

        int start = page * size;
        int end = Math.min(start + size, allLogs.size());

        if (start >= allLogs.size()) {
            return List.of(); // empty page
        }

        return allLogs.subList(start, end);
    }

    @GetMapping("/search")
    public List<LogEntry> searchLogs(@RequestParam String keyword) {
        return logService.searchLogs(keyword);
    }
}