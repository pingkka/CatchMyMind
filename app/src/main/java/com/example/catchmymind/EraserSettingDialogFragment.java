package com.example.catchmymind;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.catchmymind.databinding.EraserSettingDialogBinding;
import com.example.catchmymind.databinding.PenSettingDialogBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class EraserSettingDialogFragment extends DialogFragment {
    private EraserSettingDialogBinding binding;
    EraserSettingResult result;

    private final String penColor = "#FFFFFFFF";
    private String penSize = "10";

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String roomId;

    public EraserSettingDialogFragment(ObjectInputStream ois, ObjectOutputStream oos, String roomId) {
        this.ois = ois;
        this.oos = oos;
        this.roomId = roomId;
    }

    public static EraserSettingDialogFragment getInstance(ObjectInputStream ois, ObjectOutputStream oos, String roomId) {
        EraserSettingDialogFragment eraserSettingDialogFragment = new EraserSettingDialogFragment(ois, oos, roomId);
        return eraserSettingDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EraserSettingDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btn5px.setOnClickListener(v -> {
            penSize = "5px";
        });

        binding.btn10px.setOnClickListener(v -> {
            penSize = "10px";
        });

        binding.btn15px.setOnClickListener(v -> {
            penSize = "15px";
        });

        binding.btnSetting.setOnClickListener(view12 -> {
            settingEraser();
            //Toast.makeText(requireContext(), "색 : " + penColor + ", 두께 : " + penSize + " 지우개 설정 완료", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        binding.btnCancel.setOnClickListener(view1 -> dismiss());

        return view;
    }

    public void setDialogResult(EraserSettingResult dialogResult) {
        result = dialogResult;
    }

    public interface EraserSettingResult {
        void finish(String roomId, String penColor, String penSize);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.eraser_setting_width);
        int height = getResources().getDimensionPixelSize(R.dimen.eraser_setting_height);
        getDialog().getWindow().setLayout(width, height);
    }

    public void settingEraser() {
        new Thread() {
            public void run() {
                try {
                    oos.flush();
                    ChatMsg obj = new ChatMsg();
                    obj.setCode("602");
                    obj.setPenColor(penColor);
                    obj.setPenSize(penSize);
                    obj.setRoomId(roomId);
                    SendChatMsg(obj);
                    DoReceive(); // Server에서 읽는 Thread 실행
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    public synchronized void SendChatMsg(ChatMsg cm) {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    oos.writeObject(cm.getCode());
                    oos.writeObject(cm.getRoomId());
                    oos.writeObject(cm.getPenColor());
                    oos.writeObject(cm.getPenSize());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // ChatMsg 를 읽어서 Return, Java 호환성 문제로 field별로 수신해서 ChatMsg 로 만들어 Return
    public synchronized ChatMsg ReadChatMsg() {
        ChatMsg cm = new ChatMsg();
        try {
            // 여기가 문제
            cm.setCode((String) ois.readObject());
            Log.d("ReadChatMsg : ", cm.getCode());
            // cm.roomId 수신
            result.finish(cm.getRoomId(), cm.getPenColor(), cm.getPenSize());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }
}