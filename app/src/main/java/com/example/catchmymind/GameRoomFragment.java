package com.example.catchmymind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.catchmymind.databinding.FragmentAppStartBinding;
import com.example.catchmymind.databinding.FragmentGameRoomBinding;

import java.util.ArrayList;

public class GameRoomFragment extends Fragment {
    FragmentGameRoomBinding binding;
    private ArrayList<Room> roomList = new ArrayList<>();
    private String userName;

    public GameRoomFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        for (int i = 0; i < 8; i++) {
//            roomList.add(String.format("Room %d", i + 1));
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null) {
            userName = getArguments().getString("userName");
        }

        binding = FragmentGameRoomBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvGameRoom.setLayoutManager(new LinearLayoutManager(requireContext()));
        GameRoomRecyclerAdapter gameRoomRecyclerAdapter = new GameRoomRecyclerAdapter(roomList);
        binding.rvGameRoom.setAdapter(gameRoomRecyclerAdapter);

        binding.btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoomSettingDialogFragment roomSettingDialogFragment = RoomSettingDialogFragment.getInstance();

                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);
                roomSettingDialogFragment.setArguments(bundle);

                roomSettingDialogFragment.show(getParentFragmentManager(), "Create Room");
                roomSettingDialogFragment.setDialogResult(new RoomSettingDialogFragment.RoomSettingResult() {
                    @Override
                    public void finish(String roomName, String roomNumofPeo, String roomId) {
                        Room room = new Room(roomName, roomNumofPeo, roomId);
                        roomList.add(room);
                        gameRoomRecyclerAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}