package com.example.jussijokio.exercise4;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements AsyncResponse {
    public TextView saveMessageField;
    public TextView receiveMessageField;
    public Button saveMessageButton;
    private SharedPreferences preferences;
    private String message;
    public Location last;
    public Button intentswitcher;
    public CallAPI apihelper;
    int s;

    //eetu liittyi dev tiimiin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JSONObject jsonParam = new JSONObject();
        apihelper = new CallAPI();
        apihelper.setPayload(jsonParam, "GET");
        s = ((UserData) this.getApplication()).getmUserID();
        apihelper.delegate = this;
        if (!CheckLoginActive()) {
            Intent gotoLogin = new Intent(this, LoginActivity.class);
            startActivity(gotoLogin);
//            finish();
        }
        // this is coding
        //init ui elements
        saveMessageField = (TextView) findViewById(R.id.SaveMessageField);
        receiveMessageField = (TextView) findViewById(R.id.ReceiveMessageField);
        saveMessageButton = (Button) findViewById(R.id.SaveMessage);
        intentswitcher = (Button) findViewById(R.id.intent_switcher);

        intentswitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intentswitcher = new Intent(MainActivity.this,ListActivity.class);
//                startActivity(intentswitcher);
//                Location update request: method: GET, endpoint: "location/update" urlParams: "id, lat, lon"
                apihelper.execute(String.format("location/update?id=%s&lat=%s&lon=%s", s, last.getLatitude(), last.getLongitude()));
            }
        });

        saveMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (last != null && saveMessageField.getText().length() >= 1) {
                    SaveMessage(last);
                } else {
                    Log.e("locationMessage", "null pointers!");
                }
            }
        });

        //init shared preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //init location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        //time in ms, distance in m
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // code here plz.
                last = location;
                Log.d("latitude", String.valueOf(location.getLatitude()));
                displayLastLocation(last);
                CheckForMessages(last);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                CheckForMessages(last);
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getBaseContext(), "Enable GPS please!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        displayLastLocation(last);
    }

    private boolean CheckForMessages(Location last) {
        //search keys from preferences.
        String key = getPreferencesKey(last);
        String receivedMessage = preferences.getString(key, null);
        if (receivedMessage != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // What happens, e.g., what activity is launched, if notification is clicked
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            // Since this can happen in the future, wrap it in a pending intent
            PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // build notification
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification = new Notification.Builder(getBaseContext())
                    .setContentTitle("You have a new location-bound message!")
                    .setContentText(receivedMessage)
                    .setSound(alarmSound)
                    .setSmallIcon(R.drawable.favicon1)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true) //clear automatically when clicked
                    .build();
            // send notification to notification tray
            notificationManager.notify(123, notification);
            DisplayMessage(receivedMessage);
            return true;
        } else {
            Log.e("locationMessage", "no messages with key: " + key);
            receiveMessageField.setText(null);
            return false;
        }

    }

    private String getPreferencesKey(Location last) {
        DecimalFormat df = new DecimalFormat("#.000");
        String lat = df.format(last.getLatitude());
        String lon = df.format(last.getLongitude());
        String key = "Lat:" + lat + ", Lon:" + lon;
        return key;
    }

    private void DisplayMessage(String receivedMessage) {
        Toast.makeText(getBaseContext(), "Your location has A message! ",
                Toast.LENGTH_LONG).show();
        receiveMessageField.setText(receivedMessage);
    }

    public void displayLastLocation(Location last) {
        if (last != null) {
            String key = getPreferencesKey(last);
            Toast.makeText(getBaseContext(), key,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void SaveMessage(Location last) {
        String key = getPreferencesKey(last);
        preferences.edit().putString(key, saveMessageField.getText().toString()).apply();
        Toast.makeText(getBaseContext(), "Message saved.",
                Toast.LENGTH_LONG).show();
        saveMessageField.setText(null);
    }


    public boolean CheckLoginActive() {
        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        return true;
    }

    public Context getContext(){
        return this;
    }

    @Override
    public void processFinish(String output) {
        JSONObject obj = null;
        //response ID:t ja esimerkki responset
        // 1 - Create user - {"responseid":1,"status":"success","id":41,"username":"testi","msg":"Successfully created new account. Welcome testi"}
        // 2 - Login - {"responseid":2,"status":"success","id":34,"username":"testi","msg":"Successfully logged in."}
        // 3 - Location update - {"responseid":3,"status":"success","nearbyUsers":["Testikakkone"],"msg":"Location updated."}
        try {
            obj = new JSONObject(output);
            Log.e("ApiHelper responsejson",obj.toString());
            Log.e("ApiHelper responseid", String.valueOf(obj.getInt("responseid")));
            Toast.makeText(this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
            //mInfoText.setText(obj.getString("msg"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            if(obj != null) {
                if(obj.getString("status").toLowerCase().equals("failed")){
                    Toast.makeText(this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }

            switch(obj != null ? obj.getInt("responseid") : 0){
                case 1:
                    Log.e("ApiHelperhandleresponse","user creation response");
                    break;
                case 2:
                    //Do this and this
                    if (Objects.equals(obj != null ? obj.getString("status") : null, "success")){
                        ((UserData) this.getApplication()).setmUserID(obj.getInt("id"));
                    }
                    break;
                case 3:
                    Log.e("resp", String.valueOf(obj.getJSONArray("nearbyUsers")));

                    Intent intentswitcher = new Intent(MainActivity.this,ListActivity.class);
                    intentswitcher.putExtra("nearbyUsers", obj.getJSONArray("nearbyUsers").toString());
                    startActivity(intentswitcher);
                    break;
                default: //For all other cases, do this
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "An error occured!"+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
