package com.trichain.omiinad.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trichain.omiinad.R;
import com.trichain.omiinad.entities.VisitedPlaceTable;
import com.trichain.omiinad.room.DatabaseClient;

import java.util.List;

import static com.trichain.omiinad.utils.Utils.setGoogleMapStyle;

public class VisitedFragment extends Fragment {

    public static String TAG = "VisitedFragment";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 111;
    View root;
    Double longitude, latitude;
    MapView mMapView;
    private GoogleMap googleMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_visited, container, false);

        mMapView = (MapView) root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                setGoogleMapStyle(getActivity(), googleMap);

                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(-34, 151);
                getPlaces(googleMap);
//                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(Marker marker) {
//                        new AlertDialog.Builder(getActivity())
//                                .setTitle("Title")
//                                .setMessage("Endereço: Telefone: ")
//                                .setPositiveButton("Ir", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
////                                        Intent intent=new Intent(getActivity(),)
//                                    }
//                                })
//                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // do nothing
//                                    }
//                                })
//                                .show();
//                        return false;
//                    }
//                });

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Location Permission already granted
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    googleMap.setMyLocationEnabled(true);
                }
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        latitude = latLng.latitude;
                        longitude = latLng.longitude;
                    }
                });
            }
        });

        return root;
    }

    private void getPlaces(final GoogleMap googleMap) {
        class GetHolidays extends AsyncTask<Void, Void, List<VisitedPlaceTable>> {

            @Override
            protected List<VisitedPlaceTable> doInBackground(Void... voids) {
                List<VisitedPlaceTable> visitedPlaceTables = DatabaseClient
                        .getInstance(getActivity())
                        .getAppDatabase()
                        .visitedPlaceDao()
                        .getAllVisitedplacesAnywhere();
                return visitedPlaceTables;
            }

            @Override
            protected void onPostExecute(List<VisitedPlaceTable> visitedPlaceTables) {
                super.onPostExecute(visitedPlaceTables);

//                TasksAdapter adapter = new TasksAdapter(MainActivity.this, tasks);
//                recyclerView.setAdapter(adapter);


                for (int i = 0; i < visitedPlaceTables.size(); i++) {
                    System.out.println(visitedPlaceTables.get(i));
                    Log.e(TAG, "doInBackground: " + visitedPlaceTables.get(i).getName());
                    String name, des,date;
                    Double longitude, latitude;
                    int id;
                    name = visitedPlaceTables.get(i).getName();
                    des = visitedPlaceTables.get(i).getText();
                    longitude = visitedPlaceTables.get(i).getLongitude();
                    latitude = visitedPlaceTables.get(i).getLatitude();
                    id = visitedPlaceTables.get(i).getId();
                    date = visitedPlaceTables.get(i).getVisitDate();

                    LatLng sydney = new LatLng(latitude, longitude);

                    googleMap.addMarker(new MarkerOptions().position(sydney).title(name).snippet("Date-"+date));

                }
//
//                RecyclerView recyclerView = root.findViewById(R.id.eventsrec);
//                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//                EventAdapter eventAdapter = new EventAdapter(visitedPlaceTables, getActivity());
//                recyclerView.setAdapter(eventAdapter);
            }
        }

        GetHolidays gh = new GetHolidays();
        gh.execute();
    }
}