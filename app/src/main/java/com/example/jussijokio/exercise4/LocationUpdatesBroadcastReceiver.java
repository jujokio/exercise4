package com.example.jussijokio.exercise4;

/**
 * Created by gorinds on 09/04/2018.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver implements AsyncResponse {
    CallAPI apihelper;
    Context baseContext;
    private static final String TAG = "LUBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        baseContext = context;
        SharedPreferences sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        int mUserID = sharedPref.getInt("id", 0);

        if (intent != null) {
            LocationResult result = LocationResult.extractResult(intent);
            if (result != null) {
                List<Location> locations = result.getLocations();
                Location lastLoc = locations.get(locations.size() - 1);
                Log.d(TAG, String.valueOf(lastLoc.getLatitude()));
                apihelper = new CallAPI();
                apihelper.setPayload(new JSONObject(), "GET");
                apihelper.delegate = this;
                apihelper.execute(String.format("location/update?id=%s&lat=%s&lon=%s", mUserID, lastLoc.getLatitude(), lastLoc.getLongitude()));
            }

        }
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
            Toast.makeText(baseContext, obj.getString("msg"), Toast.LENGTH_SHORT).show();
            //mInfoText.setText(obj.getString("msg"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            if (obj != null) {
                if (obj.getString("status").toLowerCase().equals("failed")) {
                    Toast.makeText(baseContext, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }

            switch (obj != null ? obj.getInt("responseid") : 0) {
                case 1:
                    Log.e("ApiHelperhandleresponse", "user creation response");
                    break;
                case 3:
                    Log.e("resp", String.valueOf(obj.getJSONArray("nearbyUsers")));

                    if (obj.getJSONArray("nearbyUsers").length() > 0) {
                        NotificationManager notificationManager = (NotificationManager) baseContext.getSystemService(NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.d(TAG, "Going to oreo notify");
                            int notifyID = 1;
                            String CHANNEL_ID = "my_channel_01";// The id of the channel.
                            CharSequence name = "HangOut";// The user-visible name of the channel.
                            int importance = NotificationManager.IMPORTANCE_HIGH;
                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                            notificationManager.createNotificationChannel(mChannel);
                            Intent intent = new Intent(baseContext, ListActivity.class);
                            intent.putExtra("nearbyUsers", obj.getJSONArray("nearbyUsers").toString());
                            // Since this can happen in the future, wrap it in a pending intent
                            PendingIntent pIntent = PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Notification notification = new Notification.Builder(baseContext, CHANNEL_ID)
                                    .setContentTitle("You have a new location-bound message!")
                                    .setContentText(obj.getJSONArray("nearbyUsers").get(0).toString() + " is nearby!")
                                    .setSmallIcon(R.drawable.favicon1)
                                    .setContentIntent(pIntent)
                                    .setAutoCancel(true)
                                    .setChannelId(CHANNEL_ID)
                                    .build();
                            notificationManager.notify(123, notification);
                        } else {
                            Log.d(TAG, "Going to depricated notify");
                            // What happens, e.g., what activity is launched, if notification is clicked
                            Intent intent = new Intent(baseContext, ListActivity.class);
                            intent.putExtra("nearbyUsers", obj.getJSONArray("nearbyUsers").toString());
                            // Since this can happen in the future, wrap it in a pending intent
                            PendingIntent pIntent = PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            // build notification
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Notification notification = new Notification.Builder(baseContext)
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
            Toast.makeText(baseContext, "An error occured!" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}