package com.example.catchmymind;

import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catchmymind.databinding.FragmentAppStartBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;


public class AppStartFragment extends Fragment implements Serializable {

    private FragmentAppStartBinding binding;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;
    private Socket socket;

    private static final String ip_addr = "10.0.2.2"; // Emulator PC의 127.0.0.1
//    private static final String ip_addr = "192.168.123.100"; // 실제 Phone으로 테스트 할 때 설정. 경진
    //private static final String ip_addr = "192.168.0.4"; // 실제 Phone으로 테스트 할 때 설정. 수연
    private static final int port_no = 30000;

    public AppStartFragment() {
    }

    public static AppStartFragment getInstance() {
        AppStartFragment appStartFragment = new AppStartFragment();
        return appStartFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppStartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Connect();
        return view;
    }

    public void Connect() {
        new Thread() {
            public void run() {
                try {
                    socket = new Socket(ip_addr, port_no);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.flush();
                    ois = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnStart.setOnClickListener(view1 -> {
            LoginDialogFragment loginDialog = LoginDialogFragment.getInstance(ois, oos);
            loginDialog.show(getParentFragmentManager(), "login");
            loginDialog.setDialogResult((name, code) -> {
                if(code.equals("100")) {
                    GameRoomFragment gameRoomFragment = GameRoomFragment.getInstance(name, ois, oos);

                    getParentFragmentManager().beginTransaction().replace(R.id.fg_app_start, gameRoomFragment).commit();
//                    GameRoomFragment gameRoomFragment = new GameRoomFragment();
//                    Bundle args = new Bundle();
//                   MySocket mySocket = new MySocket(name, this.ois, this.oos);
//                    args.putSerializable("socket", mySocket);


//                    getActivity().runOnUiThread(() -> Navigation.findNavController(requireView()).navigate(R.id.action_appStartFragment_to_gameRoomFragment, args));
                }
            });
        });
    }
}