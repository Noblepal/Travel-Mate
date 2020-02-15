package com.trichain.omiinad.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.trichain.omiinad.R;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    View root;
    String TAG = "SettingsFragment";
    Switch switc;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        switc= root.findViewById(R.id.switch1);
        switc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("shake_me",isChecked);
                editor.apply();
            }
        });
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        if (pref.contains("shake_me")){
            switc.setChecked(pref.getBoolean("shake_me",true));
        }else {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("shake_me",true);
            editor.apply();
            switc.setChecked(pref.getBoolean("shake_me",true));

        }

        return root;
    }

}