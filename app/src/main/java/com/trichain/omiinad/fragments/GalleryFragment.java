package com.trichain.omiinad.fragments;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trichain.omiinad.R;
import com.trichain.omiinad.adapters.PhotoAdapter;
import com.trichain.omiinad.entities.PhotoTable;
import com.trichain.omiinad.room.DatabaseClient;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class GalleryFragment extends Fragment {


    public GalleryFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        FloatingActionButton fabSort = root.findViewById(R.id.fabSort);
        fabSort.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);
            AlertDialog.Builder b = new AlertDialog.Builder(getContext());
            b.setView(dialogView);
            AlertDialog dialog = b.create();
            dialog.show();
        });


        class GetPhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

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

        GetPhotos gh = new GetPhotos();
        gh.execute();

        return root;
    }


}
