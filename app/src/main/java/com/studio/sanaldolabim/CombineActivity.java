package com.studio.sanaldolabim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.studio.classes.Combine;
import com.studio.classes.CombineAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CombineActivity extends AppCompatActivity {

    String user_id, deletedCombine, sentCombine, selectCombine;
    RecyclerView r_view;
    ArrayList<Combine> combineArrayList;
    ArrayList<Uri> clothesArrayList;
    CombineAdapter combineAdapter;
    FirebaseFirestore db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine);
        defineVariables();
        getCombines();
        LocalBroadcastManager.getInstance(this).registerReceiver(sMessageReceiver,
                new IntentFilter("select_combine"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("delete_combine"));
        LocalBroadcastManager.getInstance(this).registerReceiver(kMessageReceiver,
                new IntentFilter("send_combine"));

    }

    public void defineVariables() {
        r_view = (RecyclerView) findViewById(R.id.r_view_klist);
        combineArrayList = new ArrayList<>();
        context = this;
        clothesArrayList = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        r_view = findViewById(R.id.r_view_klist);
        db = FirebaseFirestore.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        r_view.setLayoutManager(linearLayoutManager);
        combineAdapter = new CombineAdapter(combineArrayList, this);
        r_view.setAdapter(combineAdapter);
    }

    private void getCombines() {
        db.collection("Users").document(user_id)
                .collection("combinelist")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> list = task.getResult().getDocuments();
                for (int i = 0; i<list.size(); i++) {
                    Map<String, Object> map = task.getResult().getDocuments().get(i).getData();
                    assert map != null;
                    String overhead = null, face = null, upperbody = null, lowerbody = null, foot = null;
                    String id = task.getResult().getDocuments().get(i).getId();
                    String name = Objects.requireNonNull(map.get("name")).toString();
                    if (map.get("overhead") != null){
                        overhead = Objects.requireNonNull(map.get("overhead")).toString();
                    }
                    if (map.get("face") != null){
                        face = Objects.requireNonNull(map.get("face")).toString();
                    }
                    if (map.get("upperbody") != null) {
                        upperbody = Objects.requireNonNull(map.get("upperbody")).toString();
                    }
                    if (map.get("lowerbody") != null){
                        lowerbody = Objects.requireNonNull(map.get("lowerbody")).toString();
                    }
                    if (map.get("foot") != null) {
                        foot = Objects.requireNonNull(map.get("foot")).toString();
                    }
                    Combine combine = new Combine(overhead, face, upperbody, lowerbody, foot, name, id);
                    combineArrayList.add(combine);
                }
                combineAdapter.notifyDataSetChanged();
            }});
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            deletedCombine = intent.getStringExtra("combine_id");
            AlertDialog alertDialog = AskDelete();
            alertDialog.show();
        }
    };

    // sent
    public BroadcastReceiver kMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sentCombine = intent.getStringExtra("combine_id");
            sendCombine(sentCombine);
        }
    };

    public BroadcastReceiver sMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            selectCombine = intent.getStringExtra("combine_id");
        }
    };

    private AlertDialog AskDelete() {
        return new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you really want to Delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.collection("Users").document(user_id)
                                .collection("combinelist")
                                .document(deletedCombine)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                        Toast.makeText(CombineActivity.this, "Combine deleted successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(getIntent());
                                        Toast.makeText(CombineActivity.this, "Combine deleted successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error deleting document", e);
                                    }
                                });
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void sendCombine(String id) {
        db.collection("Users").document(user_id)
                .collection("combinelist")
                .document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                File file;
                Map<String, Object> map = task.getResult().getData();
                String overhead = null, face = null, upperbody = null, lowerbody = null, foot = null;
                String name = Objects.requireNonNull(map.get("name")).toString();
                if (map.get("overhead") != null){
                    overhead = Objects.requireNonNull(map.get("overhead")).toString();
                    file = new File(overhead);
                    clothesArrayList.add(createUriFromPath(file));
                }
                if (map.get("face") != null){
                    face = Objects.requireNonNull(map.get("face")).toString();
                    file = new File(face);
                    clothesArrayList.add(createUriFromPath(file));
                }
                if (map.get("upperbody") != null) {
                    upperbody = Objects.requireNonNull(map.get("upperbody")).toString();
                    file = new File(upperbody);
                    clothesArrayList.add(createUriFromPath(file));
                }
                if (map.get("lowerbody") != null){
                    lowerbody = Objects.requireNonNull(map.get("lowerbody")).toString();
                    file = new File(lowerbody);
                    clothesArrayList.add(createUriFromPath(file));
                }
                if (map.get("foot") != null) {
                    foot = Objects.requireNonNull(map.get("foot")).toString();
                    file = new File(foot);
                    clothesArrayList.add(createUriFromPath(file));
                }
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, clothesArrayList);
                startActivity(Intent.createChooser(intent, "Share images to.."));
            }
        });
    }

    private Uri createUriFromPath(File file) {
        return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
    }
}