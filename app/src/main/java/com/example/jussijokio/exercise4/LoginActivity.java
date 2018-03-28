package com.example.jussijokio.exercise4;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {

    private ProgressBar spinner;
    public TextView username;
    public TextView password;
    public Button loginBtn;
    public Button registerBtn;
    public CallAPI apihelper;
    public JSONObject payload;
    public JSONObject response;
    private TextView mInfoText;
    static final String baseApiUrl = "https://hangouts-mobisocial-18.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (TextView) findViewById(R.id.UsernameField);
        password = (TextView) findViewById(R.id.PasswordField);
        loginBtn = (Button) findViewById(R.id.LoginButton);
        registerBtn = (Button) findViewById(R.id.RegisterButton);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        mInfoText = findViewById(R.id.tv_Login_info);

        apihelper = new CallAPI();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckValidRegister()) {
                    Toast.makeText(getBaseContext(),"GO TO REGISTER",
                            Toast.LENGTH_SHORT).show();
                    GoToMain();
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().length()>= 1){
                    if(password.getText().length()>= 1){
                        if(CheckValidLogin()){
                            GoToMain();
                        }
                    }
                    else{
                        Toast.makeText(getBaseContext(),"Give password please!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getBaseContext(),"Give username please!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        CallAPI asyncTask = new CallAPI();
        asyncTask.delegate = this;
        asyncTask.execute("users/createuser");


    }

    private boolean CheckValidRegister() {
        if(username.getText().length()>= 1) {
            if (password.getText().length() >= 1) {
                Log.e("ApiHelper", "check registering...");
                Log.e("ApiHelper", "Spinner on");
                spinner.setVisibility(View.VISIBLE);

                //call API
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("username",username.getText());
                    jsonParam.put("password",password.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ApiHelper", e.toString());
                }

                apihelper.setPayload(jsonParam);

                apihelper.delegate = this;
                apihelper.execute("users/createuser");
                spinner.setVisibility(View.GONE);
                Log.e("ApiHelper", "spinner off");
                if (response != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private JSONObject PostJSON() {


        return null;

    }


    public boolean CheckValidLogin() {
        if (username.getText().length() >= 1) {
            if (password.getText().length() >= 1) {
                Toast.makeText(getBaseContext(), "Login ok!!!",
                        Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

    private void GoToMain(){
        Intent gotoMain = new Intent(this, MainActivity.class);
        startActivity(gotoMain);
    }

    @Override
    public void processFinish(String output) {
        mInfoText.setText(output);
    }
}
