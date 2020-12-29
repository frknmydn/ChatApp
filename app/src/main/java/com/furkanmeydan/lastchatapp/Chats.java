package com.furkanmeydan.lastchatapp;

public class Chats {

    String inboxKey;
    String gonderenUid;
    String mesaj;

    public Chats(String inboxKey, String gonderenUid, String mesaj) {
        this.inboxKey = inboxKey;
        this.gonderenUid = gonderenUid;
        this.mesaj = mesaj;
    }

    public Chats(){

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

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
}


