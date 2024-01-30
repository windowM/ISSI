package com.example.mysensor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Tshirt extends AppCompatActivity {
    private String Url="http://220.69.207.117/myshop.php";
    private TextView text2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tshirt);

        text2=findViewById(R.id.textView);

        RequestQueue queue= Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,response -> {
            text2.setText("ultrasonic:" + response);
        },error -> {
            text2.setText("error : "+error.toString());
        });
        queue.add(stringRequest);
    }
}