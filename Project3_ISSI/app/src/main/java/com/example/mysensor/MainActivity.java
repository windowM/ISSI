package com.example.mysensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button ultra;
    Button shop;
    Button senData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ultra = findViewById(R.id.ultrasonic);
        shop = findViewById(R.id.shopping);
        senData = findViewById(R.id.sendata);

    }
    public void onClick(View view) {
        Intent intent ;
        switch (view.getId()) {
            case R.id.ultrasonic:
                intent = new Intent(this, UltraSensorActivity.class);
                break;
            case R.id.shopping:
                 intent = new Intent(this, HyunShop.class);
                 break;
            case R.id.sendata:
                intent = new Intent(this, JsonUltraData.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
        startActivity(intent);
    }
}