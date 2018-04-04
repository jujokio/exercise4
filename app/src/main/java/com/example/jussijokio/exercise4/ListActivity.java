package com.example.jussijokio.exercise4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    ListView simpleListView;
    int[] Images = {R.mipmap.ic_launcher}; //Hardset Image for every user
    String[] username; //List of users close by (harcoded at the moment)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String jsonArray = intent.getStringExtra("nearbyUsers");
        Log.e("jsonarray", jsonArray);
        try {
            JSONArray array = new JSONArray(jsonArray);
            username=toStringArray(array);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_list);
        simpleListView = (ListView) findViewById(R.id.list_item);

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        for (String anUsername : username) {
            HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
            hashMap.put("name", anUsername);
            hashMap.put("image", Images[0] + "");
            arrayList.add(hashMap);//add the hashmap into arrayList
        }
        String[] from = {"name", "image"};//string array
        int[] to = {R.id.view_username, R.id.imageView};//int array of views id's
        Log.e("listviewbug","begin of adapter");
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.custom_layout, from, to);//Create object and set the parameters for simpleAdapter
        Log.e("listviewbug","middle of adapter");
        simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView
        Log.e("listviewbug","end of adapter");
        Log.e("listviewbug","end of adapter");
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
}