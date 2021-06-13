package com.studio.sanaldolabim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class CabinActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String extra = "type";
    private static final int REQUEST_SELECT = 201;

    Button overhead, face, upperBody, lowerBody, foot, saveCombine, goCombines;
    Button remove1, remove2, remove3, remove4, remove5;
    ImageView imgOverHead, imgFace, imgUpperBody, imgLowerBody, imgFoot;
    EditText name;
    TextView cabinQuestion, cabinTitle;
    String overPath, facePath, upperPath, lowerPath, footPath, user_id, combineName;
    String isFromEventActivity, isFromEventAdapter, fileName;
    FirebaseFirestore db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabin);
        defineVariables();
        defineListeners();
    }

    public void defineVariables() {
        // add event e ekle
        Intent intent = getIntent();
        isFromEventActivity = intent.getStringExtra("isFromEventActivity");
        isFromEventAdapter = intent.getStringExtra("isFromEventAdapter");
        overhead = (Button) findViewById(R.id.btnSelectOverhead);
        cabinQuestion = (TextView) findViewById(R.id.cabinActivityQuestion);
        cabinTitle = (TextView) findViewById(R.id.cabinTitle);
        face = (Button) findViewById(R.id.btnSelectFace);
        upperBody = (Button) findViewById(R.id.btnSelectUpperBody);
        lowerBody = (Button) findViewById(R.id.btnSelectLowerBody);
        foot = (Button) findViewById(R.id.btnSelectFoot);
        name = (EditText) findViewById(R.id.newCombineName);
        saveCombine = (Button) findViewById(R.id.btnSaveCombine);
        goCombines = (Button) findViewById(R.id.btnGoPastCombines);
        imgOverHead = (ImageView) findViewById(R.id.imageViewOverhead);
        imgFace = (ImageView) findViewById(R.id.imageViewFace);
        imgUpperBody = (ImageView) findViewById(R.id.imageViewUpperBody);
        imgLowerBody = (ImageView) findViewById(R.id.imageViewLowerBody);
        imgFoot = (ImageView) findViewById(R.id.imageViewFoot);
        remove1 = (Button) findViewById(R.id.btnRemoveOver);
        remove2 = (Button) findViewById(R.id.btnRemoveFace);
        remove3 = (Button) findViewById(R.id.btnRemoveUpper);
        remove4 = (Button) findViewById(R.id.btnRemoveLower);
        remove5 = (Button) findViewById(R.id.btnRemoveFoot);
        context = this;

        if (isFromEventActivity != null) {
            cabinQuestion.setVisibility(View.INVISIBLE);
            cabinTitle.setVisibility(View.INVISIBLE);
            goCombines.setVisibility(View.INVISIBLE);
            name.setVisibility(View.GONE);
            saveCombine.setText(R.string.btn_select_combine);
        }

        else if (isFromEventAdapter != null) {
            cabinQuestion.setVisibility(View.INVISIBLE);
            cabinTitle.setVisibility(View.INVISIBLE);
            goCombines.setVisibility(View.INVISIBLE);
            name.setVisibility(View.GONE);
            fileName = intent.getStringExtra("id");
            readFromInternalStorage(fileName);
        }

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SelectClothesActivity.class);
        switch (v.getId()){
            case R.id.btnSelectOverhead:
                intent.putExtra(extra, "Overhead");
                startActivityForResult(intent, REQUEST_SELECT);
                break;
            case R.id.btnSelectFace:
                intent.putExtra(extra, "Face");
                startActivityForResult(intent, REQUEST_SELECT);
                break;
            case R.id.btnSelectUpperBody:
                intent.putExtra(extra, "UpperBody");
                startActivityForResult(intent, REQUEST_SELECT);
                break;
            case R.id.btnSelectLowerBody:
                intent.putExtra(extra, "LowerBody");
                startActivityForResult(intent, REQUEST_SELECT);
                break;
            case R.id.btnSelectFoot:
                intent.putExtra(extra, "Foot");
                startActivityForResult(intent, REQUEST_SELECT);
                break;
            case R.id.btnRemoveOver:
                imgOverHead.setImageDrawable(null);
                overPath = null;
                break;
            case R.id.btnRemoveFace:
                imgFace.setImageDrawable(null);
                facePath = null;
                break;
            case R.id.btnRemoveUpper:
                imgUpperBody.setImageDrawable(null);
                upperPath = null;
                break;
            case R.id.btnRemoveLower:
                imgLowerBody.setImageDrawable(null);
                lowerPath = null;
                break;
            case R.id.btnRemoveFoot:
                imgFoot.setImageDrawable(null);
                footPath = null;
                break;
            case R.id.btnSaveCombine:
                if (isFromEventActivity != null){
                    String fileName = generateRandomFileName();
                    String body = overPath + "\n" + facePath + "\n" + upperPath + "\n" + lowerPath + "\n" + footPath;
                    Toast.makeText(context, "Combine saved successfully!", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent(this, AddEventActivity.class);
                    returnIntent.putExtra("event_file_name", fileName);
                    returnIntent.putExtra("event_file_body", body);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
                else if (isFromEventAdapter != null) {
                    String body = overPath + "\n" + facePath + "\n" + upperPath + "\n" + lowerPath + "\n" + footPath;
                    saveToInternalStorage(fileName, body);
                    Toast.makeText(context, "Combine saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    putToFireStore();
                    Toast.makeText(context, "Combine saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void defineListeners() {
        overhead.setOnClickListener(this);
        face.setOnClickListener(this);
        upperBody.setOnClickListener(this);
        lowerBody.setOnClickListener(this);
        foot.setOnClickListener(this);
        remove1.setOnClickListener(this);
        remove2.setOnClickListener(this);
        remove3.setOnClickListener(this);
        remove4.setOnClickListener(this);
        remove5.setOnClickListener(this);
        saveCombine.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                String path = data.getStringExtra("selected_path");
                String part = data.getStringExtra("body_part");
                setImage(part, path);
            }
        }
    }

    private void setImage(String part, String path) {
        if ("Overhead".equals(part)) {
            imgOverHead.setImageDrawable(Drawable.createFromPath(path));
            overPath = path;
        }
        else if ("Face".equals(part)) {
            imgFace.setImageDrawable(Drawable.createFromPath(path));
            facePath = path;
        }
        else if ("UpperBody".equals(part)) {
            imgUpperBody.setImageDrawable(Drawable.createFromPath(path));
            upperPath = path;
        }
        else if ("LowerBody".equals(part)) {
            imgLowerBody.setImageDrawable(Drawable.createFromPath(path));
            lowerPath = path;
        }
        else {
            imgFoot.setImageDrawable(Drawable.createFromPath(path));
            footPath = path;
        }
    }

    // mkdir -> internal storage - clothes file
    private void putToFireStore() {
        Map<String, Object> combineMap = new HashMap<>();
        combineName = name.getText().toString();
        combineMap.put("name", combineName);
        combineMap.put("overhead", overPath);
        combineMap.put("face", facePath);
        combineMap.put("upperbody", upperPath);
        combineMap.put("lowerbody", lowerPath);
        combineMap.put("foot", footPath);
        // kontroller ekle
        db.collection("Users").document(user_id)
                .collection("combinelist")
                .add(combineMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

    public void onGoCombines(View view) {
        Intent intent = new Intent(this, CombineActivity.class);
        startActivity(intent);
    }

    public String generateRandomFileName() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private void readFromInternalStorage(String fileName) {
        ArrayList<String> clothesPaths = new ArrayList<>();
        try {
            File fileEvents = new File(context.getFilesDir().getAbsolutePath() + "/myDirectory/" + fileName + ".txt");
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                clothesPaths.add(line);
            }
            overPath = clothesPaths.get(0);
            imgOverHead.setImageDrawable(Drawable.createFromPath(overPath));
            facePath = clothesPaths.get(1);
            imgFace.setImageDrawable(Drawable.createFromPath(facePath));
            upperPath = clothesPaths.get(2);
            imgUpperBody.setImageDrawable(Drawable.createFromPath(upperPath));
            lowerPath = clothesPaths.get(3);
            imgLowerBody.setImageDrawable(Drawable.createFromPath(lowerPath));
            footPath = clothesPaths.get(4);
            imgFoot.setImageDrawable(Drawable.createFromPath(footPath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToInternalStorage(String fileName, String body) {
        File dir = new File(context.getFilesDir(), "myDirectory");
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            File new_file = new File(dir, fileName + ".txt");
            FileWriter writer = new FileWriter(new_file);
            writer.append(body);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}