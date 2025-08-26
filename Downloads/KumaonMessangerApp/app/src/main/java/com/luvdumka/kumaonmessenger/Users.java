package com.luvdumka.kumaonmessenger;

public class Users {
    String profilepic,mail,userName,password,userId,lastMessage,status;
    boolean isOnline;
    long lastSeen;

    public  Users(){}

    public Users(String userId, String userName, String maill, String password, String profilepic, String status) {
        this.userId = userId;
        this.userName = userName;
        this.mail = maill;
        this.password = password;
        this.profilepic = profilepic;
        this.status = status;
        this.isOnline = false;
        this.lastSeen = System.currentTimeMillis();
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}
