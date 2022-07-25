package com.example.mysensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.mysensor.sensorData.Data;

public class UltraSensorActivity extends AppCompatActivity {
    private RecyclerAdapter adapter;
    private String serverUrl="http://220.69.207.117/sensor.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultra_sensor);

        init();
        getData();

    }

    private void init(){
        RecyclerView recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData(){
        if(AppHelper.queue==null)
            AppHelper.queue= Volley.newRequestQueue(this);       //volley로 부터 RequestQueue 제공 받음.

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, serverUrl,null,
                response -> {
            try{
                for(int i=0;i<response.length();i++){
                    Data data=new Data();
                    data.setDateTime(response.getJSONObject(i).getString("Datetime")+"\n");
                    data.setDistanceValue(response.getJSONObject(i).getString("Distance")+"\n");
                    data.setLedValue(response.getJSONObject(i).getString("Led")+"\n");

                    switch (response.getJSONObject(i).getString("Led").toUpperCase()) {
                        case "RED":
                            data.setLedColor(R.drawable.edge_red);
                            break;

                        case "GREEN":
                            data.setLedColor(R.drawable.edge_green);
                            break;

                        case "BLUE":
                            data.setLedColor(R.drawable.edge_blue);
                            break;

                        case "WHITE":
                            data.setLedColor(R.drawable.edge_white);
                            break;
                    }
                    adapter.addItem(data);

                }
                adapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }
        },error -> {
            System.out.println(error.getMessage());
        });
        AppHelper.queue.add(jsonArrayRequest);
    }
}



