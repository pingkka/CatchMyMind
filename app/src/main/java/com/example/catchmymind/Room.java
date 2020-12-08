package com.example.catchmymind;


public class Room {

    private String presenter; // 출제자
    private String roomName; // 방이름
    private String maxNumofPeo; // 최대 인원수
    private String roomNumofPeo; // 인원수
    private String roomId; // 방아이디

    public Room(String presenter, String roomName, String maxNumofPeo, String roomId) {
        this.presenter = presenter;
        this.roomName = roomName;
        this.maxNumofPeo = maxNumofPeo;
        this.roomId = roomId;
    }

    public String getPresenter() {
        return presenter;
    }

    public void setPresenter(String presenter) {
        this.presenter = presenter;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMaxNumofPeo() { return maxNumofPeo; }

    public void setMaxNumofPeo(String maxNumofPeo) { this.maxNumofPeo = maxNumofPeo; }

    public String getRoomNumofPeo() { return roomNumofPeo; }

    public void setRoomNumofPeo(String roomNumofPeo) { this.roomNumofPeo = roomNumofPeo; }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

}
