package com.example.catchmymind;

// ChatMsg.java 채팅 메시지 ObjectStream 용.
public class ChatMsg {
    public String code;
    public String userName;
    public String data;
    public byte[] imgbytes;

    public String roomName; // 방이름
    public String roomNumofPeo; // 인원수

    public String roomId; // 방아이디

    public String quiz; // 문제

    public String itemName; // Item이름
    public String penColor; // 펜 색상

    public ChatMsg() {}

    public ChatMsg(String userName, String code, String msg) {
        this.code = code;
        this.userName = userName;
        this.data = msg;
    }
}