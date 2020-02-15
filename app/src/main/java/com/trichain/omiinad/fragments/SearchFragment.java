package com.trichain.omiinad.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trichain.omiinad.R;
import com.trichain.omiinad.adapters.EventAdapter;
import com.trichain.omiinad.entities.VisitedPlaceTable;
import com.trichain.omiinad.room.DatabaseClient;

import java.util.List;

public class SearchFragment extends Fragment {
    View root;
    String TAG = "SearchFragment";
    ProgressBar searchProgressBar;
    TextView tvNoItems;
    List<VisitedPlaceTable> visitedPlaceTables;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        tvNoItems = root.findViewById(R.id.tvNoItems);
        EditText searchEditText = root.findViewById(R.id.id_msg);
        searchProgressBar = root.findViewById(R.id.searchProgressBar);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (str.length() > 3) {
                            Log.i(TAG, "afterTextChanged: Searching for: " + str);
                            getPlaces(str);
                        }
                    }
                }, 500);

            }
        });

        return root;
    }

    private void getPlaces(String s) {
        showView(searchProgressBar);
        class GetHolidays extends AsyncTask<Void, Void, List<VisitedPlaceTable>> {

            @Override
            protected List<VisitedPlaceTable> doInBackground(Void... voids) {
                visitedPlaceTables = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .visitedPlaceDao()
                        .getSearchResultsOfPlace(s);
                return visitedPlaceTables;
            }

            @Override
            protected void onPostExecute(List<VisitedPlaceTable> visitedPlaceTables) {
                super.onPostExecute(visitedPlaceTables);
                hideView(searchProgressBar);
                if (visitedPlaceTables.size() == 0) {
                    showView(tvNoItems);
                    //Snackbar.make(root, "No items found", Snackbar.LENGTH_LONG).show();
                } else {
                    hideView(tvNoItems);
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

    private void hideView(View v) {
        v.setVisibility(View.GONE);
    }


    private void showView(View v) {
        v.setVisibility(View.VISIBLE);
    }
}