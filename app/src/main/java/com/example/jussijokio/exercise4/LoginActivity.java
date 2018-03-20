package com.example.jussijokio.exercise4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public TextView username;
    public TextView password;
    public Button loginBtn;
    public Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (TextView) findViewById(R.id.UsernameField);
        password = (TextView) findViewById(R.id.PasswordField);
        loginBtn = (Button) findViewById(R.id.LoginButton);
        registerBtn = (Button) findViewById(R.id.RegisterButton);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"GO TO REGISTER",
                        Toast.LENGTH_SHORT).show();
                Intent gotoMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(gotoMain);
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
