package com.example.testingandroid;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
class customAdapterForCart extends ArrayAdapter<DataModel> implements View.OnClickListener {
    private ArrayList<DataModel> dataSet;
    Context mContext;
//    private int lastPosition = -1;
    // View lookup cache

    public customAdapterForCart(Context context, ArrayList<DataModel> data) {
        super(context, R.layout.cart_textview, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        System.out.println("test2");
        int position = (int) v.getTag();
        Object object = getItem(position);
        DataModel dataModel = (DataModel) object;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        DataModel dataModel = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.cart_textview, parent, false);
            ((TextView) convertView.findViewById(R.id.item_name)).setText(dataModel.getName());
            ((TextView) convertView.findViewById(R.id.item_quantity)).setText(String.format("%d", dataModel.getQuantity()));
            TextView quantityTextView = convertView.findViewById(R.id.item_quantity);
            int Quantity = Integer.parseInt(quantityTextView.getText().toString());
            ((TextView) convertView.findViewById(R.id.item_price)).setText(String.format("%.2f", dataModel.getPrice() * Quantity));
            Picasso.with(getContext()).load(dataModel.getImgURL()).into((ImageView) convertView.findViewById(R.id.item_image));
            setQuantityChangeOnClickListener(convertView, position, parent);
            placeOrderOnClickListener(convertView, position, parent);
        }
        return convertView;
    }
    private void setQuantityChangeOnClickListener(View view, final int position, final ViewGroup parent) {
        view.findViewById(R.id.add).setOnClickListener(v -> {
            TextView quantityTextView = view.findViewById(R.id.item_quantity);
            int oldQuantity = Integer.parseInt(quantityTextView.getText().toString());
            int newQuantity = oldQuantity + 1;

            quantityTextView.setText(String.valueOf(newQuantity));
            TextView priceTextView = view.findViewById(R.id.item_price);
            float oldPrice = Float.parseFloat(priceTextView.getText().toString());
            float newPrice = (oldPrice / oldQuantity) * newQuantity;
            priceTextView.setText(String.valueOf(newPrice));
            ((ListView) parent).performItemClick(v, position, view.getId());
        });

        view.findViewById(R.id.subtract).setOnClickListener(v -> {
            TextView quantityTextView = view.findViewById(R.id.item_quantity);
            int oldQuantity = Integer.parseInt(quantityTextView.getText().toString());
            if(oldQuantity > 0) {
                int newQuantity = oldQuantity - 1;

                quantityTextView.setText(String.valueOf(newQuantity));
                TextView priceTextView = view.findViewById(R.id.item_price);
                float oldPrice = Float.parseFloat(priceTextView.getText().toString());
                float newPrice = (oldPrice / oldQuantity) * newQuantity;
                priceTextView.setText(String.valueOf(newPrice));
            }
            else Toast.makeText(getContext(), "Minimum quantity reached", Toast.LENGTH_SHORT).show();
            ((ListView) parent).performItemClick(v, position, view.getId());
        });
    }
    enum ResponseType {
        SUCCESS,
        FAILURE,
        UNSURE
    }
    private CustomAdapter.ResponseType responseType = CustomAdapter.ResponseType.UNSURE;
    private void placeOrderOnClickListener(View view, final int position, final ViewGroup parent) {
        final OkHttpClient client = new OkHttpClient();

        view.findViewById(R.id.addToOrderList).setOnClickListener(v -> {
            TextView nameTextView = view.findViewById(R.id.item_name);
            String item_name = (nameTextView.getText().toString());
            TextView quantityTextView = view.findViewById(R.id.item_quantity);
            int item_quantity = Integer. parseInt(quantityTextView.getText().toString());
            TextView priceTextView = view.findViewById(R.id.item_price);
            float item_price = Float. parseFloat(priceTextView.getText().toString());
            System.out.println("price after click on plus sign " +item_price);
            Cart emailObj = new Cart();
            String userEmail = emailObj.getEmail();
//            String userEmail = "ankitmail";
            System.out.println("user email is " +userEmail);
            Request placeOrderReq = placeOrderRequest(userEmail, item_name, item_quantity, item_price);
            client.newCall(placeOrderReq).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() ) {
                        if(isOrderPlaced(response.body())){
//                            Toast.makeText(mContext.getApplicationContext(), "Order succesfully placed", Toast.LENGTH_SHORT).show();
                            responseType = CustomAdapter.ResponseType.SUCCESS;
                        }
                        else {
                            responseType = CustomAdapter.ResponseType.FAILURE;
                            System.out.println("Some error occured (maybe the email is wrong)");
                            System.out.println("response isSuccessful:" + response.isSuccessful());
                            System.out.println("response:" + response.toString());
                            System.out.println("Response body" + response.body().toString());
                        }
                    }
                }
            });
            long sleepTime = 100;
            while(responseType == CustomAdapter.ResponseType.UNSURE){
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sleepTime += 100l;
            }
            ((ListView) parent).performItemClick(v, position, view.getId());
        });
    }
    private Request placeOrderRequest(String userEmail, String item_name, int item_quantity, float item_price) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(" http://192.168.43.137:5000/placeOrder").newBuilder();
        urlBuilder.addQueryParameter("email",userEmail);
        urlBuilder.addQueryParameter("name",item_name);
        urlBuilder.addQueryParameter("quantity", String.valueOf(item_quantity));
        urlBuilder.addQueryParameter("price", String.valueOf(item_price));
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }
    private Boolean isOrderPlaced (ResponseBody resp) {
        try {
            JSONObject respObj = new JSONObject(resp.string());
            System.out.println("resp = "+respObj.get("data"));
            return ((int)respObj.get("data") ) == 1;
        } catch (JSONException | IOException e){
            System.out.println("Failed to decode resp string into JSON.");
            e.printStackTrace();
            return false;
        }
    }


}

