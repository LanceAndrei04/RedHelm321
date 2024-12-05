package com.example.redhelm321.connect_nearby;
import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String sender;
    private String message;
    private long timestamp;
    private String recipient; // Optional field

    // Private constructor to enforce the use of Builder
    private ChatMessage(Builder builder) {
        this.sender = builder.sender;
        this.message = builder.message;
        this.timestamp = builder.timestamp;
        this.recipient = builder.recipient;
    }

    // Getters
    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getRecipient() {
        return recipient;
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", recipient='" + recipient + '\'' +
                '}';
    }

    // Builder Class
    public static class Builder {
        private String sender;      // Required
        private String message;     // Required
        private long timestamp;     // Optional, default to current time
        private String recipient;   // Optional

        public Builder(String sender, String message) {
            this.sender = sender;
            this.message = message;
            this.timestamp = System.currentTimeMillis(); // Default value
        }

        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setRecipient(String recipient) {
            this.recipient = recipient;
            return this;
        }

        public ChatMessage build() {
            return new ChatMessage(this);
        }
    }
}