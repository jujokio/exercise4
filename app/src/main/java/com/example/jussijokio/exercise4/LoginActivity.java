package com.example.jussijokio.exercise4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    public TextView username;
    public TextView password;
    public Button loginBtn;
    public Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (TextView) findViewById(R.id.SaveMessageField);
        password = (TextView) findViewById(R.id.ReceiveMessageField);
        loginBtn = (Button) findViewById(R.id.SaveMessage);
        registerBtn = (Button) findViewById(R.id.SaveMessage);


    }
}
