package com.example.realestatemanager.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestatemanager.R;
import com.example.realestatemanager.ui.utils.Utils;

public class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        TextView textView = findViewById(R.id.test);
        textView.setText(""+Utils.newIsInternetAvailable(this));

    }
}
