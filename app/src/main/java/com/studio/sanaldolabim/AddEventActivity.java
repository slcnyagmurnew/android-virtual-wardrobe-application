package com.studio.sanaldolabim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.studio.classes.DBHelper;
import com.studio.classes.Event;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";
    int COMBINE_REQUEST_CODE = 100;
    int LOCATION_REQUEST_CODE = 1001;
    private EditText name, type, date;
    private TextView txtLocation, txtCombine;
    private FusedLocationProviderClient fusedLocationClient;
    DatePickerDialog picker;
    Context context;
    DBHelper dbHelper;
    private String eventFileName, eventFileBody, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        defineVariables();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void defineVariables() {
        name = (EditText) findViewById(R.id.newEventName);
        type = (EditText) findViewById(R.id.newEventType);
        date = (EditText) findViewById(R.id.newEventDate);
        txtLocation = (TextView) findViewById(R.id.txtSelectedLocation);
        txtCombine = (TextView) findViewById(R.id.txtSelectedCombine);
        context = this;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
        dbHelper = new DBHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void onSelectLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationTask = fusedLocationClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess:" + location.toString());
                String address = getAddress(location.getLatitude(), location.getLongitude());
                txtLocation.setText(address);
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure:" + e.getLocalizedMessage());
            }
        });

        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

            }
        });
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d(TAG, "askLocationPermission: you should show an alert dialog");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                getLastLocation();
            }
            else {
                // permission not granted
            }
        }
    }

    public void onSelectCombine(View view) {
        Intent intent = new Intent(this, CabinActivity.class);
        intent.putExtra("isFromEventActivity", "true");
        startActivityForResult(intent, COMBINE_REQUEST_CODE);
        // start activity for result, delete butonları deaktif olsun
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COMBINE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                eventFileName = data.getStringExtra("event_file_name");
                eventFileBody = data.getStringExtra("event_file_body");
                String modifier = name.getText().toString().replaceAll(" ", "");
                txtCombine.setText("combineFor" + modifier);
            }
        }
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getSubLocality()).append(",");
                result.append(address.getThoroughfare()).append(" ");
                result.append("No:").append(address.getFeatureName()).append(" ");
                result.append(address.getSubAdminArea()).append("/");
                result.append(address.getAdminArea()).append(", ");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
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

    public void onSelectDate(View view) {
        getDatePicker();
    }

    public void onSaveEvent(View view) {
        String eventName = name.getText().toString();
        String eventType = type.getText().toString();
        String eventDate = date.getText().toString();
        String eventLocation = txtLocation.getText().toString();
        String combineId = txtCombine.getText().toString();
        if (dbHelper.insertEvent(eventFileName, userEmail, eventName, eventType,
                eventLocation, Date.valueOf(eventDate), combineId)) {
            saveToInternalStorage(eventFileName, eventFileBody);
            Toast.makeText(AddEventActivity.this, "Event added successfully!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void getDatePicker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(AddEventActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
        picker.show();
    }
}