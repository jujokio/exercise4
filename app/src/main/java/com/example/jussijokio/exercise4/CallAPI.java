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

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by gorinds on 28/03/2018.
 */

public class CallAPI extends AsyncTask<String, String, String> {
    private static final String apiUrl = "https://hangouts-mobisocial-18.herokuapp.com/";
    JSONObject payload;
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
        Log.d("Result", result);
        delegate.processFinish(result);
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            URL url = new URL(apiUrl + params[0]);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("username", "httpUkko")
                    .appendQueryParameter("password", "testii");
            String query = builder.build().getEncodedQuery();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", "HTTPUKKO3");
            jsonParam.put("password", "SALIS");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(payload.toString());
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String content = "", line;
            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }

            Log.d("Content", content);
            return content;


        } catch (Exception e) {

            System.out.println(e.getMessage());


        }

        return "heloo";
    }

    public void setPayload(JSONObject parJson){
        payload = parJson;
    }
}
