package com.example.jussijokio.exercise4;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements AsyncResponse, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public TextView saveMessageField;
    public TextView receiveMessageField;
    public Button saveMessageButton;
    public Location last;
    public Button intentswitcher;
    public CallAPI apihelper;
    int s;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL = 10 * 1000;
    private static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 3;
    private String nearbyUsers;
    String[] username;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        nearbyUsers = sharedPref.getString("nearbyUsers", "");
        try {
            JSONArray array = new JSONArray(nearbyUsers);
            username=toStringArray(array);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView usersText = (TextView) findViewById(R.id.nearbyUsers);
        usersText.setText(String.valueOf(username.length) + " Nearby friends");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            finish();
            return;
        }
        JSONObject jsonParam = new JSONObject();
        apihelper = new CallAPI();
        apihelper.setPayload(jsonParam, "GET");
        s = ((UserData) this.getApplication()).getmUserID();
        apihelper.delegate = this;
        if (!CheckLoginActive()) {
            Intent gotoLogin = new Intent(this, LoginActivity.class);
            startActivity(gotoLogin);
        }

        saveMessageField = (TextView) findViewById(R.id.SaveMessageField);
        saveMessageButton = (Button) findViewById(R.id.SaveMessage);
        intentswitcher = (Button) findViewById(R.id.intent_switcher);

        intentswitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentswitcher = new Intent(MainActivity.this, ListActivity.class);
                sharedPref = getContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                nearbyUsers = sharedPref.getString("nearbyUsers", "");
                intentswitcher.putExtra("nearbyUsers", nearbyUsers);
                startActivity(intentswitcher);
            }
        });

        saveMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLocationUpdates();
                Toast.makeText(getBaseContext(), "HangOut stopped updating your location.",
                        Toast.LENGTH_LONG).show();
                if (last != null && saveMessageField.getText().length() >= 1) {
                    SaveMessage(last);
                } else {
                    Log.e("locationMessage", "null pointers!");
                }
            }
        });
        buildGoogleApiClient();

    }

    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
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
        Toast.makeText(getBaseContext(), "Message saved.",
                Toast.LENGTH_LONG).show();
        saveMessageField.setText(null);
    }


    public boolean CheckLoginActive() {
        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        return true;
    }

    public Context getContext() {
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
            Log.e("ApiHelper responsejson", obj.toString());
            Log.e("ApiHelper responseid", String.valueOf(obj.getInt("responseid")));
            Toast.makeText(this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
            //mInfoText.setText(obj.getString("msg"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            if (obj != null) {
                if (obj.getString("status").toLowerCase().equals("failed")) {
                    Toast.makeText(this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }

            switch (obj != null ? obj.getInt("responseid") : 0) {
                case 1:
                    Log.e("ApiHelperhandleresponse", "user creation response");
                    break;
                case 2:
                    //Do this and this
                    if (Objects.equals(obj != null ? obj.getString("status") : null, "success")) {
                        ((UserData) this.getApplication()).setmUserID(obj.getInt("id"));
                    }
                    break;
                case 3:
                    Log.e("resp", String.valueOf(obj.getJSONArray("nearbyUsers")));

                    Intent intentswitcher = new Intent(MainActivity.this, ListActivity.class);
                    intentswitcher.putExtra("nearbyUsers", obj.getJSONArray("nearbyUsers").toString());
                    startActivity(intentswitcher);
                    break;
                default: //For all other cases, do this
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "An error occured!" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void requestLocationUpdates() {
        try {
            Log.i("Main", "Starting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void removeLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    getPendingIntent());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Main", "destroying");
        removeLocationUpdates();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("Main", "GoogleApiClient connected");
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
