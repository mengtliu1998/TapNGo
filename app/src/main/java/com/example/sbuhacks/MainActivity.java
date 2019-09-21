package com.example.sbuhacks;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import androidx.core.app.NotificationCompat;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.example.sbuhacks.NotificationApp.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private static final String TAG = "MainActivity";
    private static Location home;
    private static String location;
    private static Geocoder geocoder;
    private static Location current = new Location("");
    private static int distance;
    private static SharedPreferences preferences;

    ListView listView;
    private static String newReminder;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    private TextView locate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callPermissions();

        preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
//        preferences.edit().remove("latitude").apply();
//        preferences.edit().remove("longitude").apply();

        if (preferences.contains("location")) {
            location = preferences.getString("location", "SBU");
            locate = (TextView)findViewById(R.id.currentLocation);
            locate.setText(location);
            home = new Location("");
            home.setLatitude(preferences.getFloat("latitude", (float) 0.0));
            home.setLongitude(preferences.getFloat("longitude", (float) 0.0));
        } else {
            geocoder = new Geocoder(this, Locale.ENGLISH);
            askForAddress();
        }

//        if(home == null){
//            geocoder = new Geocoder(this, Locale.ENGLISH);
//            askForAddress();
//        }

        notificationManager = NotificationManagerCompat.from(this);

        listView = (ListView) findViewById(R.id.reminders);

        Set<String> set = preferences.getStringSet("Reminders", null);
        if(set != null){
            arrayList = new ArrayList<String>(set);
        }else{
            arrayList = new ArrayList<>();
        }

        FloatingActionButton fab = findViewById(R.id.addReminder);
        FloatingActionButton fab2 = findViewById(R.id.setlocation);


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);

                // Get the Layout Parameters for ListView Current Item View
                ViewGroup.LayoutParams params = view.getLayoutParams();

                // Set the height of the Item View
                params.height = 150;
                view.setLayoutParams(params);

                return view;
            }
        };
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                arrayList.remove(position);
                Set<String> set = new HashSet<String>();
                set.addAll(arrayList);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putStringSet("Reminders", set);
                editor.apply();
                listView.setAdapter(arrayAdapter);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReminder();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForAddress();
            }
        });

    }


    public void addReminder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Reminder");

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(editText);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newReminder = editText.getText().toString();
                arrayList.add(newReminder);
                Set<String> set = new HashSet<String>();
                set.addAll(arrayList);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putStringSet("Reminders", set);
                editor.apply();
                listView.setAdapter(arrayAdapter);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

