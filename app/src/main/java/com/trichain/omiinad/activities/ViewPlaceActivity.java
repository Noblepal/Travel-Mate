package com.trichain.omiinad.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.esafirm.imagepicker.features.ImagePicker;
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
import com.trichain.omiinad.R;
import com.trichain.omiinad.entities.PeopleTable;
import com.trichain.omiinad.entities.PhotoTable;
import com.trichain.omiinad.entities.VisitedPlaceTable;
import com.trichain.omiinad.room.DatabaseClient;
import com.trichain.omiinad.utils.SendFile;
import com.trichain.omiinad.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

import static com.trichain.omiinad.utils.Utils.setGoogleMapStyle;

public class ViewPlaceActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapLongClickListener {

    public static String TAG = "CreateEntryActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 111;
    private View root;
    private Double longitude, latitude;
    private MapView mMapView;
    private GoogleMap googleMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private int place, people1, holiday;
    private List<Image> images;
    private int strtext;
    private CarouselView carouselView, cV;
    private ImageButton ib2_close;
    private List<PhotoTable> photoTables2;
    private String people = "";
    private FrameLayout mainLayout;
    private RelativeLayout secondLayout;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);
        place = getIntent().getIntExtra("place_id", 0);
        people1 = 0;

        mainLayout = findViewById(R.id.rootLayoutFrame);
        secondLayout = findViewById(R.id.rl2_custom_layout);

        //Shake listener
        ShakeOptions options = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(2)
                .sensibility(2.0f);

        this.shakeDetector = new ShakeDetector(options).start(this, new ShakeCallback() {
            @Override
            public void onShake() {
                Log.e("event", "onShake");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (getSharedPreferences("MyPref", MODE_PRIVATE).getBoolean("shake_me", true)) {
                    startActivity(intent);
                }
            }
        });

        /*Load images from external storage*/
        getImages();

        //Map
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(ViewPlaceActivity.this.getApplicationContext());
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
                setGoogleMapStyle(ViewPlaceActivity.this, googleMap);

                // For showing a move to my location button
                // googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(sydney).title(name).snippet("You visited this place"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ViewPlaceActivity.this,
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
                        .getInstance(ViewPlaceActivity.this)
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
                    if (people.contentEquals("")) {
                        people = peopleTables.get(i).getPersonName();
                    } else {

                        people = people + ", " + peopleTables.get(i).getPersonName();
                    }
                }
                ViewPlaceActivity.this.runOnUiThread(new Runnable() {
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
                        .getInstance(ViewPlaceActivity.this)
                        .getAppDatabase()
                        .visitedPlaceDao()
                        .getHolidayIdofplace(place);
                return visitedPlaceTables;
            }

            @Override
            protected void onPostExecute(VisitedPlaceTable visitedPlaceTables) {
                super.onPostExecute(visitedPlaceTables);
                holiday = visitedPlaceTables.getHolidayID();
                ViewPlaceActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((TextView) findViewById(R.id.id_date)).setText(Utils.getDateNumberOnly(visitedPlaceTables.getVisitDate()));
                            ((TextView) findViewById(R.id.id_day)).setText(Utils.formatDate(visitedPlaceTables.getVisitDate()));
                            Log.e(TAG, "run: "+getDateOnly(Utils.getTimeNumberOnly(visitedPlaceTables.getVisitDate())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e(TAG, "run: "+e );
                        }
                        ((TextView) findViewById(R.id.place_name)).setText(visitedPlaceTables.getName());
                        ((View) findViewById(R.id.hide_me)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ViewPlaceActivity.this, EditEntryActivity.class);
                                intent.putExtra("place", place);
                                intent.putExtra("holiday", holiday);
                                startActivity(intent);
                            }
                        });
                        ((TextView) findViewById(R.id.tv_time1)).setText(Utils.getTimeNumberOnly(visitedPlaceTables.getVisitDate()));
                        ((TextView) findViewById(R.id.til_entry_title)).setText(visitedPlaceTables.getText());
                        setGoogleMap(visitedPlaceTables.getLatitude(), visitedPlaceTables.getLongitude(), visitedPlaceTables.getName());
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
                        .getInstance(ViewPlaceActivity.this)
                        .getAppDatabase()
                        .photoDao()
                        .getAllEventphotos(place);
                return photoTables;
            }

            @Override
            protected void onPostExecute(List<PhotoTable> photoTables) {
                super.onPostExecute(photoTables);
                photoTables2 = photoTables;
                carouselView = findViewById(R.id.carouselView);
                carouselView.setImageListener(imageListener);
                carouselView.setPageCount(photoTables.size());

                findViewById(R.id.imgViewFullScreenPhoto).setOnClickListener(v -> {
                    showCarouselInFullScreen();
                });

                for (int i = 0; i < photoTables.size(); i++) {
                    Log.e(TAG, "doInBackground: " + photoTables.get(i).getPhotoName());
                }
            }
        }

        GetImages gh = new GetImages();
        gh.execute();
    }

    public void showCarouselInFullScreen() {
        cV = findViewById(R.id.carouselView2);
        cV.setImageListener(imageListener);
        cV.setPageCount(photoTables2.size());
        cV.setCurrentItem(carouselView.getCurrentItem());
        ib2_close = findViewById(R.id.ib2_close_again);
        ib2_close.setOnClickListener(v3 -> showMainView());
        showFullCarouselView();
    }

    private void showFullCarouselView() {
        secondLayout.setVisibility(View.VISIBLE);
    }

    private void showMainView() {
        secondLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (secondLayout.getVisibility() == View.VISIBLE) {
            showMainView();
        } else {
            super.onBackPressed();
        }
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            // imageView.setImageResource(sampleImages[position]);
            String filename = photoTables2.get(position).getPhotoName();
            Glide.with(ViewPlaceActivity.this)
                    .load(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + filename))
                    .fallback(R.drawable.landscape)
                    .placeholder(R.drawable.landscape)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showCarouselInFullScreen(v);
                }
            });
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

                        Date date = c.getTime();
                        String newDateString = new SimpleDateFormat("dd MMM yyyy").format(date);
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
                            photoTable.setPhotoDate(newDateString);


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
        mGoogleApiClient = new GoogleApiClient.Builder(ViewPlaceActivity.this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        LocationManager locationManager = (LocationManager) ViewPlaceActivity.this.getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();

                        if (ViewPlaceActivity.this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ViewPlaceActivity.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        } else {
                            try {
                                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.e(TAG, "onConnected: " + latitude);
                            } catch (NullPointerException e) {
                                Toast.makeText(ViewPlaceActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                            }
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
        if (ContextCompat.checkSelfPermission(ViewPlaceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ViewPlaceActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(ViewPlaceActivity.this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ViewPlaceActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ViewPlaceActivity.this,
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
                    if (ContextCompat.checkSelfPermission(ViewPlaceActivity.this,
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
                    Toast.makeText(ViewPlaceActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private String getTimeOnly() {
        Calendar c = Calendar.getInstance();

        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private String getDayMonthYear(String a) throws ParseException {
        @SuppressLint("SimpleDateFormat") Date df = new SimpleDateFormat("E\nMMM yyyy").parse(a);
        assert df != null;
        return df.toString();
    }

    private String getDateOnly(String a) throws ParseException {
        @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("dd").parse(a);
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
        Intent intent = new Intent(this, CreateEntryActivity.class);
        intent.putExtra("holiday", holiday);
        startActivity(intent);
    }

    public void share(View v) {
        //TODO share the content

        SendFile sendFile = new SendFile();
        sendFile.sendMyFile(ViewPlaceActivity.this, photoTables2);
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

    @Override
    protected void onStop() {
        super.onStop();
        shakeDetector.stopShakeDetector(this);
    }
}
