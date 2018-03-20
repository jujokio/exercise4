package com.example.jussijokio.exercise4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public TextView username;
    public TextView password;
    public Button loginBtn;
    public Button registerBtn;
    public ApiHelper apihelper;

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
                    Intent gotoMain = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(gotoMain);
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
                JSONObject payload = new JSONObject();
                try {
                    payload.put("username", username.getText().toString());
                    payload.put("password", password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
                Log.e("ApiHelper", payload.toString());
                JSONObject result = apihelper.Post(payload, "users/createuser");
                Log.e("ApiHelper", result.toString());
                if (result != null) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean CheckValidLogin(){

        if(username.getText().length()>= 1){
            if(password.getText().length()>= 1){



                Toast.makeText(getBaseContext(),"Login ok!!!",
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