//
//    public static String getPrecipitation() throws IOException {
//        String ret = "There is no rain/snow for the next 9 hours.";
//        String[] a = dataObtainer();
////    	System.out.println(a[1] + " " + a[3] + " " + a[5]);
//        if(a != null && a.length >5){
//            if ("rain".equals(a[1]) || "rain".equals(a[3]) || "rain".equals(a[5])) {
//                ret = "rain within 9 hours.";
//            }
//            if ("snow".equals(a[1]) || "snow".equals(a[3]) || "snow".equals(a[5])) {
//                ret = "snow within 9 hours.";
//            }
//        }
//
//        return ret;
//    }
//
//    public static String[] dataObtainer(){
//        String data = "";
//        try {
//            String lat = "15.3092";
//            String lon = "61.3794";
////          URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=New York,us&APPID=acc903c7dcbb320f5b17c54e372612f4");
////        	URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=40.916168&lon=-73.118309&mode=xml&appid=acc903c7dcbb320f5b17c54e372612f4");
//            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&units=imperial&mode=xml&appid=acc903c7dcbb320f5b17c54e372612f4");
//            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
//            String lineRead = reader.readLine();
//
//            while (lineRead != null) {
//                data += lineRead;
//                lineRead = reader.readLine();
//            }
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        String[] listInfo = new String[6];
//
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document;
//            document = builder.parse(new InputSource(new StringReader(data)));
//            Element rootElement = document.getDocumentElement();
//
//
//            Node forecast = rootElement.getLastChild();
//            Node currentChild = forecast.getFirstChild();
//            Node currentTimeChild;
//
//
//            //hardcoded 9 hrs
//            currentTimeChild = (Node) currentChild.getFirstChild().getNextSibling();
//            System.out.println(((Element) currentChild).getAttribute("from"));
//            System.out.println(((Element) currentTimeChild).getAttribute("type"));
//            listInfo[0] = ((Element) currentChild).getAttribute("from");
//            listInfo[1] = ((Element) currentTimeChild).getAttribute("type");
//
//            currentChild = currentChild.getNextSibling();
//            System.out.println(((Element) currentChild).getAttribute("from"));
//            currentTimeChild = (Node) currentChild.getFirstChild().getNextSibling();
//            System.out.println(((Element) currentTimeChild).getAttribute("type"));
//            listInfo[2] = ((Element) currentChild).getAttribute("from");
//            listInfo[3] = ((Element) currentTimeChild).getAttribute("type");
//
//            currentChild = currentChild.getNextSibling();
//            System.out.println(((Element) currentChild).getAttribute("from"));
//            currentTimeChild = (Node) currentChild.getFirstChild().getNextSibling();
//            System.out.println(((Element) currentTimeChild).getAttribute("type"));
//            listInfo[4] = ((Element) currentChild).getAttribute("from");
//            listInfo[5] = ((Element) currentTimeChild).getAttribute("type");
//
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return listInfo;
//    }

    public void callNotifications(){
        if (home != null && (distance = (int) current.distanceTo(home) * 3) >= 50) {
            Log.e(TAG, "The distance is " + distance + " ft.");
//            try{
//                String weather = getPrecipitation();
//                if (weather.equals("There is no rain/snow for the next 9 hours.")) {
//                    Notification();
//                } else {
//                    NotificationRain();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            Notification();

        } else {
            Log.e(TAG, "Bs The distance is " + distance + " ft.");

        }
    }

    public void askForAddress() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Address");

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(editText);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                location = editText.getText().toString();
                locate = (TextView)findViewById(R.id.currentLocation);
                locate.setText(location);
                Log.e(TAG, location);

                try {
                    Thread geo = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<Address> addressList = geocoder.getFromLocationName(location, 5);
                                home = new Location("");
                                home.setLatitude(addressList.get(0).getLatitude());
                                home.setLongitude(addressList.get(0).getLongitude());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putFloat("latitude", (float) addressList.get(0).getLatitude());
                                editor.putFloat("longitude", (float) addressList.get(0).getLongitude());
                                editor.putString("location", location);
                                editor.apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    geo.run();


                    Log.e(TAG, "The latitude: " + home.getLatitude() + " The long: " + home.getLongitude());
                    callNotifications();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (home == null) {
                    home = new Location("");
                    home.setLongitude(current.getLongitude());
                    home.setLatitude(current.getLatitude());
                    Log.e(TAG, "HARDCODED: The latitude: " + home.getLatitude() + " The long: " + home.getLongitude());
                    locate = (TextView)findViewById(R.id.currentLocation);
                    locate.setText("Latitude: " + current.getLatitude() + " Longitude: " + current.getLongitude());
                    callNotifications();
                }
            }
        });

        builder.show();
    }

    public void requestLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PermissionChecker.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PermissionChecker.PERMISSION_GRANTED) {
            client = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(12000);
            locationRequest.setInterval(15000);
            client.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Log.e(TAG, "Lat: " + locationResult.getLastLocation().getLatitude() +
                            " Long: " + locationResult.getLastLocation().getLongitude());
                    current.setLatitude(locationResult.getLastLocation().getLatitude());
                    current.setLongitude(locationResult.getLastLocation().getLongitude());
                    callNotifications();
                }
            }, getMainLooper());
        } else {
            callPermissions();
        }

    }

    public void callPermissions() {
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


    public void Notification() {
        String message = "Do you have everything?";

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("message", message);

        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this,
                0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.notbear)
                .setContentTitle("Hey you!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .build();
        notificationManager.notify(1, notification);


        NotificationManager manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        //Log.e(TAG,"bro");
    }

    public void NotificationRain() {
        String message = "UMBRELLA! There is a chance of rain/snow today! ";
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.notbear)
                .setContentTitle("Hey you!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify(1, notification);

    }
}