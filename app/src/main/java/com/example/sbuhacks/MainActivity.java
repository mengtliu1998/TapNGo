package com.example.sbuhacks;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.location.Geocoder;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;

import android.view.View;
import android.widget.EditText;

import com.example.sbuhacks.ui.main.SectionsPagerAdapter;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private static final String TAG = "MainActivity";
    private static Location home;
    private static String location;
    private static Geocoder geocoder;
    private static Location current = new Location("");
    private static int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        callPermissions();

        if(home == null){
            geocoder = new Geocoder(this, Locale.ENGLISH);
            askForAddress();
        }

    }

    public void callNotifications(){
        if(home != null && (distance = (int) current.distanceTo(home) * 3) >= 50){
            Log.e(TAG, "The distance is " + distance + " ft.");
        }else{
            Log.e(TAG, "Bs The distance is " + distance + " ft.");

        }
    }

    public void askForAddress(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Address");

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(editText);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                location = editText.getText().toString();
                try{
                    List<Address> addressList = geocoder.getFromLocationName(location, 5);
                    home = new Location("");
                    home.setLatitude(addressList.get(0).getLatitude());
                    home.setLatitude(addressList.get(0).getLongitude());

                    Log.e(TAG, "The latitude: " + home.getLatitude() + " The long: " + home.getLongitude());
                    callNotifications();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(home == null){
                    home = new Location("");
                    home.setLongitude(-73.1229135);
                    home.setLatitude(40.9155068);
                    Log.e(TAG, "HARDCODED: The latitude: " + home.getLatitude() + " The long: " + home.getLongitude());
                    callNotifications();
                }
            }
        });

        builder.show();
    }

    public void requestLocationUpdates(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PermissionChecker.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                == PermissionChecker.PERMISSION_GRANTED){
            client = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(2000);
            client.requestLocationUpdates(locationRequest, new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Log.e(TAG,"Lat: " + locationResult.getLastLocation().getLatitude() +
                            " Long: " + locationResult.getLastLocation().getLongitude());
                    current.setLatitude(locationResult.getLastLocation().getLatitude());
                    current.setLongitude(locationResult.getLastLocation().getLongitude());
                }
            }, getMainLooper());
        }else{
            callPermissions();
        }

    }

    public void callPermissions(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                requestLocationUpdates();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callPermissions();
            }
        });
    }
}