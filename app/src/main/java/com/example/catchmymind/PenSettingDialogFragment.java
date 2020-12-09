package com.example.catchmymind;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.catchmymind.databinding.PenSettingDialogBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PenSettingDialogFragment extends DialogFragment {
    private PenSettingDialogBinding binding;
    PenSettingResult result;

    private String penColor = "#FF000000";
    private String penSize = "10";

    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String roomId;

    public PenSettingDialogFragment(ObjectInputStream ois, ObjectOutputStream oos, String roomId) {
        this.ois = ois;
        this.oos = oos;
        this.roomId = roomId;
    }

    public static PenSettingDialogFragment getInstance(ObjectInputStream ois, ObjectOutputStream oos, String roomId) {
        PenSettingDialogFragment penSettingDialogFragment = new PenSettingDialogFragment(ois, oos, roomId);
        return penSettingDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PenSettingDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnRed.setOnClickListener(v -> {
            penColor = "#FF0000";
        });

        binding.btnOrange.setOnClickListener(v -> {
            penColor = "#FF9436";
        });

        binding.btnYellow.setOnClickListener(v -> {
            penColor = "#FFFF24";
        });

        binding.btnGreen.setOnClickListener(v -> {
            penColor = "#1DDB16";
        });

        binding.btnBlue.setOnClickListener(v -> {
            penColor = "#0054FF";
        });

        binding.btnNavy.setOnClickListener(v -> {
            penColor = "#0000A5";
        });

        binding.btnPurple.setOnClickListener(v -> {
            penColor = "#8041D9";
        });

        binding.btnBlack.setOnClickListener(v -> {
            penColor = "#000000";
        });

        binding.btn5px.setOnClickListener(v -> {
            penSize = "3";
        });

        binding.btn10px.setOnClickListener(v -> {
            penSize = "10";
        });

        binding.btn15px.setOnClickListener(v -> {
            penSize = "17";
        });

        binding.btnSetting.setOnClickListener(view12 -> {
            settingPen();
            //Toast.makeText(requireContext(), "색 : " + penColor + ", 두께 : " + penSize + " 펜 설정 완료", Toast.LENGTH_SHORT).show();
            result.finish(roomId, penColor, penSize);
            dismiss();
        });

        binding.btnCancel.setOnClickListener(view1 -> dismiss());

        return view;
    }

    public void setDialogResult(PenSettingResult dialogResult) {
        result = dialogResult;
    }

    public interface PenSettingResult {
        void finish(String roomId, String penColor, String penSize);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.pen_setting_width);
        int height = getResources().getDimensionPixelSize(R.dimen.pen_setting_height);
        getDialog().getWindow().setLayout(width, height);
    }

    public void settingPen() {
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
            cm.setCode((String) ois.readObject());
            Log.d("ReadChatMsg : ", cm.getCode());
            //result.finish(cm.getRoomId(), cm.getPenColor(), cm.getPenSize());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }
}