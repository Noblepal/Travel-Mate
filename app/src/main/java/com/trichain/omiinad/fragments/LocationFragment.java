package com.trichain.omiinad.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.trichain.omiinad.R;
import com.trichain.omiinad.roomDB.CalendarListener;
import com.trichain.omiinad.roomDB.OnViewSelected;

import java.util.Arrays;

import static com.trichain.omiinad.constants.Constant.APIKEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {


    View root;
    CalendarListener calendarListener;
    String TAG = "LocationFragment";
    OnViewSelected _mClickListener;
    int PERMISSION_ID = 222;
    private TextView tvCurrentLocation;
    private boolean isLocationFound;
    private LatLng mLatLng;
    FusedLocationProviderClient mFusedLocationClient;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_location, container, false);

        // Initialize the AutocompleteSupportFragment.
        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), APIKEY);
        }
        tvCurrentLocation = root.findViewById(R.id.currentLocTxt);
        tvCurrentLocation.setOnClickListener(v -> {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            if (isLocationEnabled())
                showAcquiringLocationDialog();
            else
                showGPSWarningDialog();
        });

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // getActivity().getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment == null) {
            Log.e(TAG, "onCreateView: null");
        }
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.e(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
                _mClickListener.onViewSelected(place.getName(), place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "An error occurred: " + status);
            }
        });
        return root;
    }

    private void showAcquiringLocationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_custom_place, null);
        mBuilder.setView(v);
        mBuilder.setCancelable(false);
        ProgressBar mProgressBar = v.findViewById(R.id.progressCustomLocation);
        TextView locationName = v.findViewById(R.id.tvGettinglocation);
        MaterialButton cancelButton = v.findViewById(R.id.btnCancelLocation);
        MaterialButton saveLocation = v.findViewById(R.id.btnSaveLocation);

        if (checkPermissions()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    task -> {
                        Log.e(TAG, "showAcquiringLocationDialog: Task starting" );
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                            isLocationFound = false;
                            Log.e(TAG, "showAcquiringLocationDialog: Task false" );
                        } else {

                            Log.e(TAG, "showAcquiringLocationDialog: Task true" );
                            mProgressBar.setVisibility(View.INVISIBLE);
                            saveLocation.setEnabled(true);
                            locationName.setText(String.format("Lat: %s Lng: %s", location.getLatitude(), location.getLongitude()));
                            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        }

                        Log.e(TAG, "showAcquiringLocationDialog: Task end" );
                    }
            );
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();

            EditText edtCustomLocation = v.findViewById(R.id.edtCustomLocation);
            cancelButton.setOnClickListener(view -> mDialog.dismiss());
            saveLocation.setOnClickListener(v1 -> {
                String customLocation = edtCustomLocation.getText().toString().trim();
                if (customLocation.isEmpty()) {
                    edtCustomLocation.setError("Please enter location name");
                } else {
                    mDialog.dismiss();
                    tvCurrentLocation.setText(customLocation);
                    _mClickListener.onViewSelected(customLocation, mLatLng.latitude, mLatLng.longitude);
                }
            });
        } else {
            _mClickListener.onViewSelected("requestPermissions","","");
//            requestPermissions();
        }

    }

    private void showGPSWarningDialog() {
        AlertDialog.Builder a = new AlertDialog.Builder(getActivity());
        a.setTitle("This application requires GPS");
        a.setMessage("Please turn on GPS in the next screen");
        a.setPositiveButton("Turn on", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });
        a.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            Toast.makeText(getActivity(), "GPS permission denied", Toast.LENGTH_SHORT).show();
        });
        AlertDialog d = a.create();
        d.show();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            _mClickListener = (OnViewSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onViewSelected");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Granted. Start getting the location information 2");
            }
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private boolean getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                    isLocationFound = false;
                                } else {
                                    _mClickListener.onViewSelected("Custom", location.getLatitude(), location.getLongitude());
                                    //requestNewLocationData();
                                    isLocationFound = true;
                                }
                            }
                        }
                );
            } else {
                //Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
        return isLocationFound;
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            _mClickListener.onViewSelected("Custom", mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    };
}
