package com.example.catchmymind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catchmymind.databinding.FragmentAppStartBinding;


public class AppStartFragment extends Fragment {

    private FragmentAppStartBinding binding;

    public AppStartFragment() {
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnStart.setOnClickListener(view1 -> {
            LoginDialogFragment loginDialog = LoginDialogFragment.getInstance();
            loginDialog.show(getParentFragmentManager(), "login");
            loginDialog.setDialogResult((name, code) -> {
                if(code.equals("100")) {
                    getActivity().runOnUiThread(() -> Navigation.findNavController(requireView()).navigate(R.id.action_appStartFragment_to_gameRoomFragment));
                }
            });
        });
    }
}