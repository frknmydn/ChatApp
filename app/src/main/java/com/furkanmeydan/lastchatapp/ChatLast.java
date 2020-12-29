package com.furkanmeydan.lastchatapp;

public class ChatLast {

    String inboxKey;
    String mesajKey;

    public ChatLast(String inboxKey, String mesajKey) {
        this.inboxKey = inboxKey;
        this.mesajKey = mesajKey;
    }

    public ChatLast(){

    }

    public String getInboxKey() {
        return inboxKey;
    }

    public void setInboxKey(String inboxKey) {
        this.inboxKey = inboxKey;
    }

    public String getMesajKey() {
        return mesajKey;
    }

    public void setMesajKey(String mesajKey) {
        this.mesajKey = mesajKey;
    }
}
