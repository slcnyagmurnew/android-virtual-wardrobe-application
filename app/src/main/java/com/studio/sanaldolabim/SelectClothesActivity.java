package com.studio.sanaldolabim;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.studio.classes.Clothes;
import com.studio.classes.ClothesSelectAdapter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SelectClothesActivity extends AppCompatActivity {

    String bodyPart, user_id, selectedPath;
    RecyclerView r_view;
    ArrayList<Clothes> clothesArrayList;
    ClothesSelectAdapter clothesAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_clothes);
        defineVariables();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("selected_clothes_path"));
        getDrawers();
    }

    public void defineVariables() {
        Intent intent = getIntent();
        bodyPart = intent.getStringExtra("type");
        clothesArrayList = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        r_view = findViewById(R.id.r_view_slist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        r_view.setLayoutManager(linearLayoutManager);
        clothesAdapter = new ClothesSelectAdapter(clothesArrayList, this);
        r_view.setAdapter(clothesAdapter);
    }

    private void getDrawers() {
        db.collection("Users").document(user_id)
                .collection("drawerlist")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> list = task.getResult().getDocuments();
                for (int i = 0; i<list.size(); i++) {
                    String drawerName = task.getResult().getDocuments().get(i).getId();
                    getClothes(drawerName);
                }
            }});
    }

    private void getClothes(String drawer) {
        db.collection("Users").document(user_id)
                .collection("drawerlist")
                .document(drawer)
                .collection("clotheslist")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> list = task.getResult().getDocuments();
                for (int i = 0; i<list.size(); i++) {
                    Map<String, Object> map = task.getResult().getDocuments().get(i).getData();
                    String category = map.get("category").toString();
                    if (category.equals(bodyPart)) {
                        String type = map.get("type").toString();
                        String pattern = map.get("pattern").toString();
                        String color = map.get("color").toString();
                        String price = map.get("price").toString();
                        String date = map.get("receiptdate").toString();
                        String uri = map.get("uri").toString();
                        String photoname = map.get("photoname").toString();
                        String photopath = map.get("photopath").toString();
                        Clothes clothes = new Clothes(type, color, category, pattern,
                                Date.valueOf(date), price,
                                photopath, photoname, drawer, Uri.parse(uri));
                        clothesArrayList.add(clothes);
                    }
                }
                clothesAdapter.notifyDataSetChanged();
            }});
    }

    private void finishActivity() {
        Intent returnIntent = new Intent(this, CabinActivity.class);
        returnIntent.putExtra("selected_path", selectedPath);
        returnIntent.putExtra("body_part", bodyPart);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            selectedPath = intent.getStringExtra("file_path");
            finishActivity();
        }
    };
}