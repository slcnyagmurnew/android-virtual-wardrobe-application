package com.studio.sanaldolabim;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    public ImageButton drawers, cabin, activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        defineVariables();
        defineListeners();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnDrawers:
                startActivity(new Intent(UserActivity.this, ListDrawerActivity.class));
                break;
            case R.id.imgBtnCabin:
                startActivity(new Intent(UserActivity.this, CabinActivity.class));
                break;
            case R.id.imgBtnActivities:
                startActivity(new Intent(UserActivity.this, EventActivity.class));
                break;
        }
    }

    public void defineVariables() {
        drawers = (ImageButton) findViewById(R.id.imgBtnDrawers);
        cabin = (ImageButton) findViewById(R.id.imgBtnCabin);
        activities = (ImageButton) findViewById(R.id.imgBtnActivities);
    }

    public void defineListeners() {
        drawers.setOnClickListener(this);
        cabin.setOnClickListener(this);
        activities.setOnClickListener(this);
    }
}