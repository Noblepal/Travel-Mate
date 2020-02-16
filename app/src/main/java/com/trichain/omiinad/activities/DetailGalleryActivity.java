package com.trichain.omiinad.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trichain.omiinad.R;
import com.trichain.omiinad.adapters.PhotoAdapter;
import com.trichain.omiinad.entities.PhotoTable;
import com.trichain.omiinad.room.DatabaseClient;

import java.util.List;

public class DetailGalleryActivity extends AppCompatActivity {


    String type;
    String TAG = "DetailGalleryActivity";
    String date;
    int holiday;
    int place;
    int id;
    boolean descending;
    List<PhotoTable> photoTables;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_gallery);
        type = getIntent().getStringExtra("type");
        descending = getIntent().getBooleanExtra("descending", true);
        date = getIntent().getStringExtra("date");
        holiday = getIntent().getIntExtra("holiday", 0);
        place = getIntent().getIntExtra("place", 0);
        id = getIntent().getIntExtra("id", 0);
        photoTables = (List<PhotoTable>) getIntent().getSerializableExtra("data");
        Log.e(TAG, "onCreate: " + place);
        if (id == 0) {
            finish();
        }
        if (type.contentEquals("date")) {
            GetDatePhotos gh = new GetDatePhotos();
            gh.execute();
        } else if (type.contentEquals("holiday")) {
            GetHolidayPhotos gh = new GetHolidayPhotos();
            gh.execute();

        } else if (type.contentEquals("place")) {
            GetPlacePhotos gh = new GetPlacePhotos();
            gh.execute();
        } else {
            Log.e(TAG, "onCreate: Very Big error" + type);
        }
    }

    class GetDatePhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            if (descending) {
                photoTables = DatabaseClient
                        .getInstance(DetailGalleryActivity.this)
                        .getAppDatabase()
                        .photoDao()
                        .getDateDescphotos(date);
                return photoTables;
            } else {
                photoTables = DatabaseClient
                        .getInstance(DetailGalleryActivity.this)
                        .getAppDatabase()
                        .photoDao()
                        .getDateAscphotos(date);
                return photoTables;

            }
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            Log.e(TAG, "onPostExecute: " + photoTables.size());
            RecyclerView recyclerView = findViewById(R.id.photo_Gal);
            recyclerView.setLayoutManager(new GridLayoutManager(DetailGalleryActivity.this, 2));
            PhotoAdapter photoAdapter = new PhotoAdapter(photoTables, DetailGalleryActivity.this);
            recyclerView.setAdapter(photoAdapter);
        }
    }

    public void closeMeNow(View view){
        finish();
    }
    class GetPlacePhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            if (descending) {
                photoTables = DatabaseClient
                        .getInstance(DetailGalleryActivity.this)
                        .getAppDatabase()
                        .photoDao()
                        .getEventDescphotos(place);
                return photoTables;
            } else {
                photoTables = DatabaseClient
                        .getInstance(DetailGalleryActivity.this)
                        .getAppDatabase()
                        .photoDao()
                        .getEventAscphotos(place);
                return photoTables;

            }
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            Log.e(TAG, "onPostExecute: " + photoTables.size());
            RecyclerView recyclerView = findViewById(R.id.photo_Gal);
            recyclerView.setLayoutManager(new GridLayoutManager(DetailGalleryActivity.this, 2));
            PhotoAdapter photoAdapter = new PhotoAdapter(photoTables, DetailGalleryActivity.this);
            recyclerView.setAdapter(photoAdapter);
        }
    }

    class GetHolidayPhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            if (descending) {
                photoTables = DatabaseClient
                        .getInstance(DetailGalleryActivity.this)
                        .getAppDatabase()
                        .photoDao()
                        .getHolDescphotos(holiday);
                return photoTables;
            } else {
                photoTables = DatabaseClient
                        .getInstance(DetailGalleryActivity.this)
                        .getAppDatabase()
                        .photoDao()
                        .getHolAscphotos(holiday);
                return photoTables;

            }
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            RecyclerView recyclerView = findViewById(R.id.photo_Gal);
            recyclerView.setLayoutManager(new GridLayoutManager(DetailGalleryActivity.this, 2));
            PhotoAdapter photoAdapter = new PhotoAdapter(photoTables, DetailGalleryActivity.this);
            recyclerView.setAdapter(photoAdapter);
        }
    }

}
