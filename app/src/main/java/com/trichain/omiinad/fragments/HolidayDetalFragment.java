package com.trichain.omiinad.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trichain.omiinad.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HolidayDetalFragment extends Fragment {


    public HolidayDetalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_holiday_detal, container, false);
    }

}
