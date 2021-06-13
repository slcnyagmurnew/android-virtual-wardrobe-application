package com.studio.sanaldolabim;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.studio.classes.DBHelper;
import com.studio.classes.Event;
import com.studio.classes.EventAdapter;
import java.util.ArrayList;
import java.util.Objects;

public class EventActivity extends AppCompatActivity {

    RecyclerView r_view;
    ArrayList<Event> eventArrayList;
    EventAdapter eventAdapter;
    String userEmail;
    Context context;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        defineVariables();
    }

    public void onCreateEvent(View view) {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }

    private void defineVariables() {
        dbHelper = new DBHelper(this);
        r_view = (RecyclerView) findViewById(R.id.r_view_elist);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
        eventArrayList = dbHelper.getAllEvents(userEmail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        r_view.setLayoutManager(linearLayoutManager);
        context = this;
        eventAdapter = new EventAdapter(eventArrayList, this);
        r_view.setAdapter(eventAdapter);
    }
}