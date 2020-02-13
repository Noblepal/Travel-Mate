package com.trichain.omiinad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.trichain.omiinad.Entities.PeopleTable;
import com.trichain.omiinad.Entities.PhotoTable;
import com.trichain.omiinad.Entities.VisitedPlaceTable;
import com.trichain.omiinad.RoomDB.DatabaseClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewPlace extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapLongClickListener {

    public static String TAG = "CreateEntry";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 111;
    View root;
    Double longitude, latitude;
    MapView mMapView;
    private GoogleMap googleMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    int place, people1, holiday;
    List<Image> images;
    int strtext;
    CarouselView carouselView;
    List<PhotoTable> photoTables2;
    String people="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);
        place = getIntent().getIntExtra("place_id", 0);
        people1 = 0;

        //carosel
        getImages();
//        ((ImageButton) findViewById(R.id.img_add_photo)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImagePicker.create(ViewPlace.this)
//                        .returnMode(ReturnMode.CAMERA_ONLY)
//                        .folderMode(true) // folder mode (false by default)
//                        .toolbarFolderTitle("Folder") // folder selection title
//                        .toolbarImageTitle("Tap to select") // image selection title
//                        .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
//                        .multi() // multi mode (default mode)
//                        .limit(5) // max images can be selected (99 by default)
//                        .enableLog(true) // disabling log
//                        .start(); // start image picker activity with request code
//
//            }
//        });
//        ((ImageButton) findViewById(R.id.img_add_members)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImageButton ok_now=findViewById(R.id.ok_now);
//                final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.dialog_number_picker);
//                numberPicker.setMaxValue(50);
//                numberPicker.setMinValue(1);
//                numberPicker.setWrapSelectorWheel(false);
//                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                    @Override
//                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                        people1=newVal;
//                        ((TextView)findViewById(R.id.people)).setText(String.valueOf(people1));
//                    }
//
//                });
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    numberPicker.setOnContextClickListener(new View.OnContextClickListener() {
//                        @Override
//                        public boolean onContextClick(View v) {
//                            Log.d(TAG, "onContextClick: ");
//                            numberPicker.setVisibility(View.GONE);
//                            return false;
//                        }
//                    });
//                }
//                numberPicker.setVisibility(View.VISIBLE);
//                ok_now.setVisibility(View.VISIBLE);
//                ok_now.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        numberPicker.setVisibility(View.GONE);
//                        ok_now.setVisibility(View.GONE);
//                    }
//                });
//                View vva= numberPicker;
//                vva.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        if (!hasFocus){
//                            numberPicker.clearFocus();
//                            v.setVisibility(View.GONE);
//                            ok_now.setVisibility(View.GONE);
//                        }
//                        Log.e(TAG, "onFocusChange: "+hasFocus );
//                    }
//                });
//
//            }
//        });
//
//
//        findViewById(R.id.img_change_location).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startEditing();
//            }
//        });
        //Map
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(ViewPlace.this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (place != 0) {
            getHolidayId();
            getPeople();
        }
    }

    public void setGoogleMap(Double latitude, Double longitude, String name) {

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
//                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(sydney).title(name).snippet("You visited this place"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ViewPlace.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Location Permission already granted
                        buildGoogleApiClient();
                        googleMap.setMyLocationEnabled(true);
                    } else {
                        //Request Location Permission
                        checkLocationPermission();
                    }
                } else {
                    buildGoogleApiClient();
                    googleMap.setMyLocationEnabled(true);
                }
                /*googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                });*/
            }
        });
    }

    private void getPeople() {
        class GetPeople extends AsyncTask<Void, Void, List<PeopleTable>> {

            @Override
            protected List<PeopleTable> doInBackground(Void... voids) {
                List<PeopleTable> peopleTables = DatabaseClient
                        .getInstance(ViewPlace.this)
                        .getAppDatabase()
                        .peopleDao()
                        .getAllofEventPeople(place);
                return peopleTables;
            }

            @Override
            protected void onPostExecute(List<PeopleTable> peopleTables) {
                super.onPostExecute(peopleTables);
                for (int i = 0; i < peopleTables.size(); i++) {
                    Log.e(TAG, "getPersonName: " + peopleTables.get(i).getPersonName());
                    if (people.contentEquals("")){
                        people=peopleTables.get(i).getPersonName();
                    }else {

                        people=people+", "+peopleTables.get(i).getPersonName();
                    }
                }
                ViewPlace.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.names)).setText(people);
                    }
                });

            }
        }

        GetPeople gh = new GetPeople();
        gh.execute();
    }

    private void getHolidayId() {
        class GetHoliday extends AsyncTask<Void, Void, VisitedPlaceTable> {

            @Override
            protected VisitedPlaceTable doInBackground(Void... voids) {
                VisitedPlaceTable visitedPlaceTables = DatabaseClient
                        .getInstance(ViewPlace.this)
                        .getAppDatabase()
                        .visitedPlaceDao()
                        .getHolidayIdofplace(place);
                return visitedPlaceTables;
            }

            @Override
            protected void onPostExecute(VisitedPlaceTable visitedPlaceTables) {
                super.onPostExecute(visitedPlaceTables);
                holiday = visitedPlaceTables.getHolidayID();
                ViewPlace.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((TextView) findViewById(R.id.id_date)).setText(getDateOnly(visitedPlaceTables.getVisitDate()));
                            ((TextView) findViewById(R.id.id_day)).setText(getDayMonthYear(visitedPlaceTables.getVisitDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        ((TextView) findViewById(R.id.place_name)).setText(visitedPlaceTables.getName());
                        ((View) findViewById(R.id.hide_me)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ViewPlace.this, EditEntry.class);
                                intent.putExtra("place",place);
                                intent.putExtra("holiday",holiday);
                                startActivity(intent);
                            }
                        });
                        ((TextView) findViewById(R.id.tv_time1)).setText(visitedPlaceTables.getVisitTime());
                        ((TextView) findViewById(R.id.til_entry_title)).setText(visitedPlaceTables.getText());
                        setGoogleMap(visitedPlaceTables.getLatitude(),visitedPlaceTables.getLongitude(),visitedPlaceTables.getName());
                    }
                });

            }
        }

        GetHoliday gh = new GetHoliday();
        gh.execute();
    }

    private void getImages() {
        class GetImages extends AsyncTask<Void, Void, List<PhotoTable>> {

            @Override
            protected List<PhotoTable> doInBackground(Void... voids) {
                List<PhotoTable> photoTables = DatabaseClient
                        .getInstance(ViewPlace.this)
                        .getAppDatabase()
                        .photoDao()
                        .getAllEventphotos(place);
                return photoTables;
            }

            @Override
            protected void onPostExecute(List<PhotoTable> photoTables) {
                super.onPostExecute(photoTables);
                photoTables2 = photoTables;
                carouselView = (CarouselView) findViewById(R.id.carouselView);
                carouselView.setImageListener(imageListener);
                carouselView.setPageCount(photoTables.size());//sampleImages.length


                for (int i = 0; i < photoTables.size(); i++) {
                    Log.e(TAG, "doInBackground: " + photoTables.get(i).getPhotoName());
                }
            }
        }

        GetImages gh = new GetImages();
        gh.execute();
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
//                imageView.setImageResource(sampleImages[position]);
            String filename = photoTables2.get(position).getPhotoName();
            Glide.with(ViewPlace.this)
                    .load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + filename))
                    .fallback(R.drawable.japan)
                    .into(imageView);
        }
    };
    public void trySave(View v) {
        TextView img_add_photo_no, id_date, id_day, tv_time1, people;

        TextInputEditText id_title, id_msg;
        img_add_photo_no = findViewById(R.id.img_add_photo_no);
        id_date = findViewById(R.id.id_date);
        id_day = findViewById(R.id.id_day);
        tv_time1 = findViewById(R.id.tv_time1);
        id_title = findViewById(R.id.id_title);
        id_msg = findViewById(R.id.id_msg);
        people = findViewById(R.id.people);

        if (img_add_photo_no.getText().toString().contentEquals("0")) {
            Toast.makeText(this, "Kindly add some photos", Toast.LENGTH_SHORT).show();
        } else if (id_title.getText().toString().contentEquals("")) {
            Toast.makeText(this, "Please fill the title", Toast.LENGTH_SHORT).show();
        } else if (id_msg.getText().toString().contentEquals("")) {
            Toast.makeText(this, "Please add some details", Toast.LENGTH_SHORT).show();
        } else if (place == 0) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

        } else if (people1 == 0) {
            Toast.makeText(this, "Select Number of people", Toast.LENGTH_SHORT).show();

        } else {

            String id_dates = id_date.getText().toString();
            String id_days = id_day.getText().toString();
            String tv_time1s = tv_time1.getText().toString();
            String id_titles = id_title.getText().toString();
            String id_msgs = id_msg.getText().toString();


            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            String formattedDate2 = df2.format(c.getTime());

            class SaveTask extends AsyncTask<Void, Void, Void> {

                @Override
                protected Void doInBackground(Void... voids) {

                    //creating a task

                    VisitedPlaceTable visitedPlaceTable = new VisitedPlaceTable();
                    visitedPlaceTable.setName(id_titles);
                    visitedPlaceTable.setHolidayID(place);
                    visitedPlaceTable.setLatitude(latitude);
                    visitedPlaceTable.setLongitude(longitude);
                    visitedPlaceTable.setVisitDate(formattedDate);
                    visitedPlaceTable.setVisitTime(formattedDate2);


                    long vid = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                            .visitedPlaceDao()
                            .insert(visitedPlaceTable);
                    int vid2 = ((int) vid);


                    for (int i = 0; i < images.size(); i++) {
                        System.out.println(images.get(i));
                        Log.e(TAG, "doInBackground: " + images.get(i).getPath());

                        Uri imageUri = Uri.parse(images.get(i).getPath());
                        Bitmap bitmap = null;
                        try {
                            String fileSuffix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());


                            File source1 = new File(images.get(i).getPath());
                            Log.e(TAG, "doInBackground: Path :" + images.get(i).getPath());
                            File destination1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + fileSuffix + images.get(i).getName());
                            copy(source1, destination1);

                            PhotoTable photoTable = new PhotoTable();
                            photoTable.setHolidayID(place);
                            photoTable.setPhotoName(fileSuffix + images.get(i).getName());
                            photoTable.setPlaceID(vid2);


                            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                    .photoDao()
                                    .insert(photoTable);
                            Log.e(TAG, "doInBackground: photos" + i);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < people1; i++) {
                        PeopleTable peopleTable = new PeopleTable();
                        peopleTable.setHolidayID(place);
                        peopleTable.setPlaceID(vid2);
                        peopleTable.setPersonName("none");
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                                .peopleDao()
                                .insert(peopleTable);
                        Log.e(TAG, "doInBackground: people" + i);

                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    finish();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                }
            }

            SaveTask st = new SaveTask();
            st.execute();
        }
    }

    private void copy(File source, File destination) throws IOException {

        if (!destination.exists()) {
            destination.getParentFile().mkdirs();
            destination.createNewFile();
        }
        FileChannel in = new FileInputStream(source).getChannel();
        FileChannel out = new FileOutputStream(destination).getChannel();

        try {
            in.transferTo(0, in.size(), out);
        } catch (Exception e) {
            // post to log
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }


    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            // Get a list of picked images
            images = ImagePicker.getImages(data);
            ((TextView) findViewById(R.id.img_add_photo_no)).setText(String.valueOf(images.size()));

            Log.e(TAG, "onActivityResult: " + images.size());
        }
        Log.e(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
//            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
//            // do your logic here...
//        }
//        super.onActivityResult(requestCode, resultCode, data);  // You MUST have this line to be here
        // so ImagePicker can work with fragment mode
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(ViewPlace.this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LocationManager locationManager = (LocationManager) ViewPlace.this.getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();

                        if (ViewPlace.this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ViewPlace.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        } else {
                            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Log.e(TAG, "onConnected: " + latitude);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(ViewPlace.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ViewPlace.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(ViewPlace.this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ViewPlace.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ViewPlace.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(ViewPlace.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ViewPlace.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private String getTimeOnly() {
        Calendar c = Calendar.getInstance();

//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private String getDayMonthYear(String a) throws ParseException {
        Date df = new SimpleDateFormat("E\nMMM yyyy").parse(a);
        return df.toString();
    }

    private String getDateOnly(String a) throws ParseException {
        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(a);
        return date1.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public void addPlace(View v) {
        Intent intent = new Intent(this, CreateEntry.class);
        intent.putExtra("holiday", holiday);
        startActivity(intent);
    }

    public void share(View v) {
        //TODO share the content

        sendFile sendFile=new sendFile();
        sendFile.sendMyFile(ViewPlace.this,photoTables2);
       /* Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("image/jpeg"); *//* This example is sharing jpeg images. *//*

        ArrayList<Uri> files = new ArrayList<Uri>();
        for (int i = 0; i < photoTables2.size(); i++) {
            Log.e(TAG, "doInBackground: " + photoTables2.get(i).getPhotoName());
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + photoTables2.get(i).getPhotoName());
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }


        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);*/
    }
}
