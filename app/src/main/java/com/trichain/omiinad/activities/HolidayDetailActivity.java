package com.trichain.omiinad.activities;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trichain.omiinad.R;
import com.trichain.omiinad.adapters.EventAdapter;
import com.trichain.omiinad.entities.VisitedPlaceTable;
import com.trichain.omiinad.room.DatabaseClient;
import com.trichain.omiinad.utils.MyShakeDetector;

import java.util.List;

import static com.trichain.omiinad.utils.Utils.loadPhoto;

public class HolidayDetailActivity extends AppCompatActivity {

    private Menu menu;
    private int holidayid;
    private static String TAG = "HolidayDetailActivity";
    private RelativeLayout rlNoPlaces;
    private ImageView imgNoPlaces;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_detail);
        Toolbar toolbar = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
        holidayid = getIntent().getIntExtra("holiday", 0);
        Log.e(TAG, "onCreate: " + holidayid);

        MyShakeDetector.getInstance(this).instantiateShakeDetector();

        rlNoPlaces = findViewById(R.id.rlNoPlaces);
        imgNoPlaces = findViewById(R.id.imgNoPlaces);
        setImage(holidayid);
        getHolidayName(holidayid);
        tvTitle = findViewById(R.id.tvHolidayTitle);
        getPlaces(holidayid);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent bundle = new Intent(HolidayDetailActivity.this, CreateEntryActivity.class);
            bundle.putExtra("holiday", holidayid);
            startActivity(bundle);
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

    @SuppressLint("StaticFieldLeak")
    private void getHolidayName(int holidayId) {
        new AsyncTask<Integer, Void, String>() {
            @Override
            protected String doInBackground(Integer... ints) {
                return DatabaseClient.getInstance(HolidayDetailActivity.this)
                        .getAppDatabase()
                        .holidayDao()
                        .getHolidayName(ints[0])
                        .getName();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                tvTitle.setText(s);
            }
        }.execute(holidayId);
    }

    public void closeMeNow(View view) {
        finish();
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
                loadPhoto(HolidayDetailActivity.this,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + holidayphotoCount,
                        view);
                /*Glide.with(HolidayDetailActivity.this)
                        .load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + holidayphotoCount)
                        .fallback(R.drawable.ic_landscape)
                        .placeholder(R.drawable.ic_landscape)
                        .into(view);*/
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

    @Override
    protected void onStop() {
        super.onStop();
        MyShakeDetector.getInstance(this).stopShake();
    }

}