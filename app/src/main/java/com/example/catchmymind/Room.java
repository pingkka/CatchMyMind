package com.example.catchmymind;

public class Room {
    public String roomName;
    public String roomNumofPeo;
    public String roomId;

    public Room(String roomName, String roomNumofPeo, String roomId) {
        this.roomName = roomName;
        this.roomNumofPeo = roomNumofPeo;
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomNumofPeo() {
        return roomNumofPeo;
    }

    public String getRoomId() {
        return roomId;
    }
}
