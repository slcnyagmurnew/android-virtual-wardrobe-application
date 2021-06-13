package com.studio.classes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.studio.sanaldolabim.AddClothesActivity;
import com.studio.sanaldolabim.DrawerActivity;
import com.studio.sanaldolabim.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ViewHolder> {

    ArrayList<Clothes> arrayList;
    LayoutInflater layoutInflater;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id;
    Intent requestFileIntent;

    public ClothesAdapter(ArrayList<Clothes> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    // Her bir satır için temsil edilecek olan arayüz belirlenir.
    @NonNull
    @Override
    public ClothesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        layoutInflater = LayoutInflater.from(context);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        View view = layoutInflater.inflate(R.layout.clothes_item,parent,false);
        return new ViewHolder(view);
    }

    // Her bir satırın içeriği belirlenir.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clothes item = arrayList.get(position);
        holder.price.setText(item.getPrice());
        holder.pattern.setText(item.getPattern());
        holder.color.setText(item.getColor());
        holder.type.setText(item.getClothesType());
        holder.date.setText(item.getReceiptDate().toString());
        holder.clothes_img.setImageDrawable(Drawable.createFromPath(item.getFilePath()));
        holder.llm_list_element.setTag(holder);
        // Listedeki elemanlara tıklanıdığında yapılacak olan işlem...
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String drawerName = arrayList.get(position).getDrawerName();
                String photoPath = arrayList.get(position).getFilePath();
                Intent intent = new Intent(context, AddClothesActivity.class);
                intent.putExtra("drawer_name", drawerName);
                intent.putExtra("photo_path", photoPath);
                context.startActivity(intent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String drawerName = arrayList.get(position).getDrawerName();
                String photoPath = arrayList.get(position).getFilePath();
                AskDelete(drawerName, photoPath).show();
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
        TextView type, pattern, price, date, color;
        ImageView clothes_img;
        ImageButton edit, delete;
        LinearLayout llm_list_element;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.txtClothesColor);
            type = itemView.findViewById(R.id.txtClothesType);
            pattern = itemView.findViewById(R.id.txtClothesPattern);
            date = itemView.findViewById(R.id.txtClothesDate);
            price = itemView.findViewById(R.id.txtClothesPrice);
            clothes_img = itemView.findViewById(R.id.img_clothes);
            edit = itemView.findViewById(R.id.btnEditImage);
            delete = itemView.findViewById(R.id.btnDeleteImage);
            llm_list_element = itemView.findViewById(R.id.llm_list_element);
        }
    }

    private AlertDialog AskDelete(String drawer, String path) {
        return new AlertDialog.Builder(context)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you really want to Delete Drawer?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.collection("Users").document(user_id)
                                .collection("drawerlist")
                                .document(drawer)
                                .collection("clotheslist")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                                        for (int i = 0; i<list.size(); i++) {
                                            Map<String, Object> map = task.getResult().getDocuments().get(i).getData();
                                            if (map.get("photopath").equals(path)) {
                                                String id = task.getResult().getDocuments().get(i).getId();
                                                deleteClothes(drawer, id);
                                            }
                                        }
                                        ((Activity)context).finish();
                                        context.startActivity(((Activity) context).getIntent());
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

    private void deleteClothes(String drawer, String id) {
        db.collection("Users").document(user_id)
                .collection("drawerlist")
                .document(drawer)
                .collection("clotheslist")
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });
    }
}