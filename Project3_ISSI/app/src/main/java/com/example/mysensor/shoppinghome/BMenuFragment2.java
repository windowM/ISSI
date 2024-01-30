package com.example.mysensor.shoppinghome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mysensor.R;

public class BMenuFragment2 extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageButton mainPicture=new ImageButton(getActivity());

        mainPicture.setLayoutParams(params);
        mainPicture.setImageResource(R.drawable.firstpage);


        return inflater.inflate(R.layout.fragment_2,container,false);
    }
}
