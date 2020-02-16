package com.trichain.omiinad.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trichain.omiinad.DetailGalleryActivity;
import com.trichain.omiinad.R;
import com.trichain.omiinad.adapters.PhotoAdapter;
import com.trichain.omiinad.adapters.PhotoGroupListAdapter;
import com.trichain.omiinad.adapters.PhotoPlaceListAdapter;
import com.trichain.omiinad.entities.PhotoTable;
import com.trichain.omiinad.roomDB.DatabaseClient;
import com.trichain.omiinad.util.RecyclerItemClickListener;

import java.io.Serializable;
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
    View dialogView;
    AlertDialog.Builder b;

    String type="place";
    AlertDialog alertDialog;
    Context context;

    public GalleryFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_gallery, container, false);
        context=getActivity();
        b = new AlertDialog.Builder(context);

        dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);
        //               inflater.inflate(R.layout.dialog_filter, container,false);

        FloatingActionButton fabSort = root.findViewById(R.id.fabSort);
        fabSort.setOnClickListener(v -> {
            // TEXTVIEW
            if(dialogView.getParent() != null) {
                ((ViewGroup)dialogView.getParent()).removeView(dialogView); // <- fix
            }
            b.setView(dialogView);
            b.setPositiveButton("Sort", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

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
                            type ="place";
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
                                type ="date";
                                GetListDatePhotos gh = new GetListDatePhotos(type);
                                gh.execute();
                            }else if (rbHoliday.isChecked()){
                                isHoliday=true;
                                type ="holiday";
                                GetListHolidayPhotos gh = new GetListHolidayPhotos(type);
                                gh.execute();
                            }else if (rvTag.isChecked()){
                                isPlace=true;
                                type ="place";
                                GetListPlacePhotos gh = new GetListPlacePhotos(type);
                                gh.execute();
                            }else {
                                isPlace=true;
                            }

                            dialog.dismiss();
                            Log.e(TAG, "RadioGroup: "+radioGroup.getCheckedRadioButtonId());
                            Log.e(TAG, "RadioGroup 2: "+radioGroup2.getCheckedRadioButtonId());
                        }
                    }
                }
            });
            b.show();

        });


        GetListPlacePhotos gh = new GetListPlacePhotos(type);
        gh.execute();

        return root;
    }

    class GetListPlacePhotos extends AsyncTask<Void, Void, List<PhotoTable>> {

        String type2;
        public GetListPlacePhotos(String type) {
            this.type2=type;
        }

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            Log.e(TAG, "doInBackground: "+type );
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
            Log.e(TAG, "onPostExecute: "+photoTables.size() );
            ((View)root.findViewById(R.id.holiday)).setVisibility(View.GONE);
            ((View)root.findViewById(R.id.place)).setVisibility(View.VISIBLE);
            ((View)root.findViewById(R.id.date)).setVisibility(View.GONE);

            RecyclerView recyclerView = root.findViewById(R.id.place);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            PhotoGroupListAdapter photoAdapter = new PhotoGroupListAdapter(photoTables, getActivity(),isDate,isHoliday,isPlace,descending,recyclerView);
            recyclerView.setAdapter(photoAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    final PhotoTable h = photoTables.get(position);

                    Log.e(TAG, "onClick Frag: "+ type2 +descending+h.getHolidayID() );
                    Intent intent= new Intent(getActivity(), DetailGalleryActivity.class);
                    intent.putExtra("type", type2);
                    intent.putExtra("descending",descending);
                    intent.putExtra("date",h.getPhotoDate());
                    intent.putExtra("holiday",h.getHolidayID());
                    intent.putExtra("place",h.getPlaceID());
                    intent.putExtra("id",h.getId());
                    intent.putExtra("data", (Serializable) photoTables);
                    getActivity().startActivity(intent);
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
    }

    class GetListHolidayPhotos extends AsyncTask<Void, Void, List<PhotoTable>> {


        String type;
        public GetListHolidayPhotos(String type) {
            this.type=type;
        }

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            List<PhotoTable> photoTables = DatabaseClient
                    .getInstance(getActivity())
                    .getAppDatabase()
                    .photoDao()
                    .GetListHolidayPhotos();
            return photoTables;
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            RecyclerView recyclerView = root.findViewById(R.id.place);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            PhotoGroupListAdapter photoAdapter = new PhotoGroupListAdapter(photoTables, getActivity(),isDate,isHoliday,isPlace,descending, recyclerView);
            recyclerView.setAdapter(photoAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    final PhotoTable h = photoTables.get(position);

                    Log.e(TAG, "onClick Frag: "+ type +descending+h.getHolidayID() );
                    Intent intent= new Intent(getActivity(), DetailGalleryActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("descending",descending);
                    intent.putExtra("date",h.getPhotoDate());
                    intent.putExtra("holiday",h.getHolidayID());
                    intent.putExtra("place",h.getPlaceID());
                    intent.putExtra("id",h.getId());
                    intent.putExtra("data", (Serializable) photoTables);
                    getActivity().startActivity(intent);
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
    }

    class GetListDatePhotos extends AsyncTask<Void, Void, List<PhotoTable>> {
        String type;
        public GetListDatePhotos(String type) {
            this.type=type;
        }

        @Override
        protected List<PhotoTable> doInBackground(Void... voids) {
            List<PhotoTable> photoTables = DatabaseClient
                    .getInstance(getActivity())
                    .getAppDatabase()
                    .photoDao()
                    .GetListDatePhotos();
            return photoTables;
        }

        @Override
        protected void onPostExecute(List<PhotoTable> photoTables) {
            super.onPostExecute(photoTables);
            RecyclerView recyclerView = root.findViewById(R.id.place);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            PhotoGroupListAdapter photoAdapter = new PhotoGroupListAdapter(photoTables, getActivity(),isDate,isHoliday,isPlace,descending, recyclerView);
            recyclerView.setAdapter(photoAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    final PhotoTable h = photoTables.get(position);

                    Log.e(TAG, "onClick Frag: "+ type +descending+h.getHolidayID() );
                    Intent intent= new Intent(getActivity(), DetailGalleryActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("descending",descending);
                    intent.putExtra("date",h.getPhotoDate());
                    intent.putExtra("holiday",h.getHolidayID());
                    intent.putExtra("place",h.getPlaceID());
                    intent.putExtra("id",h.getId());
                    intent.putExtra("data", (Serializable) photoTables);
                    getActivity().startActivity(intent);
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
    }


}
