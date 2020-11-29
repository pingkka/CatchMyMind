package com.example.catchmymind;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.catchmymind.databinding.LoginDialogBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LoginDialogFragment extends DialogFragment implements Serializable {
    private LoginDialogBinding binding;
    LoginResult result;

    private String userName;

    private MySocket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public boolean LoginStatus = false;

    public LoginDialogFragment() {
    }

    public static LoginDialogFragment getInstance() {
        LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
        return loginDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoginDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnLogin.setOnClickListener(view1 -> {
            // 로그인
            userName = binding.etLogin.getText().toString();
            if(!userName.equals("")) {
                Login();
                dismiss();
            }
            else
                Toast.makeText(requireContext(),"입력해주세요",Toast.LENGTH_SHORT).show();
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
                    socket = MySocket.getInstance();
                    Log.d("Login: ", userName);
                    socket.setUserName(userName);
                    oos = socket.getMyOos();
                    oos.flush();
                    ois = socket.getMyOis();

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
                    oos.writeObject(cm.getCode());
                    oos.writeObject(cm.getUserName());
                    oos.writeObject(cm.getData());
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
            cm.setCode((String) ois.readObject());
            cm.setUserName((String) ois.readObject());
            cm.setData((String) ois.readObject());
            result.finish(cm.getUserName(), cm.getCode());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }
}
