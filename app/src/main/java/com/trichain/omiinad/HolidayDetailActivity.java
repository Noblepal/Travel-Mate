package com.trichain.omiinad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.trichain.omiinad.adapters.EventAdapter;
import com.trichain.omiinad.adapters.EventAdapter2;
import com.trichain.omiinad.entities.VisitedPlaceTable;
import com.trichain.omiinad.roomDB.DatabaseClient;

import java.util.List;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class HolidayDetailActivity extends AppCompatActivity {

    private Menu menu;
    private int holidayid;
    private static String TAG = "HolidayDetailActivity";
    private RelativeLayout rlNoPlaces;
    private ImageView imgNoPlaces;

    ShakeDetector shakeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_detail);
        Toolbar toolbar = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
        holidayid = getIntent().getIntExtra("holiday", 0);
        Log.e(TAG, "onCreate: " + holidayid);

        //sensors
        ShakeOptions options = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(2)
                .sensibility(2.0f);

        this.shakeDetector = new ShakeDetector(options).start(this, new ShakeCallback() {
            @Override
            public void onShake() {
                Log.e("event", "onShake");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (getSharedPreferences("MyPref", MODE_PRIVATE).getBoolean("shake_me",true)){
                    startActivity(intent);
                }
            }
        });
        //sesor
        rlNoPlaces = findViewById(R.id.rlNoPlaces);
        imgNoPlaces = findViewById(R.id.imgNoPlaces);
        setImage(holidayid);

        getPlaces(holidayid);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Visited Place?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent bundle = new Intent(HolidayDetailActivity.this, CreateEntryActivity.class);
                                bundle.putExtra("holiday", holidayid);
                                startActivity(bundle);
                            }
                        }).show();
            }
        });


        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption(R.id.action_gallery);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.action_detail_settings);
                }
            }
        });
    }

    private void setImage(final int holidayid) {
        class GetHolidays extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {

                final String holidayphotoCount = DatabaseClient
                        .getInstance(HolidayDetailActivity.this)
                        .getAppDatabase()
                        .photoDao()
                        .getLatestHolydayphotos(holidayid);

                return holidayphotoCount;
            }

            @Override
            protected void onPostExecute(String holidayphotoCount) {
                super.onPostExecute(holidayphotoCount);
                ImageView view = findViewById(R.id.expandedImage);
                Glide.with(HolidayDetailActivity.this)
                        .load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + holidayphotoCount)
                        .fallback(R.drawable.ic_landscape)
                        .placeholder(R.drawable.ic_landscape)
                        .into(view);
            }
        }

        GetHolidays gh = new GetHolidays();
        gh.execute();
    }

    private void getPlaces(final int holidayid) {
        class GetHolidays extends AsyncTask<Void, Void, List<VisitedPlaceTable>> {

            @Override
            protected List<VisitedPlaceTable> doInBackground(Void... voids) {
                List<VisitedPlaceTable> visitedPlaceTables = DatabaseClient
                        .getInstance(HolidayDetailActivity.this)
                        .getAppDatabase()
                        .visitedPlaceDao()
                        .getAllVisitedplace(holidayid);
                return visitedPlaceTables;
            }

            @Override
            protected void onPostExecute(List<VisitedPlaceTable> visitedPlaceTables) {
                super.onPostExecute(visitedPlaceTables);

                RecyclerView recyclerView = findViewById(R.id.eventsrec);
                recyclerView.setLayoutManager(new LinearLayoutManager(HolidayDetailActivity.this));
                EventAdapter eventAdapter = new EventAdapter(visitedPlaceTables, HolidayDetailActivity.this);
                recyclerView.setAdapter(eventAdapter);

                if (visitedPlaceTables.size() == 0) {
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
        rlNoPlaces.setVisibility(View.VISIBLE);
        imgNoPlaces.setOnClickListener(v -> {
            Intent bundle = new Intent(HolidayDetailActivity.this, CreateEntryActivity.class);
            bundle.putExtra("holiday", holidayid);
            startActivity(bundle);
        });
    }

    private void hideNoHolidaysLayout() {
        rlNoPlaces.setVisibility(View.GONE);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_holiday_details, menu);
        hideOption(R.id.action_detail_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_detail_settings) {
            return true;
        } else if (id == R.id.action_gallery) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}