package com.example.jussijokio.exercise4;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public TextView username;
    public TextView password;
    public Button loginBtn;
    public Button registerBtn;
    public ApiHelper apihelper;
    public JSONObject payload;
    public JSONObject response;
    static final String baseApiUrl = "https://hangouts-mobisocial-18.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (TextView) findViewById(R.id.UsernameField);
        password = (TextView) findViewById(R.id.PasswordField);
        loginBtn = (Button) findViewById(R.id.LoginButton);
        registerBtn = (Button) findViewById(R.id.RegisterButton);
        apihelper = new ApiHelper();
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


    }

    private boolean CheckValidRegister() {
        if(username.getText().length()>= 1) {
            if (password.getText().length() >= 1) {
                Log.e("ApiHelper", "post init");
                response = PostJSON();
                if (response != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private JSONObject PostJSON() {
        Thread thread = new Thread(new Runnable() {
            public JSONObject result;
            @Override
            public void run() {
                try {
                    URL url = new URL(baseApiUrl+"users/createuser");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("username",username.getText());
                    jsonParam.put("password",password.getText());

                    Log.e("ApiHelper", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.e("ApiHelper", String.valueOf(conn.getResponseCode()));
                    Log.e("ApiHelper" , conn.getResponseMessage());
                    result = new JSONObject(conn.getResponseMessage());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            public JSONObject getResult(){
                return result;
            }


        });

        thread.start();
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
}
