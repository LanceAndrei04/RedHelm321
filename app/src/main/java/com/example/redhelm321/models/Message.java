package com.example.redhelm321.models;

public class Message {
    private String text;
    private long timestamp;
    private boolean isSent;
    private boolean isNotification;

    public Message(String text, boolean isSent) {
        this.text = text;
        this.timestamp = System.currentTimeMillis();
        this.isSent = isSent;
    }

    public boolean isNotification(){
        return isNotification;
    }

    public void setNotification(boolean isNotification) {
        this.isNotification = isNotification;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isSent() {
        return isSent;
    }
}
