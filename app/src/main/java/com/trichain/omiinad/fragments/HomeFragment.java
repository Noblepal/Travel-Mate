package com.trichain.omiinad.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trichain.omiinad.R;
import com.trichain.omiinad.adapters.HolidayAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Toast.makeText(getActivity(), "I am open", Toast.LENGTH_LONG).show();
        RecyclerView  recyclerView= root.findViewById(R.id.mainrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArrayList<String> aa= new ArrayList<>();
        aa.add("data 1");
        aa.add("data 11");
        aa.add("data 111");
        aa.add("data 1111");
        HolidayAdapter holidayAdapter=new HolidayAdapter(aa, getActivity());
        recyclerView.setAdapter(holidayAdapter);
        return root;
    }

}