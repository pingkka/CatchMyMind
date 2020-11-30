package com.example.catchmymind;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MySocket extends Socket {

    private static MySocket socket;

    private String userName;
    private String roomID;
    //private static final String ip_addr = "10.0.2.2"; // Emulator PC의 127.0.0.1
    //private static final String ip_addr = "192.168.123.100"; // 실제 Phone으로 테스트 할 때 설정. 경진
    private static final String ip_addr = "192.168.0.4"; // 실제 Phone으로 테스트 할 때 설정. 수연
    private static final int port_no = 30000;

    private static ObjectInputStream myOis;
    private static ObjectOutputStream myOos;

    public MySocket(String host, int port) throws IOException, UnknownHostException {
        super(host, port);
    }

    public ObjectOutputStream getMyOos() {
        return myOos;
    }

    public ObjectInputStream getMyOis() {
        return myOis;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public synchronized static MySocket getInstance() throws IOException {
        if (socket == null) {
            synchronized (MySocket.class) {
                if (socket == null) {
                    socket = new MySocket(ip_addr, port_no);
                    myOos = new ObjectOutputStream(socket.getOutputStream());
                    myOis = new ObjectInputStream(socket.getInputStream());
                }
            }
        }
        return socket;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return super.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return super.getOutputStream();
    }
}
