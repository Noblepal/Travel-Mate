package com.trichain.omiinad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.trichain.omiinad.entities.HolidayTable;
import com.trichain.omiinad.roomDB.DatabaseClient;
import com.trichain.omiinad.roomDB.OnViewSelected;
import com.trichain.omiinad.adapters.SectionsPagerAdapter;

import static com.trichain.omiinad.constants.Constant.APIKEY;

public class AddHolidayActivity extends AppCompatActivity implements OnViewSelected {
    String startDate, stopDate, about, name;
    Double latitude, longitude;
    String TAG = "AddHolidayActivity";
    private FloatingActionButton fabCompleteAddHoliday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_holiday);
        fabCompleteAddHoliday = findViewById(R.id.fabCompleteAddHoliday);
        startDate = null;
        stopDate = null;
        latitude = 0.0;
        longitude = 0.0;
        about = "";
        // Initialize Places.

        Places.initialize(getApplicationContext(), APIKEY);

        // Create a new Places client instance.

        PlacesClient placesClient = Places.createClient(this);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        fabCompleteAddHoliday.setOnClickListener(v -> onViewSelected("save"));
    }


    @Override
    public void onViewSelected(String start, String stop) {
        if (!start.isEmpty()) {
            startDate = start;
        }
        if (!stop.isEmpty()) {
            stopDate = stop;
        }
        Log.e(TAG, "onViewSelected: " + start);
    }

    @Override
    public void onViewSelected(String name1, Double latitude1, Double longitude1) {
        Log.e(TAG, "onViewSelected: " + latitude1);
        name = name1;
        latitude = latitude1;
        longitude = longitude1;
    }

    @Override
    public void onViewSelected(String about1) {
        about = about1;
        if (startDate == null || stopDate == null) {
            Toast.makeText(this, "Kindly fill the dates", Toast.LENGTH_SHORT).show();
        } else if (name == null) {
            Log.e(TAG, "onViewSelected: " + latitude + " " + longitude + " " + name);
            Toast.makeText(this, "Location not Correctly captured", Toast.LENGTH_SHORT).show();

        } else {

            class SaveTask extends AsyncTask<Void, Void, Void> {

                @Override
                protected Void doInBackground(Void... voids) {

                    //creating a task

                    HolidayTable holidayTables = new HolidayTable();
                    holidayTables.setName(name);
                    holidayTables.setStartDate(startDate);
                    holidayTables.setStartTime("");
                    holidayTables.setEndDate(stopDate);
                    holidayTables.setEndTime("");


                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                            .holidayDao()
                            .insert(holidayTables);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    finish();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                }
            }

            SaveTask st = new SaveTask();
            st.execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode);
    }
}