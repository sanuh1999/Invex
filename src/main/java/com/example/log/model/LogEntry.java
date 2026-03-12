package com.example.log.model;

public class LogEntry {
     private int id;
    private String level;
    private String message;
    private long timestamp;

    public LogEntry(int id, String level, String message, long timestamp) {
        this.id = id;
        this.level = level;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getLevel() { return level; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
