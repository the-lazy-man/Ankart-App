

package com.example.testingandroid;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class HomePageActivity extends AppCompatActivity {
    ArrayList<DataModel> itemInfoList;
    private CustomAdapter adapter;
    private ListView listView;
    private final OkHttpClient client = new OkHttpClient();
    private static final String ITEM_LIST_API_PATH = "http://192.168.43.137:5000/item";
    private Button cartViewButton;
    public  String email;
    enum ResponseType {
        SUCCESS,
        FAILURE,
        UNSURE
    }
    private ResponseType responseType = ResponseType.UNSURE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        listView = findViewById(R.id.listview);
        populateListView();
        long sleepTime = 100;
        while(responseType == ResponseType.UNSURE){
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sleepTime += 100l;
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("test");
            }
        });
        // below is view cart button activity code
        cartViewButton = findViewById(R.id.view_cart);
        Intent intent = getIntent();
       email = intent.getStringExtra("userEmail");
//        System.out.println("email from login activity before getEMAIL: " +email);
        System.out.println("email from login activity after getEMAIL: " +getEmail());
        cartViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Cart.class);
                intent.putExtra("userEmail",email);
                startActivity(intent);
//                System.out.println("Clicked onClick listener");
//                Intent intent = getIntent();
//                String email = intent.getStringExtra("userEmail");
//                getCartView(email);
//                long sleepTimer = 100l;
//                while(viewCartResponse == viewCartResponse.UNSURE){
//                    try {
//                        sleep(sleepTime);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    sleepTimer += 100l;
//                }
//                if(viewCartResponse == viewCartResponse.SUCCESS) {
//                    String emailForCart = emailEditText.getText().toString();
//                    Intent intent = new Intent(getApplicationContext(),HomePageActivity.class);
//                    intent.putExtra("userEmail",emailForCart);
//                    startActivity(intent);
//                }
//                else {
//                    Toast.makeText(HomePageActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }
//    Intent intent = getIntent();
//    email = intent.getStringExtra("userEmail");
//    Bundle bundle = getIntent().getExtras();
//    String getEmail() {
//        System.out.println("email from login activity : " +email);
//        return email;
//    }
    private void populateListView() {
        itemInfoList = new ArrayList<>();
        Request itemListReq = buildGetItemListRequest();
        client.newCall(itemListReq).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    itemInfoList = responseArray(response.body());
                    adapter = new CustomAdapter(getApplicationContext(), itemInfoList);
                    responseType = ResponseType.SUCCESS;
                    System.out.println("chal rha");
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get response from the server", Toast.LENGTH_LONG).show();
                    responseType = ResponseType.FAILURE;
                }
            }
        });
    }
    String getEmail() {
        System.out.println("email from login activity : " +email);
        return email;
    }
    private Request buildGetItemListRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ITEM_LIST_API_PATH).newBuilder();
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    private ArrayList<DataModel> responseArray(ResponseBody resp) {
        try {
            JSONObject respObj = new JSONObject(resp.string());
            JSONArray jsonArray = respObj.getJSONArray("data");
            ArrayList<DataModel> itemInfoList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemInfo = jsonArray.getJSONObject(i);
                String name = itemInfo.getString("name");
                float price = (float) itemInfo.getDouble("price");
                int quantity = 0;
                //TODO: fetch image URL from server.
                itemInfoList.add(new DataModel(name, price, quantity));
            }
            return itemInfoList;
        } catch (JSONException | IOException e) {
            System.out.println("Failed to decode resp string into JSON.");
            e.printStackTrace();
            return null;
        }
    }
}