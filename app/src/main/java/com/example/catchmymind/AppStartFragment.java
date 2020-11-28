package com.example.catchmymind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.catchmymind.databinding.FragmentAppStartBinding;


public class AppStartFragment extends Fragment {

    private FragmentAppStartBinding binding;
    private String userName;

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

        binding.btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LoginDialogFragment loginDialog = LoginDialogFragment.getInstance();
                loginDialog.show(getParentFragmentManager(), "login");
                loginDialog.setDialogResult(new LoginDialogFragment.LoginResult() {
                    @Override
                    public void finish(String name, String code) {
                        userName = name;

                        Fragment fragment = new GameRoomFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("userName", userName);
                        fragment.setArguments(bundle);

                        if(code.equals("100")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Navigation.findNavController(requireView()).navigate(R.id.action_appStartFragment_to_gameRoomFragment);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}