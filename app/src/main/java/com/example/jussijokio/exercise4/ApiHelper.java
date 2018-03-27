package com.example.jussijokio.exercise4;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by Jussi Jokio on 20.3.2018.
 */

public class ApiHelper implements Callable<JSONObject> {
    static final String apiUrl = "https://hangouts-mobisocial-18.herokuapp.com/";
    public JSONObject apiResponse;
    public String method;
    public JSONObject payload;
    public String url;


    @Override
    public JSONObject call() throws Exception {
        Log.e("ApiHelper", "do in background init");

        if(method == null || payload == null || url == null){
            throw new Exception("No parameters setted! use ApiHelper.SetParams()");
        }

        Log.e("ApiHelper", "Using HTTp-method: " + method);
        if (method == "POST") {
            apiResponse = Post(payload, url);
        } else if (method == "GET") {
            apiResponse = Get(payload, url);
        } else if (method == "GET") {
            apiResponse = Get(payload, url);
        } else if (method == "DELETE") {
            apiResponse = Delete(payload, url);
        } else if (method == "PUT") {
            apiResponse = Put(payload, url);
        } else {
            apiResponse = null;
        }
        return apiResponse;
    }


    public void setParams(String parMethod, JSONObject parJson, String parUrlPrefix){
        method = parMethod;
        payload = parJson;
        url = parUrlPrefix;
    }


    private static JSONObject Post(JSONObject payload, String urlPrefix) {
        Log.e("ApiHelper", "POST api init");
        Log.e("ApiHelper", payload.toString());

        final JSONObject json = payload;
        final String prefix = urlPrefix;
        Thread thread = new Thread(new Runnable() {
            //public JSONObject result;
            @Override
            public void run() {
                try {
                    URL url = new URL(apiUrl+prefix);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(json.toString());

                    os.flush();
                    os.close();

                    Log.e("ApiHelper", String.valueOf(conn.getResponseCode()));
                    Log.e("ApiHelper" , conn.getResponseMessage());
                    final JSONObject result = new JSONObject(conn.getResponseMessage().toString());
                } catch (Exception e) {
                    Log.e("ApiHelper", "Error in POST");
                    Log.e("ApiHelper", e.toString());
                    e.printStackTrace();
                }
            }

        });
        thread.start();

    }

    private static JSONObject Get(JSONObject payload, String url) {

        Log.e("ApiHelper", "GET api init");
        Log.e("ApiHelper", payload.toString());
        return new JSONObject();
    }

    private static JSONObject Delete(JSONObject payload, String url) {

        Log.e("ApiHelper", "DELETE api init");
        Log.e("ApiHelper", payload.toString());
        return new JSONObject();
    }

    private static JSONObject Put(JSONObject payload, String url) {

        Log.e("ApiHelper", "PUT api init");
        Log.e("ApiHelper", payload.toString());
        return new JSONObject();
    }

}
