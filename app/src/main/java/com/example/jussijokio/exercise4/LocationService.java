package com.example.jussijokio.exercise4;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LocationService extends Service {
    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 1;

    private class LocationListener implements android.location.LocationListener, AsyncResponse {
        Location mLastLocation;
        CallAPI apihelper;

                int s = ((UserData) getApplication()).getmUserID();
        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
//            JSONObject jsonParam = new JSONObject();
//            apihelper = new CallAPI();
//            apihelper.setPayload(jsonParam, "GET");
//            apihelper.delegate = this;
//            apihelper.execute(String.format("location/update?id=%s&lat=%s&lon=%s", 34, 65.04265639, 25.43124888));
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            JSONObject jsonParam = new JSONObject();
            apihelper = new CallAPI();
            apihelper.setPayload(jsonParam, "GET");
            apihelper.delegate = this;
            apihelper.execute(String.format("location/update?id=%s&lat=%s&lon=%s", 34, mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
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
                Log.e("ApiHelper responsejson", obj.toString());
                Log.e("ApiHelper responseid", String.valueOf(obj.getInt("responseid")));
                Toast.makeText(getBaseContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                //mInfoText.setText(obj.getString("msg"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {

                if (obj != null) {
                    if (obj.getString("status").toLowerCase().equals("failed")) {
                        Toast.makeText(getBaseContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }

                switch (obj != null ? obj.getInt("responseid") : 0) {
                    case 1:
                        Log.e("ApiHelperhandleresponse", "user creation response");
                        break;
                    case 3:
                        Log.e("resp", String.valueOf(obj.getJSONArray("nearbyUsers")));

                        if (obj.getJSONArray("nearbyUsers").length() > 0) {
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Log.d(TAG,"Going to oreo notify");
                                int notifyID = 1;
                                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                                CharSequence name = "HangOut";// The user-visible name of the channel.
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                notificationManager.createNotificationChannel(mChannel);
                                Intent intent = new Intent(getBaseContext(), ListActivity.class);
                                intent.putExtra("nearbyUsers", obj.getJSONArray("nearbyUsers").toString());
                                // Since this can happen in the future, wrap it in a pending intent
                                PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                Notification notification = new Notification.Builder(getBaseContext(), CHANNEL_ID)
                                        .setContentTitle("You have a new location-bound message!")
                                        .setContentText(obj.getJSONArray("nearbyUsers").get(0).toString() + " is nearby!")
                                        .setSmallIcon(R.drawable.favicon1)
                                        .setContentIntent(pIntent)
                                        .setAutoCancel(true)
                                        .setChannelId(CHANNEL_ID)
                                        .build();
                                notificationManager.notify(123, notification);
                            } else {
                                Log.d(TAG,"Going to depricated notify");
                                // What happens, e.g., what activity is launched, if notification is clicked
                                Intent intent = new Intent(getBaseContext(), ListActivity.class);
                                intent.putExtra("nearbyUsers", obj.getJSONArray("nearbyUsers").toString());
                                // Since this can happen in the future, wrap it in a pending intent
                                PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                // build notification
                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Notification notification = new Notification.Builder(getBaseContext())
                                        .setContentTitle("You have a new location-bound message!")
                                        .setContentText(obj.getJSONArray("nearbyUsers").get(0).toString() + " is nearby!")
                                        .setSound(alarmSound)
                                        .setSmallIcon(R.drawable.favicon1)
                                        .setContentIntent(pIntent)
                                        .setAutoCancel(true) //clear automatically when clicked
                                        .build();
                                // send notification to notification tray
                                notificationManager.notify(123, notification);
                            }
                        }
                        break;
                    default: //For all other cases, do this
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "An error occured!" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER)/*,
            new LocationListener(LocationManager.NETWORK_PROVIDER)*/
    };

  /*  LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };*/

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");
        initializeLocationManager();

       /* try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }*/

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    0,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}