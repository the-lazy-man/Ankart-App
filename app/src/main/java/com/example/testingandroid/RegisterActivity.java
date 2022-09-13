package com.example.testingandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class RegisterActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private static final String REGISTER_API_PATH = " http://192.168.43.137:5000/register_user";
    private Button registerButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private TextView alreadyHaveAccount;
    private Boolean isRegistered;
    private Boolean invalidCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        isRegistered = false;
        invalidCredentials = false;
        registerButton = findViewById(R.id.register);
        emailEditText= findViewById(R.id.email);
        usernameEditText= findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount_activity_opener);

        setupRegisterButtonClickListener();
        AlreadyHaveAccountClickListener();
//        final Context context = this;
//        Button registerbtn = findViewById(R.id.register);
//        EditText emailEditText= findViewById(R.id.email);
//        EditText passwordEditText = findViewById(R.id.password);
//        EditText usernameEditText= findViewById(R.id.username);
//        registerbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("Clicked onClick listener");
//                String email = emailEditText.getText().toString();
//                String password = passwordEditText.getText().toString();
//                String username = emailEditText.getText().toString();
//                registerUser(email,username,password);
//            }
        }
    private void AlreadyHaveAccountClickListener(){
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
    private void setupRegisterButtonClickListener(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Clicked onClick listener");
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                verifyRegister(username,email,password);
                long sleepTimer = 100l;
                while(!isRegistered && !invalidCredentials){
                    try {
                        Thread.sleep(sleepTimer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sleepTimer += 100;
                }
                if(isRegistered) {
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
                else if(invalidCredentials) {
                    Toast.makeText(RegisterActivity.this, "User already registered", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "This should not happen", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void verifyRegister(String username, String email, String password) {
        Request registerReq = buildRegisterRequest(username,email, password);
        client.newCall(registerReq).enqueue(new Callback() {
             @Override
             public void onFailure(@NonNull Call call, @NonNull IOException e) {
                 e.printStackTrace();
             }

             @Override
             public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                 if (response.isSuccessful() && isRegistered(response.body())) {
                     isRegistered = true;
                 }
                 else {
                     System.out.println("response isSuccessful:" + response.isSuccessful());
                     System.out.println("response:" + response.toString());
                     System.out.println("Response body" + response.body().string());
                     System.out.println("IsRegisteredIn: " + isRegistered(response.body()));
                     invalidCredentials = true;
                 }
             }
         }
        );
    }
    private Request buildRegisterRequest(String username,String email, String password){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(REGISTER_API_PATH).newBuilder();
        urlBuilder.addQueryParameter("username",username);
        urlBuilder.addQueryParameter("email",email);
        urlBuilder.addQueryParameter("password",password);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }
    private Boolean isRegistered(ResponseBody resp){
        try {
            JSONObject respObj = new JSONObject(resp.string());
            return ((int) respObj.get("data")) == 0;
        } catch (IOException | JSONException e){
            System.out.println("Failed to decode resp string into JSON.");
            e.printStackTrace();
            return false;
        }
    }
//        TextView btn = findViewById(R.id.alreadyhaveanaccount);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(registerActivity.this,loginActivity.class));
//            }
//        });
//    }
//    private void registerUser(String email, String username, String password) {
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://192.168.86.26:5000/register_user").newBuilder();
//        urlBuilder.addQueryParameter("email",email);
//        urlBuilder.addQueryParameter("email",username);
//        urlBuilder.addQueryParameter("password",password);
//        String url = urlBuilder.build().toString();
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                e.printStackTrace();
//            }
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    ResponseBody register_response = response.body();
//                    JSONObject resp = null;
//                    try {
//                        resp = new JSONObject(register_response.string());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        System.out.println(resp.get("data"));
//                        String result = resp.get("data").toString();
//                        Button registerBtn = findViewById(R.id.register);
//                        registerBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                System.out.println("result = " +result);
//                                if(result == "0")
//                                    Toast.makeText(registerActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
//                                else{
//                                    Toast.makeText(registerActivity.this, "User Successfully registered", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(registerActivity.this, loginActivity.class));
//                                }
//                            }
//                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
//    }
}