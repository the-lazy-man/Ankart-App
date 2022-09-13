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


public class ViewOrderList extends AppCompatActivity {
    ArrayList<DataModel> orderItemList;
    private customAdapterForOrderlist adapter;
    private ListView listView;
    private final OkHttpClient client = new OkHttpClient();
    private static final String ORDER_ITEM_LIST_API_PATH = "http://192.168.43.137:5000/Order_items";

    enum ResponseType {
        SUCCESS,
        FAILURE,
        UNSURE
    }
    private ResponseType responseType = ResponseType.UNSURE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_list);
        listView = findViewById(R.id.listview);
//        Intent intent = getIntent();
//        String userEmail = intent.getStringExtra("userEmail");
        String userEmail = "ankitmail";
        orderListView(userEmail);
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
                System.out.println("test3");
            }
        });

    }
    private void orderListView(String email) {
        orderItemList = new ArrayList<>();
        Request orderItemListReq = buildGetItemListRequest(email);
        client.newCall(orderItemListReq).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    orderItemList = responseArray(response.body());
                    adapter = new customAdapterForOrderlist(getApplicationContext(), orderItemList);
                    responseType = ResponseType.SUCCESS;
                } else {
//                    Toast.makeText(getApplicationContext(), "There's no order to show", Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), "Failed to get response from the server", Toast.LENGTH_LONG).show();
                    responseType = ResponseType.FAILURE;
                }
            }
        });
    }

    private Request buildGetItemListRequest(String email) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ORDER_ITEM_LIST_API_PATH).newBuilder();
        urlBuilder.addQueryParameter("email",email);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    private ArrayList<DataModel> responseArray(ResponseBody resp) {
        try {
            JSONObject respObj = new JSONObject(resp.string());
            JSONArray jsonArray = respObj.getJSONArray("data");
            ArrayList<DataModel> orderItemList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject orderItemInfo = jsonArray.getJSONObject(i);
                String name = orderItemInfo.getString("name");
                float price = (float) orderItemInfo.getDouble("price");
                int quantity = (int) orderItemInfo.getInt("quantity");
                //TODO: fetch image URL from server.
                orderItemList.add(new DataModel(name, price, quantity));
            }
            return orderItemList;
        } catch (JSONException | IOException e) {
            System.out.println("Failed to decode resp string into JSON.");
            e.printStackTrace();
            return null;
        }
    }
}