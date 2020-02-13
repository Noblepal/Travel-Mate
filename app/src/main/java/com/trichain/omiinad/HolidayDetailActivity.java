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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.trichain.omiinad.Entities.VisitedPlaceTable;
import com.trichain.omiinad.RoomDB.DatabaseClient;
import com.trichain.omiinad.adapters.EventAdapter;
import com.trichain.omiinad.adapters.EventAdapter2;

import java.util.List;

public class HolidayDetailActivity extends AppCompatActivity {

    private Menu menu;
    int holidayid;
    private static String TAG = "HolidayDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_detail);
        Toolbar toolbar = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
        holidayid = getIntent().getIntExtra("holiday", 0);
        Log.e(TAG, "onCreate: " + holidayid);

        setImage(holidayid);


        getPlaces(holidayid);
//        RecyclerView recyclerView = findViewById(R.id.eventsrec);
//        recyclerView.setLayoutManager(new LinearLayoutManager(HolidayDetailActivity.this));
//        List<VisitedPlaceTable> aa = new List<VisitedPlaceTable>(List);
//        aa.add("data 1");
//        aa.add("data 11");
//        aa.add("data 111");
//        aa.add("data 1111");
//        EventAdapter holidayAdapter = new EventAdapter(aa, HolidayDetailActivity.this);
//        recyclerView.setAdapter(holidayAdapter);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Visited Place?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent bundle = new Intent(HolidayDetailActivity.this,CreateEntry.class);
                                bundle.putExtra("holiday",holidayid);
                                startActivity(bundle);
//                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                                CreateEntryFragment createEntryFragment = new CreateEntryFragment();
//                                createEntryFragment.setArguments(bundle);
//                                ft.replace(R.id.add_place_fragment, createEntryFragment);
//                                ft.commit();
//                                fab.setVisibility(View.GONE);
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

//                TasksAdapter adapter = new TasksAdapter(MainActivity.this, tasks);
//                recyclerView.setAdapter(adapter);

                ImageView view=findViewById(R.id.expandedImage);
                Glide.with(HolidayDetailActivity.this)
                        .load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/"+holidayphotoCount)
                        .fallback(R.drawable.japan)
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

//                TasksAdapter adapter = new TasksAdapter(MainActivity.this, tasks);
//                recyclerView.setAdapter(adapter);


                RecyclerView recyclerView = findViewById(R.id.eventsrec);
                recyclerView.setLayoutManager(new LinearLayoutManager(HolidayDetailActivity.this));
                EventAdapter2 eventAdapter = new EventAdapter2(visitedPlaceTables, HolidayDetailActivity.this);
                recyclerView.setAdapter(eventAdapter);
            }
        }

        GetHolidays gh = new GetHolidays();
        gh.execute();
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