package com.furkanmeydan.lastchatapp;

public class ChatInbox {
    String inboxKey;
    String gonderenUid;
    String aliciUid;
    String okundu;

    public ChatInbox(String inboxKey, String gonderenUid, String aliciUid, String okundu) {
        this.inboxKey = inboxKey;
        this.gonderenUid = gonderenUid;
        this.aliciUid = aliciUid;
        this.okundu = okundu;
    }

    public ChatInbox(){

    }

    public String getInboxKey() {
        return inboxKey;
    }

    public void setInboxKey(String inboxKey) {
        this.inboxKey = inboxKey;
    }

    public String getGonderenUid() {
        return gonderenUid;
    }

    public void setGonderenUid(String gonderenUid) {
        this.gonderenUid = gonderenUid;
    }

    public String getAliciUid() {
        return aliciUid;
    }

    public void setAliciUid(String aliciUid) {
        this.aliciUid = aliciUid;
    }

    public String getOkundu() {
        return okundu;
    }

    public void setOkundu(String okundu) {
        this.okundu = okundu;
    }
}
