package com.example.catchmymind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.catchmymind.databinding.FragmentEditQuizDialogBinding;
import com.example.catchmymind.databinding.RoomSettingDialogBinding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class EditQuizDialogFragment extends DialogFragment {

    private EditQuizDialogFragment.EditWordResult result;
    private FragmentEditQuizDialogBinding binding;

    public ObjectInputStream ois;
    public ObjectOutputStream oos;
    public String userName;

    public String word;

    public EditQuizDialogFragment() {

    }

    public static EditQuizDialogFragment newInstance() {
        EditQuizDialogFragment editQuizDialogFragment = new EditQuizDialogFragment();
        return editQuizDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditQuizDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnEnter.setOnClickListener(view12 -> {
            word = binding.etEditword.getText().toString();
            if(!word.equals("")) {
                result.finish(word);
                dismiss();
            }
            else
                Toast.makeText(requireContext(),"입력해주세요",Toast.LENGTH_SHORT).show();
        });

        binding.btnEditCancle.setOnClickListener(view1 -> dismiss());

        return view;
    }

    public void setDialogResult(EditQuizDialogFragment.EditWordResult dialogResult) {
        result = dialogResult;
    }

    public interface EditWordResult {
        void finish(String word);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.edit_quiz_width);
        int height = getResources().getDimensionPixelSize(R.dimen.edit_quiz_height);
        getDialog().getWindow().setLayout(width, height);
    }

}