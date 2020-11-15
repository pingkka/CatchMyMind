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

public class LoginDialogFragment extends DialogFragment {
    LoginResult result;

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
                if(!et_login.equals("")) {
                    result.finish(et_login.getText().toString());
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
        void finish(String result);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.login_width);
        int height = getResources().getDimensionPixelSize(R.dimen.login_height);
        getDialog().getWindow().setLayout(width, height);
    }
}
