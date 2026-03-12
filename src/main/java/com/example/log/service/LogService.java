package com.example.log.service;

import com.example.log.model.LogEntry;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LogService {

    private static final int CACHE_SIZE = 100;
    private int counter = 0;

    // LRU cache with insertion order (accessOrder=false)
    private final Map<Integer, LogEntry> logCache =
            new LinkedHashMap<>(CACHE_SIZE, 0.75f, false) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, LogEntry> eldest) {
                    return size() > CACHE_SIZE;
                }
            };

    // Inverted index: word -> list of logs
    private final Map<String, List<LogEntry>> invertedIndex = new HashMap<>();

    // Add log
    public synchronized void addLog(String level, String message) {
        LogEntry log = new LogEntry(counter++, level, message, System.currentTimeMillis());
        logCache.put(log.getId(), log);
        indexLog(log);
    }

    // Build inverted index
    private void indexLog(LogEntry log) {
        String[] words = log.getMessage().toLowerCase().split("\\s+");
        for (String word : words) {
            invertedIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(log);
        }
    }

    // Return logs in insertion order
    public List<LogEntry> getAllLogs() {
        return new ArrayList<>(logCache.values());
    }

    // Search logs by keyword
    public List<LogEntry> searchLogs(String keyword) {
        return invertedIndex.getOrDefault(keyword.toLowerCase(), new ArrayList<>());
    }
}