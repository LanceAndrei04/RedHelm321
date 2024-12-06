package com.example.redhelm321.connect_nearby;
import java.io.Serializable;

public class ChatMessage implements Serializable {
    public final static String TYPE_COMMAND_FRIEND_REQUEST = "Command_FRIEND_REQUEST";
    public final static String TYPE_COMMAND_DISCONNECT = "Command_DISCONNECT";
    public final static String TYPE_MESSAGE = "MESSAGE";

    private String senderName;
    private String senderId;
    private String message;
    private String type;
    private long timestamp;
    private String recipient; // Optional field

    // Private constructor to enforce the use of Builder
    private ChatMessage(Builder builder) {
        this.senderName = builder.senderName;
        this.senderId = builder.senderId;
        this.message = builder.message;
        this.timestamp = builder.timestamp;
        this.recipient = builder.recipient;
        this.type = builder.type == null ? TYPE_MESSAGE : builder.type;
    }

    public String getType() {
        return type;
    }

    // Getters
    public String getSenderName() {
        return senderName;
    }

    public String getSenderId() {
        return senderId;
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
                "sender='" + senderName + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", recipient='" + recipient + '\'' +
                '}';
    }

    // Builder Class
    public static class Builder {
        private String senderName;      // Required
        private String senderId;      // Required
        private String message;     // Required
        private long timestamp;     // Optional, default to current time
        private String recipient;   // Optional
        private String type;        // Optional, default to TYPE_MESSAGE

        public Builder(String senderId, String senderName, String message) {
            this.senderId = senderId;
            this.senderName = senderName;
            this.message = message;
            this.timestamp = System.currentTimeMillis(); // Default value
        }
        public Builder setType(String type) {
            this.type = type;
            return this;
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
