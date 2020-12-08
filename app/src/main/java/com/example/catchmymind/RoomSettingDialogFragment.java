package com.example.catchmymind;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.catchmymind.databinding.RoomSettingDialogBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class RoomSettingDialogFragment extends DialogFragment implements Serializable {
    private RoomSettingResult result;
    private RoomSettingDialogBinding binding;

    private String roomName;
    private String maxNumofPeo;

    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    public String userName;

    public RoomSettingDialogFragment() {
    }

    public RoomSettingDialogFragment(String userName, ObjectInputStream ois, ObjectOutputStream oos) {
        this.userName = userName;
        this.ois = ois;
        this.oos = oos;
    }

    public static RoomSettingDialogFragment getInstance(String userName, ObjectInputStream ois, ObjectOutputStream oos) {
        RoomSettingDialogFragment roomSettingDialogFragment = new RoomSettingDialogFragment(userName, ois, oos);
        return roomSettingDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RoomSettingDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ArrayAdapter num_of_peo_Adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.num_of_people, android.R.layout.simple_spinner_dropdown_item);
        binding.spSetNumOfPeo.setAdapter(num_of_peo_Adapter);

        binding.btnCreate.setOnClickListener(view12 -> {
            roomName = binding.etRoomName.getText().toString();
            maxNumofPeo = binding.spSetNumOfPeo.getSelectedItem().toString();
            if(!roomName.equals("")) {
                createRoom();
                dismiss();
            }
            else
                Toast.makeText(requireContext(),"입력해주세요",Toast.LENGTH_SHORT).show();
        });

        binding.btnCancel.setOnClickListener(view1 -> dismiss());


        return view;
    }

    public void setDialogResult(RoomSettingResult dialogResult) {
        result = dialogResult;
    }

    public interface RoomSettingResult {
        void finish(String roomName, String maxNumofPeo, String roomId);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.room_setting_width);
        int height = getResources().getDimensionPixelSize(R.dimen.room_setting_height);
        getDialog().getWindow().setLayout(width, height);
    }

    public void createRoom() {
        new Thread() {
            public void run() {
                ChatMsg obj = new ChatMsg();
                obj.setCode("300");
                obj.setRoomName(roomName);
                obj.setMaxNumofPeo(maxNumofPeo);
                SendChatMsg(obj);
                DoReceive(); // Server에서 읽는 Thread 실행
            }
        }.start();
    }

    // Server Message 수신
    public synchronized void DoReceive() {
        new Thread() {
            public void run() {
                ReadChatMsg();
            }
        }.start();
    }

    // SendChatMsg() : 방이름, 인원수를 서버에게 전달
    public synchronized void SendChatMsg(ChatMsg cm)  {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    oos.writeObject(cm.getCode());
                    oos.writeObject(cm.getRoomName());
                    oos.writeObject(cm.getMaxNumofPeo());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // ChatMsg 를 읽어서 Return, Java 호환성 문제로 field별로 수신해서 ChatMsg 로 만들어 Return
    public synchronized ChatMsg ReadChatMsg()  {
        ChatMsg cm = new ChatMsg();
        try {
            cm.setCode((String) ois.readObject());
            cm.setRoomId((String) ois.readObject());
            Log.d("roomSetting:", roomName);
            Log.d("roomSetting:", maxNumofPeo);
            Log.d("roomSetting:", cm.getRoomId());
            result.finish(roomName, maxNumofPeo, cm.getRoomId());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }
}
