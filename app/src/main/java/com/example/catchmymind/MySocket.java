package com.example.catchmymind;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class MySocket implements Serializable {

    public String userName;
//    private Socket socket;
    public ObjectInputStream ois;
    public ObjectOutputStream oos;

    public MySocket(String userName, ObjectInputStream ois, ObjectOutputStream oos) {
        this.userName = userName;
        this.ois = ois;
        this.oos = oos;
    }

//    public MySocket(String userName, Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
//        this.userName = userName;
//        this.socket = socket;
//        this.ois = ois;
//        this.oos = oos;
//    }

//    public String getUserName() {
//        return userName;
//    }
//
////    public Socket getSocket() {
////        return socket;
////    }
//
//    public ObjectInputStream getOis() {
//        return ois;
//    }
//
//    public ObjectOutputStream getOos() {
//        return oos;
//    }
}
