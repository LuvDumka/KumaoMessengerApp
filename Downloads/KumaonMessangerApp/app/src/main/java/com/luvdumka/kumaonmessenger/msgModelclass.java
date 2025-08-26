//package com.luvdumka.kumaonmessenger;
//
//public class msgModelclass {
//    String message;
//    String senderid;
//    long timeStamp;
//
//    public msgModelclass() {
//    }
//
//    public msgModelclass(String message, String senderid, long timeStamp) {
//        this.message = message;
//        this.senderid = senderid;
//        this.timeStamp = timeStamp;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public String getSenderid() {
//        return senderid;
//    }
//
//    public void setSenderid(String senderid) {
//        this.senderid = senderid;
//    }
//
//    public long getTimeStamp() {
//        return timeStamp;
//    }
//
//    public void setTimeStamp(long timeStamp) {
//        this.timeStamp = timeStamp;
//    }
//}


package com.luvdumka.kumaonmessenger;

public class msgModelclass {
    String message;
    String senderid;
    long timeStamp;
    String messageStatus; // "sent", "delivered", "read"
    boolean isDelivered;
    boolean isRead;
    String reaction; // "❤️", "👍", "😂", "😮", "😢", "😡", null for no reaction
    String reactionBy; // User ID who reacted

    public msgModelclass() {
        this.messageStatus = "sent"; // Default status
        this.isDelivered = false;
        this.isRead = false;
    }

    public msgModelclass(String message, String senderid, long timeStamp) {
        this.message = message;
        this.senderid = senderid;
        this.timeStamp = timeStamp;
        this.messageStatus = "sent"; // Default status when message is created
        this.isDelivered = false;
        this.isRead = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        this.isDelivered = delivered;
        if (delivered && !"read".equals(messageStatus)) {
            this.messageStatus = "delivered";
        }
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
        if (read) {
            this.messageStatus = "read";
            this.isDelivered = true; // If read, it's automatically delivered
        }
    }

    // Reaction methods
    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getReactionBy() {
        return reactionBy;
    }

    public void setReactionBy(String reactionBy) {
        this.reactionBy = reactionBy;
    }

    public boolean hasReaction() {
        return reaction != null && !reaction.isEmpty();
    }
}
