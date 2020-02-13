package com.trichain.omiinad.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.trichain.omiinad.Entities.VisitedPlaceTable;
import com.trichain.omiinad.R;
import com.trichain.omiinad.RoomDB.DatabaseClient;
import com.trichain.omiinad.adapters.EventAdapter;

import java.util.List;

public class SearchFragment extends Fragment {
    View root;
    String TAG = "SearchFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        ((ImageButton) root.findViewById(R.id.go)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((com.google.android.material.textfield.TextInputEditText) root.findViewById(R.id.id_msg)).getText().toString().contentEquals("")) {
                    Toast.makeText(getActivity(), "Input a word to search", Toast.LENGTH_SHORT).show();
                } else {
                    getPlaces(((com.google.android.material.textfield.TextInputEditText) root.findViewById(R.id.id_msg)).getText().toString());
                }
            }
        });

        return root;
    }

    private void getPlaces(String s) {
        class GetHolidays extends AsyncTask<Void, Void, List<VisitedPlaceTable>> {

            @Override
            protected List<VisitedPlaceTable> doInBackground(Void... voids) {
                List<VisitedPlaceTable> visitedPlaceTables = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .visitedPlaceDao()
                        .getSearchResultsofplace(s);
                return visitedPlaceTables;
            }

            @Override
            protected void onPostExecute(List<VisitedPlaceTable> visitedPlaceTables) {
                super.onPostExecute(visitedPlaceTables);
                if (visitedPlaceTables.size()==0){
                    Snackbar.make(root,"No items found", BaseTransientBottomBar.LENGTH_LONG).show();
                }
                RecyclerView recyclerView = root.findViewById(R.id.eventsrec);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                EventAdapter eventAdapter = new EventAdapter(visitedPlaceTables, getActivity());
                recyclerView.setAdapter(eventAdapter);
            }
        }

        GetHolidays gh = new GetHolidays();
        gh.execute();
    }
}