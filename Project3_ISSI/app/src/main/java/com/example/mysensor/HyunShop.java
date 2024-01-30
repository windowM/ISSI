package com.example.mysensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mysensor.shoppinghome.BMenuFragment1;
import com.example.mysensor.shoppinghome.BMenuFragment2;
import com.example.mysensor.shoppinghome.BMenuFragment3;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HyunShop extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    //FrameLayout에 각 메뉴의 Fragment를 바꿔줌
    private FragmentManager fragmentManager=getSupportFragmentManager();
    private BMenuFragment1 bMenuFragment1=new BMenuFragment1();
    private BMenuFragment2 bMenuFragment2=new BMenuFragment2();
    private BMenuFragment3 bMenuFragment3=new BMenuFragment3();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hyun_shop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();      //툴바 활성화
        actionBar.setDisplayHomeAsUpEnabled(true);      //햄버거 버튼
        actionBar.setHomeAsUpIndicator(R.drawable.hamburger);
        actionBar.setHomeButtonEnabled(true);

        mDrawerLayout=findViewById(R.id.drawer);
        navigationView=findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);     //뷰객체 이벤트를 받기위해 리스너 세팅

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavi);
        bottomNavigationView.setSelectedItemId(R.id.mainshop);

        //첫화면 지정
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout,bMenuFragment2).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction= fragmentManager.beginTransaction();
                switch (item.getItemId()){
                    case R.id.category:{
                        transaction.replace(R.id.frame_layout,bMenuFragment1).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.mainshop:{
                        transaction.replace(R.id.frame_layout,bMenuFragment2).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.my:{
                        transaction.replace(R.id.frame_layout,bMenuFragment3).commitAllowingStateLoss();
                        break;
                    }
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //select hamburger item
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.tshirt:
                Toast.makeText(this, "T-Shirt로 이동", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Tshirt.class);
                startActivity(intent);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}