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

public class ApiHelper extends AsyncTask {
    public final String apiUrl = "https://hangouts-mobisocial-18.herokuapp.com/";


    public JSONObject Post(JSONObject payload, String url){
        Log.e("ApiHelper", "post api init");
        Log.e("ApiHelper", payload.toString());

            URL apiurl;
            HttpURLConnection connection;
            try {
                apiurl = new URL(String.format(apiUrl+url));
                connection = (HttpURLConnection)apiurl.openConnection();
                connection.setRequestMethod("POST");

                for (int i = 0; i<payload.names().length(); i++){
                    String key = payload.names().getString(i);
                    String value = payload.get(payload.names().getString(i)).toString();
                    connection.setRequestProperty(key,value);
                    Log.e("ApiHelper","key = "+key);
                    Log.e("ApiHelper","value = "+value);

                }

                BufferedReader responseReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer jsonReader = new StringBuffer(1024);
                String line="";
                while((line=responseReader.readLine())!=null){
                    jsonReader.append(line).append("\n");
                }
                responseReader.close();
                JSONObject result = new JSONObject(jsonReader.toString());

                if(result.getInt("cod") != 200){
                    return result = new JSONObject("Error:404 not found");
                }else{
                    return result;
                }

            }catch(Exception e){
                Log.e("ApiHelper","error in getWeather");
                Log.e("ApiHelper",e.toString());
                return null;
            }

        }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
