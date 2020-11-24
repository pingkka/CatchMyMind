package com.example.catchmymind;

import java.io.IOException;
import java.net.Socket;

public class MySocket extends Socket {

    private static Socket socket;

    private String userName;
    private static String ip_addr;
    private static int port_no;

    public MySocket(String userName, String ip_addr, int port_no) {
        this.userName = userName;
        this.ip_addr = ip_addr;
        this.port_no = port_no;
    }

    public synchronized static Socket getInstance() throws IOException {
        if(socket == null) {
            synchronized (MySocket.class) {
                if(socket == null)
                    socket = new Socket(ip_addr, port_no);
            }
        }
        return socket;
    }
}
