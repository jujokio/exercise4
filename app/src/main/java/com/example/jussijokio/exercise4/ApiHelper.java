package com.example.jussijokio.exercise4;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jussi Jokio on 20.3.2018.
 */

public class ApiHelper {
    static final String apiUrl = "https://hangouts-mobisocial-18.herokuapp.com/";

    public interface APIResponse {
        void processFinish(JSONObject output);
    }

    static class AsyncApi extends AsyncTask {
        public APIResponse apiResponse;

        public AsyncApi(APIResponse response){
            Log.e("ApiHelper", "async init");
            apiResponse = response;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.e("ApiHelper", "do in background init");
            String method = (String) objects[0];
            JSONObject payload = (JSONObject) objects[1];
            String url = (String) objects[2];
            Log.e("ApiHelper", "Using HTTp-method: " + method);
            JSONObject result;
            if (method == "POST") {
                result = Post(payload, url);
            } else if (method == "GET") {
                result = Get(payload, url);
            } else if (method == "GET") {
                result = Get(payload, url);
            } else if (method == "DELETE") {
                result = Delete(payload, url);
            } else if (method == "PUT") {
                result = Put(payload, url);
            } else {
                result = null;
            }
            return result;

        }
    }


    private static JSONObject Post(JSONObject payload, String url) {
        Log.e("ApiHelper", "post api init");
        Log.e("ApiHelper", payload.toString());

        URL apiurl;
        HttpURLConnection connection;
        try {
            apiurl = new URL(String.format(apiUrl + url));
            connection = (HttpURLConnection) apiurl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            for (int i = 0; i < payload.names().length(); i++) {
                String key = payload.names().getString(i);
                String value = payload.get(payload.names().getString(i)).toString();
                connection.setRequestProperty(key, value);
                Log.e("ApiHelper", "key = " + key);
                Log.e("ApiHelper", "value = " + value);

            }/*

            // Send POST output.
            printout = new DataOutputStream(urlConn.getOutputStream ());
            printout.writeBytes(URLEncoder.encode(jsonParam.toString(),"UTF-8"));
            printout.flush ();
            printout.close ();


                final Handler handOfDoom = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.e("ApiHelper", "Runnable is fired!");
                        ApiHelper.AsyncApi asyncapi  = new ApiHelper.AsyncApi(new ApiHelper.APIResponse() {

                            @Override
                            public void processFinish(JSONObject output) {
                                Log.e("ApiHelper", "process finished");
                                result = output;
                                Log.e("ApiHelper", result.toString());
                            }
                        });

                        payload = new JSONObject();
                        result = null;
                        try {
                            payload.put("username", username.getText().toString());
                            payload.put("password", password.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ApiHelper", e.toString());
                        }
                        Log.e("ApiHelper", payload.toString());
                        Object[] params = new Object[3];
                        params[0] = "POST";
                        params[1] = payload;
                        params[2] = "users/createuser";
                        asyncapi.execute(params);

                        handOfDoom.postDelayed(this, 10000);
                    }
                };
                handOfDoom.post(runnable);
                */


            BufferedReader responseReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer jsonReader = new StringBuffer(1024);
            String line = "";
            while ((line = responseReader.readLine()) != null) {
                jsonReader.append(line).append("\n");
            }
            responseReader.close();
            JSONObject result = new JSONObject(jsonReader.toString());

            if (result.getInt("cod") != 200) {
                return result = new JSONObject("Error:404 not found");
            } else {
                return result;
            }

        } catch (Exception e) {
            Log.e("ApiHelper", "error in Post");
            Log.e("ApiHelper", e.toString());
            return null;
        }
    }


    private static JSONObject Get(JSONObject payload, String url) {
        return new JSONObject();
    }

    private static JSONObject Delete(JSONObject payload, String url) {
        return new JSONObject();
    }

    private static JSONObject Put(JSONObject payload, String url) {
        return new JSONObject();
    }

}
