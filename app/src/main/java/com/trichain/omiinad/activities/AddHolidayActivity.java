package com.trichain.omiinad.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.trichain.omiinad.R;
import com.trichain.omiinad.adapters.SectionsPagerAdapter;
import com.trichain.omiinad.entities.HolidayTable;
import com.trichain.omiinad.room.DatabaseClient;
import com.trichain.omiinad.room.OnViewSelected;
import com.trichain.omiinad.utils.MyShakeDetector;

import static com.trichain.omiinad.constants.Constant.APIKEY;

public class AddHolidayActivity extends AppCompatActivity implements OnViewSelected {
    String startDate, stopDate, about, name;
    Double latitude, longitude;
    int PERMISSION_ID = 1111;
    String TAG = "AddHolidayActivity";
    private FloatingActionButton fabCompleteAddHoliday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_holiday);
        checkLocationPermission();
        fabCompleteAddHoliday = findViewById(R.id.fabCompleteAddHoliday);
        startDate = null;
        stopDate = null;
        latitude = 0.0;
        longitude = 0.0;
        about = "";

        /*Initialize shake gesture detector*/
        MyShakeDetector.getInstance(this).instantiateShakeDetector();

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


    public void closeMeNow(View view) {
        finish();
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

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(AddHolidayActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddHolidayActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(AddHolidayActivity.this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(AddHolidayActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        PERMISSION_ID);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(AddHolidayActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_ID);
            }
        }
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
    public void onViewSelected(String requestPermissions, String s, String s1) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(getIntent());
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyShakeDetector.getInstance(this).stopShake();
    }
}