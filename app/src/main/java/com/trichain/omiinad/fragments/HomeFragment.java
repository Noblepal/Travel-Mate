package com.trichain.omiinad.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trichain.omiinad.activities.AddHolidayActivity;
import com.trichain.omiinad.entities.HolidayTable;
import com.trichain.omiinad.R;
import com.trichain.omiinad.room.DatabaseClient;
import com.trichain.omiinad.adapters.HolidayAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    private View root;
    private RelativeLayout rlNoHolidays;
    private ImageView imgAddNewHoliday;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        rlNoHolidays = root.findViewById(R.id.rlNoHolidays);
        imgAddNewHoliday = root.findViewById(R.id.imgNoHolidays);
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

                RecyclerView recyclerView = root.findViewById(R.id.mainrecyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                HolidayAdapter holidayAdapter = new HolidayAdapter(holidays, getActivity());
                recyclerView.setAdapter(holidayAdapter);

                if (holidays.size() == 0) {
                    showNoHolidaysLayout();
                } else {
                    hideNoHolidaysLayout();
                }
            }
        }

        GetHolidays gh = new GetHolidays();
        gh.execute();
    }

    private void showNoHolidaysLayout() {
        rlNoHolidays.setVisibility(View.VISIBLE);
        imgAddNewHoliday.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddHolidayActivity.class))
        );
    }

    private void hideNoHolidaysLayout() {
        rlNoHolidays.setVisibility(View.GONE);
    }

    private void setImage(final int holidayid) {
        class GetHolidays extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                final String holidayphotoCount = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .photoDao()
                        .getLatestHolydayphotos(holidayid);

                return holidayphotoCount;
            }

            @Override
            protected void onPostExecute(String holidayphotoCount) {
                super.onPostExecute(holidayphotoCount);

                /*TasksAdapter adapter = new TasksAdapter(MainActivity.this, tasks);
                recyclerView.setAdapter(adapter);*/

                ImageView view = root.findViewById(R.id.expandedImage);
                Glide.with(getActivity())
                        .load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + holidayphotoCount)
                        .fallback(R.drawable.japan)
                        .into(view);
            }
        }

        GetHolidays gh = new GetHolidays();
        gh.execute();
    }
}