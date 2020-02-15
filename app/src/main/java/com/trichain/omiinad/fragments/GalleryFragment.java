package com.trichain.omiinad.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trichain.omiinad.R;
import com.trichain.omiinad.adapters.PhotoAdapter;
import com.trichain.omiinad.adapters.PhotoPlaceListAdapter;
import com.trichain.omiinad.entities.PhotoTable;
import com.trichain.omiinad.roomDB.DatabaseClient;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class GalleryFragment extends Fragment {

    String TAG= "GalleryFragment";
    Button btnSort;
    boolean descending=true;
    boolean isDate,isHoliday,isPlace;
    View root;
    public GalleryFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_gallery, container, false);

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);
        btnSort= dialogView.findViewById(R.id.btnSort);
        FloatingActionButton fabSort = root.findViewById(R.id.fabSort);
        fabSort.setOnClickListener(v -> {
            AlertDialog.Builder b = new AlertDialog.Builder(getContext());
            b.setView(dialogView);
            b.show();

        });
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup= dialogView.findViewById(R.id.rgSort);
                RadioGroup radioGroup2= dialogView.findViewById(R.id.rgOrder);
                TextView textView= dialogView.findViewById(R.id.title_sort);

                if (radioGroup.getCheckedRadioButtonId() == -1){
                    // no radio buttons are checked
                    textView.setText("Sort By: Choose the sort type");
                    Toast.makeText(getActivity(),"Choose the sort type",Toast.LENGTH_LONG);
                }
                else{
                    // one of the radio buttons is checked
                    if (radioGroup2.getCheckedRadioButtonId() == -1){
                        // no radio buttons are checked
                        textView.setText("Sort By: Choose the order type");
                        Toast.makeText(getActivity(),"Choose the order type",Toast.LENGTH_LONG);
                    }
                    else{
                        // one of the radio buttons is checked
                        textView.setText("Sort By");
                        isDate=false;
                        isHoliday=false;
                        isPlace=false;
                        RadioButton rbAsc=dialogView.findViewById(R.id.rbAsc);
                        RadioButton rvDate=dialogView.findViewById(R.id.rvDate);
                        RadioButton rbHoliday=dialogView.findViewById(R.id.rbHoliday);
                        RadioButton rvTag=dialogView.findViewById(R.id.rvTag);

                        if (rbAsc.isChecked()){
                            descending=false;
                        }else {
                            descending=true;
                        }
                        if(rvDate.isChecked()){
                            isDate=true;
                            GetListDatePhotos gh = new GetListDatePhotos();
                            gh.execute();
                        }else if (rbHoliday.isChecked()){
                            isHoliday=true;
                            GetListHolidayPhotos gh = new GetListHolidayPhotos();
                            gh.execute();
                        }else if (rvTag.isChecked()){
                            isPlace=true;
                            GetListPlacePhotos gh = new GetListPlacePhotos();
                            gh.execute();
                        }else {
                            isPlace=true;
                        }
                        Log.e(TAG, "RadioGroup: "+radioGroup.getCheckedRadioButtonId());
                        Log.e(TAG, "RadioGroup 2: "+radioGroup2.getCheckedRadioButtonId());
                    }
                }
            }
        });


        GetListPlacePhotos gh = new GetListPlacePhotos();
        gh.execute();

        return root;
    }

    class GetListPlacePhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            List<PhotoTable> photoTables = DatabaseClient
                    .getInstance(getActivity())
                    .getAppDatabase()
                    .photoDao()
                    .GetListPlacePhotos();
            return photoTables;
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            ((View)root.findViewById(R.id.holiday)).setVisibility(View.GONE);
            ((View)root.findViewById(R.id.place)).setVisibility(View.VISIBLE);
            ((View)root.findViewById(R.id.date)).setVisibility(View.GONE);

            RecyclerView recyclerView = root.findViewById(R.id.place);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            PhotoPlaceListAdapter photoAdapter = new PhotoPlaceListAdapter(photoTables, getActivity(),isDate,isHoliday,isPlace,descending);
            recyclerView.setAdapter(photoAdapter);
        }
    }

    class GetListHolidayPhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            List<PhotoTable> photoTables = DatabaseClient
                    .getInstance(getActivity())
                    .getAppDatabase()
                    .photoDao()
                    .getAllphotosUnconditional();
            return photoTables;
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            RecyclerView recyclerView = root.findViewById(R.id.eventsrec);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            PhotoPlaceListAdapter photoAdapter = new PhotoPlaceListAdapter(photoTables, getActivity(),isDate,isHoliday,isPlace,descending);
            recyclerView.setAdapter(photoAdapter);
        }
    }

    class GetListDatePhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            List<PhotoTable> photoTables = DatabaseClient
                    .getInstance(getActivity())
                    .getAppDatabase()
                    .photoDao()
                    .getAllphotosUnconditional();
            return photoTables;
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            RecyclerView recyclerView = root.findViewById(R.id.eventsrec);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            PhotoPlaceListAdapter photoAdapter = new PhotoPlaceListAdapter(photoTables, getActivity(),isDate,isHoliday,isPlace,descending);
            recyclerView.setAdapter(photoAdapter);
        }
    }


    class GetHolidayPhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            List<PhotoTable> photoTables = DatabaseClient
                    .getInstance(getActivity())
                    .getAppDatabase()
                    .photoDao()
                    .getAllphotosUnconditional();
            return photoTables;
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            RecyclerView recyclerView = root.findViewById(R.id.eventsrec);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            PhotoAdapter photoAdapter = new PhotoAdapter(photoTables, getActivity());
            recyclerView.setAdapter(photoAdapter);
        }
    }

    class GetPlacePhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            List<PhotoTable> photoTables = DatabaseClient
                    .getInstance(getActivity())
                    .getAppDatabase()
                    .photoDao()
                    .getAllphotosUnconditional();
            return photoTables;
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            RecyclerView recyclerView = root.findViewById(R.id.eventsrec);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            PhotoAdapter photoAdapter = new PhotoAdapter(photoTables, getActivity());
            recyclerView.setAdapter(photoAdapter);
        }
    }

    class GetDatePhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            List<PhotoTable> photoTables = DatabaseClient
                    .getInstance(getActivity())
                    .getAppDatabase()
                    .photoDao()
                    .getAllphotosUnconditional();
            return photoTables;
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            RecyclerView recyclerView = root.findViewById(R.id.eventsrec);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            PhotoAdapter photoAdapter = new PhotoAdapter(photoTables, getActivity());
            recyclerView.setAdapter(photoAdapter);
        }
    }

}
