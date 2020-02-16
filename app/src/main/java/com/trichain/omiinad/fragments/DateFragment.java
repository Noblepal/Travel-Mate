package com.trichain.omiinad.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.google.android.material.snackbar.Snackbar;
import com.trichain.omiinad.R;
import com.trichain.omiinad.room.CalendarListener;
import com.trichain.omiinad.room.OnViewSelected;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DateFragment extends Fragment {
    View root;
    CalendarListener calendarListener;
    String TAG="DateFragment";
    OnViewSelected _mClickListener;
    private DateRangeCalendarView calendar;


    public DateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_date, container, false);
        Calendar current = Calendar.getInstance();
        calendar = root.findViewById(R.id.calendar);

        final Calendar startMonth = Calendar.getInstance();
        startMonth.add(Calendar.MONTH, -2);
        final Calendar endMonth = (Calendar) startMonth.clone();
        endMonth.add(Calendar.MONTH, 5);
        Log.d(TAG, "Start month: " + startMonth.getTime().toString() + " :: End month: " + endMonth.getTime().toString());

        calendar.setVisibleMonthRange(startMonth, endMonth);

        final Calendar startDateSelectable = (Calendar) startMonth.clone();
        startDateSelectable.add(Calendar.DATE,20);
        final Calendar endDateSelectable = (Calendar) endMonth.clone();
        endDateSelectable.add(Calendar.DATE, -20);
        Log.d(TAG, "startDateSelectable: " + startDateSelectable.getTime().toString() + " :: endDateSelectable: " + endDateSelectable.getTime().toString());
        calendar.setSelectableDateRange(startDateSelectable, endDateSelectable);
//        calendar.se
        calendar.setCalendarListener(new DateRangeCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar startDate) {
                Toast.makeText(getActivity(), "Start Date: " + startDate.getTime().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDateRangeSelected(final Calendar startDate, final Calendar endDate) {
                _mClickListener.onViewSelected(startDate.getTime().toString(),endDate.getTime().toString());
//                Toast.makeText(getActivity(), "Start Date: " + startDate.getTime().toString(), Toast.LENGTH_SHORT).show();

                Snackbar.make(root, "Done. Swipe to proceed", Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        Log.e(TAG, "onCreateView: " );
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            _mClickListener = (OnViewSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onViewSelected");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: " );
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated: " );
    }
}
