package com.example.catchmymind;

import android.os.Bundle;
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

public class RoomSettingDialogFragment extends DialogFragment {
    RoomSettingResult result;

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

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_room_name.equals("")) {
                    result.finish(et_room_name.getText().toString());
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
        void finish(String result);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.room_setting_width);
        int height = getResources().getDimensionPixelSize(R.dimen.room_setting_height);
        getDialog().getWindow().setLayout(width, height);
    }
}
