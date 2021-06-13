package com.studio.sanaldolabim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.studio.classes.Clothes;
import com.studio.classes.ClothesAdapter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DrawerActivity extends AppCompatActivity {

    String drawer_name, user_id;
    RecyclerView r_view;
    ArrayList<Clothes> clothesArrayList;
    ClothesAdapter clothesAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        defineVariables();
        getClothes();
    }

    public void defineVariables() {
        Intent intent = getIntent();
        drawer_name = intent.getStringExtra("drawer_name");
        clothesArrayList = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        r_view = findViewById(R.id.r_view_clist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        r_view.setLayoutManager(linearLayoutManager);
        clothesAdapter = new ClothesAdapter(clothesArrayList, this);
        r_view.setAdapter(clothesAdapter);
    }

    public void onCreateClothes(View view) {
        Intent intent = new Intent(DrawerActivity.this, AddClothesActivity.class);
        intent.putExtra("drawer_name", drawer_name);
        startActivity(intent);
    }

    private void getClothes() {
        db.collection("Users").document(user_id)
                .collection("drawerlist")
                .document(drawer_name)
                .collection("clotheslist")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> list = task.getResult().getDocuments();
                for (int i = 0; i<list.size(); i++) {
                    Map<String, Object> map = task.getResult().getDocuments().get(i).getData();
                    String type = map.get("type").toString();
                    String category = map.get("category").toString();
                    String pattern = map.get("pattern").toString();
                    String color = map.get("color").toString();
                    String price = map.get("price").toString();
                    String date = map.get("receiptdate").toString();
                    String uri = map.get("uri").toString();
                    String photoname = map.get("photoname").toString();
                    String photopath = map.get("photopath").toString();
                    Clothes clothes = new Clothes(type, color, category, pattern,
                            Date.valueOf(date), price,
                            photopath, photoname, drawer_name, Uri.parse(uri));
                    clothesArrayList.add(clothes);
                }
                clothesAdapter.notifyDataSetChanged();
            }});
    }
}