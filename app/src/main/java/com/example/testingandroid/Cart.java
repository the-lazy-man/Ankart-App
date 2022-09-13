package com.example.testingandroid;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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


public class Cart extends AppCompatActivity {
    ArrayList<DataModel> cartItemList;
    private customAdapterForCart adapter;
    private ListView listView;
    private Button viewOrder;
    public String email;
    private final OkHttpClient client = new OkHttpClient();
    private static final String CART_ITEM_LIST_API_PATH = "http://192.168.43.137:5000/Cart_items";

    enum ResponseType {
        SUCCESS,
        FAILURE,
        UNSURE
    }
    private ResponseType responseType = ResponseType.UNSURE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        listView = findViewById(R.id.listview);
        viewOrder = findViewById(R.id.view_order);
        Intent intent = getIntent();
//        String userEmail = intent.getStringExtra("userEmail");
        String userEmail = "ankitmail";
        cartListView(userEmail);
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
        viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ViewOrderList.class);
                intent.putExtra("userEmail",userEmail);
                startActivity(intent);
            }
        });
    }
    public String getEmail() {
        return email;
    }
    private void cartListView(String userEmail) {
        cartItemList = new ArrayList<>();
        Request cartItemListReq = buildGetItemListRequest(userEmail);
        client.newCall(cartItemListReq).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    cartItemList = responseArray(response.body());
                    adapter = new customAdapterForCart(getApplicationContext(), cartItemList);
                    responseType = ResponseType.SUCCESS;
                } else {
//                    Toast.makeText(getApplicationContext(), "There's nothing in the cart to show", Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), "Failed to get response from the server", Toast.LENGTH_LONG).show();
                    responseType = ResponseType.FAILURE;
                }
            }
        });
    }

    private Request buildGetItemListRequest(String userEmail) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(CART_ITEM_LIST_API_PATH).newBuilder();
        urlBuilder.addQueryParameter("email",userEmail);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    private ArrayList<DataModel> responseArray(ResponseBody resp) {
        try {
            JSONObject respObj = new JSONObject(resp.string());
            JSONArray jsonArray = respObj.getJSONArray("data");
            ArrayList<DataModel> cartItemList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cartItemInfo = jsonArray.getJSONObject(i);
                String name = cartItemInfo.getString("name");
                float price = (float) cartItemInfo.getDouble("price");
                int quantity = (int) cartItemInfo.getInt("quantity");
                //TODO: fetch image URL from server.
                cartItemList.add(new DataModel(name, price, quantity));
            }
            return cartItemList;
        } catch (JSONException | IOException e) {
            System.out.println("Failed to decode resp string into JSON.");
            e.printStackTrace();
            return null;
        }
    }
}