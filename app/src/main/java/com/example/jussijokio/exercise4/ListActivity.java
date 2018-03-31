package com.example.jussijokio.exercise4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {

    ListView simpleListView;
    int[] Images = {R.drawable.ic_launcher_foreground}; //Hardset Image for every user
    String[] username = {"Jussi", "Eetu", "Markus", "Tolvana","Jussi", "Eetu", "Markus", "Tolvana"}; //List of users close by (harcoded at the moment)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        simpleListView = (ListView) findViewById(R.id.list_item);

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        for (int i = 0; i < username.length; i++) {
            HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
            hashMap.put("name", username[i]);
            hashMap.put("image", Images[0] +"");
            arrayList.add(hashMap);//add the hashmap into arrayList
        }
        String[] from = {"name", "image"};//string array
        int[] to = {R.id.view_username, R.id.imageView};//int array of views id's
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.custom_layout, from, to);//Create object and set the parameters for simpleAdapter
        simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView
    }
}