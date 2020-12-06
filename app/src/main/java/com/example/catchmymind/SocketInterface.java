package com.example.catchmymind;

import java.io.IOException;

public interface SocketInterface {

    // Server Message 수신
    void DoReceive();

    // SendChatMsg()
    void SendChatMsg(ChatMsg cm);

    // ChatMsg 를 읽어서 Return, Java 호환성 문제로 field별로 수신해서 ChatMsg 로 만들어 Return
    ChatMsg ReadChatMsg();
}
