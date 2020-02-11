package com.trichain.omiinad.fragments;

import android.os.AsyncTask;
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

import com.trichain.omiinad.Entities.HolidayTable;
import com.trichain.omiinad.R;
import com.trichain.omiinad.RoomDB.DatabaseClient;
import com.trichain.omiinad.adapters.HolidayAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        Toast.makeText(getActivity(), "I am open", Toast.LENGTH_LONG).show();
        getHolidays();
        return root;
    }
    private void getHolidays() {
        class GetHolidays extends AsyncTask<Void, Void, List<HolidayTable>> {

            @Override
            protected List<HolidayTable> doInBackground(Void... voids) {
                List<HolidayTable> holidayTables = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .holidayDao()
                        .getAllHolidays();
                return holidayTables;
            }

            @Override
            protected void onPostExecute(List<HolidayTable> holidays) {
                super.onPostExecute(holidays);

//                TasksAdapter adapter = new TasksAdapter(MainActivity.this, tasks);
//                recyclerView.setAdapter(adapter);


                RecyclerView  recyclerView= root.findViewById(R.id.mainrecyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                HolidayAdapter holidayAdapter=new HolidayAdapter(holidays, getActivity());
                recyclerView.setAdapter(holidayAdapter);
            }
        }

        GetHolidays gh = new GetHolidays();
        gh.execute();
    }

}