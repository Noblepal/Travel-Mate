package com.trichain.omiinad.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.trichain.omiinad.R;
import com.trichain.omiinad.utils.MyShakeDetector;

public class HomeActivity extends AppCompatActivity implements SensorListener {
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        if (pref.contains("shake_me")) {

        } else {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("shake_me", true);
            editor.apply();

        }

        /*Shake gesture detector*/
        MyShakeDetector.getInstance(this).instantiateShakeDetector();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, AddHolidayActivity.class)));


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_visited, R.id.nav_home, R.id.nav_gallery, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    public void closeMeNow(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {

    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        MyShakeDetector.getInstance(this).stopShake();
    }
}
