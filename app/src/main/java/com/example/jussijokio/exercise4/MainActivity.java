package com.example.jussijokio.exercise4;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {
    public TextView saveMessageField;
    public TextView receiveMessageField;
    public Button saveMessageButton;
    private SharedPreferences preferences;
    private String message;
    public Location last;

    //eetu liittyi dev tiimiin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!CheckLoginActive()){
            Intent gotoLogin = new Intent(this, LoginActivity.class);
            startActivity(gotoLogin);
        }
    // this is coding
        //init ui elements
        saveMessageField = (TextView) findViewById(R.id.SaveMessageField);
        receiveMessageField = (TextView) findViewById(R.id.ReceiveMessageField);
        saveMessageButton = (Button) findViewById(R.id.SaveMessage);
        saveMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(last != null && saveMessageField.getText().length() >= 1) {
                    SaveMessage(last);
                }else {
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
        }
        else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        //time in ms, distance in m
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60*5, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // code here plz.
                last = location;
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
                Toast.makeText(getBaseContext(),"Enable GPS please!",
                        Toast.LENGTH_SHORT).show();
            }
        });
       displayLastLocation(last);
    }

    private boolean CheckForMessages(Location last) {
        //search keys from preferences.
        String key = getPreferencesKey(last);
        String receivedMessage = preferences.getString(key, null);
        if(receivedMessage != null){
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // What happens, e.g., what activity is launched, if notification is clicked
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            // Since this can happen in the future, wrap it in a pending intent
            PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // build notification
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification  = new Notification.Builder(getBaseContext())
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
        }else{
            Log.e("locationMessage", "no messages with key: "+key);
            receiveMessageField.setText(null);
            return false;
        }

    }

    private String getPreferencesKey(Location last) {
        DecimalFormat df = new DecimalFormat("#.000");
        String lat = df.format(last.getLatitude());
        String lon =  df.format(last.getLongitude());
        String key = "Lat:"+lat+", Lon:"+lon;
        return key;
    }

    private void DisplayMessage(String receivedMessage) {
        Toast.makeText(getBaseContext(),"Your location has A message! ",
                Toast.LENGTH_LONG).show();
        receiveMessageField.setText(receivedMessage);
    }

    public void displayLastLocation(Location last) {
        if(last != null) {
            String key = getPreferencesKey(last);
            Toast.makeText(getBaseContext(),key,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void SaveMessage(Location last) {
        String key = getPreferencesKey(last);
        preferences.edit().putString(key,saveMessageField.getText().toString()).apply();
        Toast.makeText(getBaseContext(),"Message saved.",
                Toast.LENGTH_LONG).show();
        saveMessageField.setText(null);
    }


    public boolean CheckLoginActive() {

        return false;
    }

}
