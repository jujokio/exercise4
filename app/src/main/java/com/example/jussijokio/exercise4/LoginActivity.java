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

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public TextView username;
    public TextView password;
    public Button loginBtn;
    public Button registerBtn;
    public ApiHelper apihelper;
    public JSONObject payload;
    public JSONObject result;

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
                payload = new JSONObject();
                result = null;
                try {
                    payload.put("username", username.getText().toString());
                    payload.put("password", password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ApiHelper", e.toString());
                    return false;
                }
                Log.e("ApiHelper", payload.toString());
                Object[] params = new Object[3];
                params[0] = "POST";
                params[1] = payload;
                params[2] = "users/createuser";


                final Handler handOfDoom = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.e("ApiHelper", "Runnable is fired!");
                        ApiHelper.AsyncApi asyncapi  = new ApiHelper.AsyncApi(new ApiHelper.APIResponse() {

                            @Override
                            public void processFinish(JSONObject output) {
                                result = output;
                                Log.e("ApiHelper", result.toString());
                            }
                        });
                        asyncapi.execute("65.060543","25.466227");
                        handOfDoom.postDelayed(this, 10000);
                    }
                };
                handOfDoom.post(runnable);
            }
        }
        if (result != null){
            return true;
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
