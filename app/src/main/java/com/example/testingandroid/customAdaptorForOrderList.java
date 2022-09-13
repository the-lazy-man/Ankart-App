package com.example.testingandroid;

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
class customAdapterForOrderlist extends ArrayAdapter<DataModel> implements View.OnClickListener {
    private ArrayList<DataModel> dataSet;
    Context mContext;
//    private int lastPosition = -1;
    // View lookup cache

    public customAdapterForOrderlist(Context context, ArrayList<DataModel> data) {
        super(context, R.layout.ordertextview, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        System.out.println("test4");
        int position = (int) v.getTag();
        Object object = getItem(position);
        DataModel dataModel = (DataModel) object;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        DataModel dataModel = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.ordertextview, parent, false);
            ((TextView) convertView.findViewById(R.id.item_name)).setText(dataModel.getName());
            ((TextView) convertView.findViewById(R.id.item_quantity)).setText(String.format("%d", dataModel.getQuantity()));
            ((TextView) convertView.findViewById(R.id.item_price)).setText(String.format("%.2f", dataModel.getPrice()));
            Picasso.with(getContext()).load(dataModel.getImgURL()).into((ImageView) convertView.findViewById(R.id.item_image));
        }
        return convertView;
    }
}

