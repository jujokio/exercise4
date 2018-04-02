package com.example.jussijokio.exercise4;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by gorinds on 28/03/2018.
 * API doc:
 * Create user request: method: POST, endpoint: "users/createuser" bodyParams (JSON): "username, password"
 * Login request: method: GET, endpoint: "users/login" urlParams: "username, password"
 * Location update request: method: GET, endpoint: "location/update" urlParams: "id, lat, lon"
 */



public class CallAPI extends AsyncTask<String, String, String> {
    private static final String apiUrl = "https://hangouts-mobisocial-18.herokuapp.com/";
    JSONObject payload;
    String mHTTPMethod;
    public AsyncResponse delegate = null;

    public CallAPI() {
        //set context variables if required
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
        Log.e("ApiHelper Result", result);
        delegate.processFinish(result);
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            URL url = new URL(apiUrl + params[0]);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(mHTTPMethod);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoInput(true);
            if (Objects.equals(mHTTPMethod, "POST")) {
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(payload.toString());
                writer.flush();
                writer.close();
                os.close();
            }
            conn.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String content = "", line;
            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }

            Log.e("ApiHelper Content", content);
            return content;


        } catch (Exception e) {

            System.out.println(e.getMessage());


        }

        return "heloo"; //heloo
    }

    public void setPayload(JSONObject parJson, String method){
        payload = parJson;
        mHTTPMethod = method;
    }
}
