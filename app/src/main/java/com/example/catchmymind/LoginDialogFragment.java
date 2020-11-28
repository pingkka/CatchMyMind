package com.example.catchmymind;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginDialogFragment extends DialogFragment {
    LoginResult result;

    private String userName;

    private MySocket mySocket;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public boolean LoginStatus = false;

    final String ip_addr = "10.0.2.2"; // Emulator PC의 127.0.0.1
//    final String ip_addr = ""; // 실제 Phone으로 테스트 할 때 설정.

    final int port_no = 30000;

    public LoginDialogFragment() {
    }

    public static LoginDialogFragment getInstance() {
        LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
        return loginDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_dialog, container);
        Button btn_login = (Button) view.findViewById(R.id.btn_login);
        EditText et_login = (EditText) view.findViewById(R.id.et_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인
                userName = et_login.getText().toString();
                if(!userName.equals("")) {
                    Login();
                    dismiss();
                }
                else
                    Toast.makeText(requireContext(),"입력해주세요",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void setDialogResult(LoginResult dialogResult) {
        result = dialogResult;
    }

    public interface LoginResult {
        void finish(String userName, String code);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.login_width);
        int height = getResources().getDimensionPixelSize(R.dimen.login_height);
        getDialog().getWindow().setLayout(width, height);
    }

    public void Login() {
        new Thread() {
            public void run() {
                try {
                    mySocket = new MySocket(userName, ip_addr, port_no);
                    socket = mySocket.getInstance();
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    //oos.flush();
                    ois = new ObjectInputStream(socket.getInputStream());
                    ChatMsg obj = new ChatMsg(userName,"100", "hello");
                    SendChatMsg(obj);
                    LoginStatus = true;
                    DoReceive(); // Server에서 읽는 Thread 실행
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // Server Message 수신
    public void DoReceive() {
        new Thread() {
            public void run() {
                ReadChatMsg();
            }
        }.start();
    }

    // SendChatMsg()
    public void SendChatMsg(ChatMsg cm)  {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    oos.writeObject(cm.code);
                    oos.writeObject(cm.userName);
                    oos.writeObject(cm.data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // ChatMsg 를 읽어서 Return, Java 호환성 문제로 field별로 수신해서 ChatMsg 로 만들어 Return
    public ChatMsg ReadChatMsg()  {
        ChatMsg cm = new ChatMsg("","","");
        try {
            cm.code = (String) ois.readObject();
            cm.userName = (String) ois.readObject();
            cm.data = (String) ois.readObject();
            result.finish(cm.userName, cm.code);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }
}
