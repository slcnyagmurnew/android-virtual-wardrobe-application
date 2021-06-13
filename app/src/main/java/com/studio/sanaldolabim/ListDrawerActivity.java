package com.studio.sanaldolabim;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.studio.classes.Drawer;
import com.studio.classes.DrawerAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListDrawerActivity extends AppCompatActivity {

    ImageButton create;
    EditText inputEditTextField;
    ArrayList<Drawer> drawerArrayList;
    RecyclerView r_view;
    String user_id;
    DrawerAdapter drawerAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_drawer);
        defineVariables();
        getDrawers();
    }

    private void getDrawers() {
        db.collection("Users").document(user_id)
                .collection("drawerlist")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> list = task.getResult().getDocuments();
                for (int i = 0; i<list.size(); i++) {
                    String name = task.getResult().getDocuments().get(i).getId();
                    //Map<String, Object> map = task.getResult().getDocuments().get(i).getData();
                    Drawer drawer = new Drawer(name);
                    drawerArrayList.add(drawer);
                }
                drawerAdapter.notifyDataSetChanged();
            }});
    }

    public void defineVariables() {
        Intent intent = getIntent();
        String drawerName;
        create = (ImageButton) findViewById(R.id.createNewClothes);
        drawerArrayList = new ArrayList<>();
        setTitle("Drawers");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        inputEditTextField = new EditText(this);
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        r_view = findViewById(R.id.r_view_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        r_view.setLayoutManager(linearLayoutManager);
        drawerAdapter = new DrawerAdapter(drawerArrayList, this);
        r_view.setAdapter(drawerAdapter);
    }

    public void onCreateDrawer(View view) {
        AlertDialog dialog = AskCreate();
        dialog.show();
    }

    private AlertDialog AskCreate() {
        return new AlertDialog.Builder(this)
                .setTitle("Create")
                .setMessage("Please enter your New Drawer name:")
                .setView(inputEditTextField)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Drawer drawer = new Drawer();
                        // draweri firebaseye ekle
                        String editTextInput = inputEditTextField.getText().toString();
                        //Log.d("onclick","editext value is: "+ editTextInput);
                        drawer.setDrawerName(editTextInput);
                        addDrawer(drawer.getDrawerName());
                        finish();
                        Intent intent = new Intent(getBaseContext(), ListDrawerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    public void addDrawer(String name) {
        Map<String, Object> drawer = new HashMap<>();
        // Add a new document with a generated ID
        db.collection("Users").document(user_id).
                collection("drawerlist").document(name)
            .set(drawer, SetOptions.merge())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ListDrawerActivity.this, "Drawer saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListDrawerActivity.this, "Oops, error occurred!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}