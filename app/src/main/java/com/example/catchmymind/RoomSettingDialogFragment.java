package com.example.catchmymind;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RoomSettingDialogFragment extends DialogFragment {
    RoomSettingResult result;

    private String userName;

    private String roomName;
    private String roomNumofPeo;

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    final String ip_addr = "10.0.2.2"; // Emulator PC의 127.0.0.1
//    final String ip_addr = ""; // 실제 Phone으로 테스트 할 때 설정.

    final int port_no = 30000;

    public RoomSettingDialogFragment() {
    }

    public static RoomSettingDialogFragment getInstance() {
        RoomSettingDialogFragment roomSettingDialogFragment = new RoomSettingDialogFragment();
        return roomSettingDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.room_setting_dialog, container);
        Button btn_create= (Button) view.findViewById(R.id.btn_create);
        EditText et_room_name = (EditText) view.findViewById(R.id.et_room_name);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        Spinner sp_num_of_peo = (Spinner) view.findViewById(R.id.sp_set_num_of_peo);
        ArrayAdapter num_of_peo_Adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.num_of_people, android.R.layout.simple_spinner_dropdown_item);
        sp_num_of_peo.setAdapter(num_of_peo_Adapter);

        if(getArguments() != null) {
            userName = getArguments().getString("userName");
        }

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomName = et_room_name.getText().toString();
                roomNumofPeo = sp_num_of_peo.getSelectedItem().toString();
                if(!roomName.equals("")) {
                    CreateRoom();
                    dismiss();
                }
                else
                    Toast.makeText(requireContext(),"입력해주세요",Toast.LENGTH_SHORT).show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return view;
    }

    public void setDialogResult(RoomSettingResult dialogResult) {
        result = dialogResult;
    }

    public interface RoomSettingResult {
        void finish(String roomName, String roomNumofPeo, String roomId);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.room_setting_width);
        int height = getResources().getDimensionPixelSize(R.dimen.room_setting_height);
        getDialog().getWindow().setLayout(width, height);
    }

    public void CreateRoom() {
        new Thread() {
            public void run() {
                try {
                    socket = new Socket(ip_addr, port_no);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    //oos.flush();
                    ois = new ObjectInputStream(socket.getInputStream());
                    ChatMsg obj = new ChatMsg();
                    obj.code = "300";
                    obj.userName = userName;
                    obj.roomName = roomName;
                    obj.roomNumofPeo = roomNumofPeo;
                    SendChatMsg(obj);
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

    // SendChatMsg() : 방이름, 인원수를 서버에게 전달
    public void SendChatMsg(ChatMsg cm)  {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    Log.d("cm.code:", cm.code);
                    Log.d("cm.userName:", cm.userName);
                    Log.d("cm.roomName:", cm.roomName);
                    Log.d("cm.roomNumofPeo:", cm.roomNumofPeo);
                    oos.writeObject(cm.code);
                    oos.writeObject(cm.userName);
                    oos.writeObject(cm.roomName);
                    oos.writeObject(cm.roomNumofPeo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // ChatMsg 를 읽어서 Return, Java 호환성 문제로 field별로 수신해서 ChatMsg 로 만들어 Return
    public ChatMsg ReadChatMsg()  {
        ChatMsg cm = new ChatMsg();
        try {
            cm.code = (String) ois.readObject();
            cm.roomId = (String) ois.readObject();
            // cm.roomId 수신
            result.finish(roomName, roomNumofPeo, cm.roomId);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }
}
