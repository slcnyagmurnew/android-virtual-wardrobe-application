package com.studio.classes;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.studio.sanaldolabim.CabinActivity;
import com.studio.sanaldolabim.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{

    ArrayList<Event> arrayList;
    LayoutInflater layoutInflater;
    Context context;
    DatePickerDialog picker;
    String address;
    DBHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    public EventAdapter(ArrayList<Event> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    // Her bir satır için temsil edilecek olan arayüz belirlenir.
    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.event_item,parent,false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        dbHelper = new DBHelper(context);
        return new ViewHolder(view);
    }

    // Her bir satırın içeriği belirlenir.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event item = arrayList.get(position);
        holder.name.setText(item.getName());
        holder.type.setText(item.getKind());
        holder.date.setText(String.valueOf(item.getDate()));
        holder.location.setText(item.getLocation());
        holder.clothes.setText(item.getCombineId());
        holder.llm_list_element.setTag(holder);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = arrayList.get(position).getId();
                holder.name.setEnabled(true);
                holder.type.setEnabled(true);
                holder.date.setEnabled(true);
                holder.location.setEnabled(true);
                holder.save.setVisibility(View.VISIBLE);

                holder.clothes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CabinActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("email", userEmail);
                        intent.putExtra("isFromEventAdapter", "true");
                        context.startActivity(intent);
                    }
                });

                holder.date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDatePicker(holder.date);
                    }
                });

                holder.location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AskLocation(holder.location).show();
                    }
                });
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = arrayList.get(position).getId();
                String newName = holder.name.getText().toString();
                String newType = holder.type.getText().toString();
                String newDate = holder.date.getText().toString();
                String newLocation = holder.location.getText().toString();
                String clothes = holder.clothes.getText().toString();
                Event event = new Event(id, newName, newType, Date.valueOf(newDate), newLocation, clothes);
                if (dbHelper.updateEvent(event, userEmail)) {
                    Toast.makeText(context, "Event updated successfully!", Toast.LENGTH_SHORT).show();
                }
                ((Activity)context).finish();
            }
        });
    }


    // Listedeki eleman sayısı kadar işlemin yapılmasını sağladık. Elle de bir değer verilebilirdi.
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // Elemanlarımıza erişip tanımladığımız yer
    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText name, type, date, location, clothes;
        ImageButton edit;
        Button save;
        LinearLayout llm_list_element;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            type = itemView.findViewById(R.id.eventType);
            date = itemView.findViewById(R.id.eventDate);
            location = itemView.findViewById(R.id.eventLocation);
            clothes = itemView.findViewById(R.id.eventClothes);
            edit = itemView.findViewById(R.id.btnEditEvent);
            save = itemView.findViewById(R.id.btnSaveEventChanges);
            llm_list_element = itemView.findViewById(R.id.llevent_list_element);
        }
    }

    private void getDatePicker(EditText date) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
        picker.show();
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationTask = fusedLocationClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                address = getAddress(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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

    private AlertDialog AskLocation(EditText location) {
        return new AlertDialog.Builder(context)
                // set message, title, and icon
                .setTitle("Update")
                .setMessage("Do you really want to update location?")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getLastLocation();
                        location.setText(address);
                    }

                })
                .setNegativeButton("delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        location.setText("");
                        dialog.dismiss();
                    }
                })
                .create();
    }
}