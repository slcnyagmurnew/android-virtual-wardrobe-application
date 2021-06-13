package com.studio.sanaldolabim;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.studio.classes.Clothes;
import java.io.File;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddClothesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_GALLERY = 200;

    Spinner type_spinner, pattern_spinner, category_spinner;
    String drawerName, photoPath;
    EditText color, price, date;
    TextView fileName;
    Intent requestFileIntent;
    Clothes clothes;
    Uri photoUri;
    String file_path = null, file_name = null, user_id;
    DatePickerDialog picker;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore db;
    UploadTask uploadTask;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothes);
        defineVariables();
        defineListeners();
        setTitle("Add Clothes");
    }

    private void defineVariables() {
        Intent intent = getIntent();
        drawerName = intent.getStringExtra("drawer_name");
        photoPath = intent.getStringExtra("photo_path");
        type_spinner = (Spinner) findViewById(R.id.spinnerType);
        pattern_spinner = (Spinner) findViewById(R.id.spinnerPattern);
        category_spinner = (Spinner) findViewById(R.id.spinnerCategory);
        date = (EditText) findViewById(R.id.txtReceiptDate);
        price = (EditText) findViewById(R.id.txtPrice);
        color = (EditText) findViewById(R.id.txtColor);
        fileName = (TextView) findViewById(R.id.fileNameTxt);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        context = this;
        if (photoPath != null) {
            type_spinner.setVisibility(View.GONE);
            pattern_spinner.setVisibility(View.GONE);
            category_spinner.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
            price.setVisibility(View.GONE);
            color.setVisibility(View.GONE);
        }
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    }

    private void callSuccessful() {
        Context context = getApplicationContext();
        CharSequence text = "Clothes added successfully!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // duzenle
    private boolean isValidQuestion() {
        return !(date.getText() == null
                || color.getText() == null || price.getText() == null);
    }

    public void onSaveNewClothes(View view) {
        if (!isValidQuestion()) {
            Toast.makeText(AddClothesActivity.this, "Please fill in Clothes information correctly!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (photoPath != null) {
            findInFireStore();
        }
        else {
            String clothesType = type_spinner.getSelectedItem().toString();
            String clothesPattern = pattern_spinner.getSelectedItem().toString();
            String clothesCategory = category_spinner.getSelectedItem().toString();
            Date receiptDate = Date.valueOf(date.getText().toString());
            String clothesPrice = price.getText().toString();
            String clothesColor = color.getText().toString();
            String clothesFileName = fileName.getText().toString();
            Intent intent = getIntent();
            String drawerName = intent.getStringExtra("drawer_name");
            clothes = new Clothes(clothesType, clothesColor, clothesCategory, clothesPattern,
                    receiptDate, clothesPrice, file_path, clothesFileName, drawerName, photoUri);
            StorageReference clothesRef = storageRef.child("Clothes/"+clothes.getPhotoUri().getLastPathSegment());
            uploadTask = clothesRef.putFile(clothes.getPhotoUri());
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    putToFireStore(clothes);
                    callSuccessful();
                    finish();
                    Intent intent = new Intent(context, DrawerActivity.class);
                    intent.putExtra("drawer_name", drawerName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddClothesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(AddClothesActivity.this, "Please Give Permission to Upload File", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(AddClothesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AddClothesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void filePicker() {
        Toast.makeText(AddClothesActivity.this, "File Picker calls", Toast.LENGTH_SHORT).show();
        requestFileIntent = new Intent();
        requestFileIntent.setType("image/*");
        requestFileIntent.setAction(Intent.ACTION_GET_CONTENT);
        requestFileIntent = Intent.createChooser(requestFileIntent, "Choose a file");
        startActivityForResult(requestFileIntent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
                assert data != null;
                String filePath = getRealPathFromUri(data.getData(), AddClothesActivity.this);
                Log.d("File Path : ", " " + filePath);
                this.file_path = filePath;
                File file = new File(filePath);
                fileName.setText(file.getName());
                this.file_name = file.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromUri(Uri uri, Activity activity) {
        String[] proj = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
        photoUri = uri;
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int id = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            return cursor.getString(id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddClothesActivity.this, "Permission Successfull", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddClothesActivity.this, "Permission Failed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void onSelectFile(View view) {
        if (checkPermission()) {
            filePicker();
        } else {
            requestPermission();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtReceiptDate:
                getDatePicker();
                break;
            default:
                break;
        }
    }

    public void defineListeners() {
        date.setOnClickListener(this);
    }

    private void putToFireStore(Clothes clothes) {
        Map<String, Object> clothesMap = new HashMap<>();
        clothesMap.put("color", clothes.getColor());
        clothesMap.put("pattern", clothes.getPattern());
        clothesMap.put("photoname", clothes.getFileName());
        clothesMap.put("photopath", clothes.getFilePath());
        clothesMap.put("category", clothes.getCategory());
        clothesMap.put("price", Integer.parseInt(clothes.getPrice()));
        clothesMap.put("receiptdate", clothes.getReceiptDate().toString());
        clothesMap.put("type", clothes.getClothesType());
        clothesMap.put("drawername", clothes.getDrawerName());
        clothesMap.put("uri", clothes.getPhotoUri().toString());

        db.collection("Users").document(user_id)
                .collection("drawerlist")
                .document(clothes.getDrawerName())
                .collection("clotheslist")
                .add(clothesMap)
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

    private void findInFireStore() {
        db.collection("Users").document(user_id)
                .collection("drawerlist")
                .document(drawerName)
                .collection("clotheslist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                        for (int i = 0; i<list.size(); i++) {
                            Map<String, Object> map = task.getResult().getDocuments().get(i).getData();
                            if (map.get("photopath").equals(photoPath)) {
                                String id = task.getResult().getDocuments().get(i).getId();
                                updateToFireStore(id);
                                finish();
                                Intent intent = new Intent(context, DrawerActivity.class);
                                intent.putExtra("drawer_name", drawerName);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

    private void updateToFireStore(String id) {
        db.collection("Users").document(user_id)
                .collection("drawerlist")
                .document(drawerName)
                .collection("clotheslist")
                .document(id)
                .update("photoname", file_name,
                        "photopath", file_path,
                        "uri", String.valueOf(photoUri))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Image updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDatePicker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(AddClothesActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
        picker.show();
    }
}