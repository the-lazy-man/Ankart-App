package com.example.testingandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.appsearch.ReportSystemUsageRequest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private static final String LOGIN_API_PATH = "http://192.168.43.137:5000/checklogin";
    Button loginButton;
    EditText emailEditText;
    EditText passwordEditText;
    TextView signupOpener;
    Boolean isLoggedIn;
    Boolean invalidCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        isLoggedIn = false;
        invalidCredentials = false;
        loginButton = findViewById(R.id.login);
        emailEditText= findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        signupOpener = findViewById(R.id.signup_activity_opener);

        setupLoginButtonClickListener();
        setupSignupOpenerListener();
    }

    private void setupLoginButtonClickListener(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("Clicked onClick listener");
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                verifyLogin(email,password);
//                System.out.println("verify login ke aage chal rha");
                long sleepTimer = 100l;
                while(!isLoggedIn && !invalidCredentials){
                    try {
                        Thread.sleep(sleepTimer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sleepTimer += 100;
                }
//                System.out.println("islogged in true ho gya");
                if(isLoggedIn) {
                    String emailForCart = emailEditText.getText().toString();
//                    System.out.println("user mail is " +emailForCart);
                    Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
                    intent.putExtra("userEmail",emailForCart);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "Login details are correct", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Invalid email/password", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void setupSignupOpenerListener(){
        signupOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void verifyLogin(String email, String password) {
//        System.out.println("email = " +email);
//        System.out.println("password  = " +password);
//        System.out.println("verify login chal rha");
        Request loginReq = buildLoginRequest(email, password);
        client.newCall(loginReq).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("response fail ho ja rha");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && isLoggedIn(response.body())) {
                    isLoggedIn = true;
                }
                else {
                    System.out.println("response isSuccessful:" + response.isSuccessful());
                    System.out.println("response:" + response.toString());
                    System.out.println("Response body" + response.body().toString());
                    System.out.println("IsLoggedIn: " + isLoggedIn(response.body()));
                    invalidCredentials = true;
                }
            }
        }
        );
    }
    private Request buildLoginRequest(String email, String password){
        System.out.println("request chal rha");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(LOGIN_API_PATH).newBuilder();
        urlBuilder.addQueryParameter("email",email);
        urlBuilder.addQueryParameter("password",password);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    private Boolean isLoggedIn(ResponseBody resp) {
        try {
            JSONObject respObj = new JSONObject(resp.string());
            System.out.println("resp = "+respObj.get("data"));
            System.out.println("response chal rha");
            return ((int) respObj.get("data")) == 1;
        } catch (JSONException | IOException e){
            System.out.println("Failed to decode resp string into JSON.");
            e.printStackTrace();
            return false;
        }
    }
}