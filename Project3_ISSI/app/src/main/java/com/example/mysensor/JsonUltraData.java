package com.example.mysensor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.ArrayList;

public class JsonUltraData extends AppCompatActivity {
    private TextView text1;
    private String serverUrl="http://220.69.207.117/sensor.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_ultra_data);

        text1=findViewById(R.id.textView);
        if(AppHelper.queue==null)
            AppHelper.queue= Volley.newRequestQueue(this);       //volley로 부터 RequestQueue 제공 받음.

        StringRequest stringRequest = new StringRequest( serverUrl,response -> {
            text1.setText("ultrasonic:"+response);
        },error -> {
            text1.setText("error : "+error.toString());
        });
        AppHelper.queue.add(stringRequest);
    }
}