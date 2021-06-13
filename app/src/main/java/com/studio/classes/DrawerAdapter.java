package com.studio.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.studio.sanaldolabim.DrawerActivity;
import com.studio.sanaldolabim.R;
import java.util.ArrayList;
import java.util.Objects;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    ArrayList<Drawer> arrayList;
    LayoutInflater layoutInflater;
    Context context;
    String user_id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DrawerAdapter(ArrayList<Drawer> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    // Her bir satır için temsil edilecek olan arayüz belirlenir.
    @NonNull
    @Override
    public DrawerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        View view = layoutInflater.inflate(R.layout.drawer_item,parent,false);
        return new ViewHolder(view);
    }

    // Her bir satırın içeriği belirlenir.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.drawer_name.setText(arrayList.get(position).getDrawerName());
        holder.drawer_img.setImageResource(R.drawable.ic_baseline_drag_handle_24);
        holder.ll_list_element.setTag(holder);
        // Listedeki elemanlara tıklanıdığında yapılacak olan işlem...
        holder.ll_list_element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // array contains, ayni isimli iki cekmece olamaz.
                String name = arrayList.get(position).getDrawerName();
                Intent intent = new Intent(context, DrawerActivity.class);
                intent.putExtra("drawer_name", name);
                context.startActivity(intent);

            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = arrayList.get(position).getDrawerName();
                AskDelete(name).show();
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
        TextView drawer_name;
        ImageView drawer_img;
        ImageButton remove;
        LinearLayout ll_list_element;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            drawer_name = itemView.findViewById(R.id.drawer_preview);
            drawer_img = itemView.findViewById(R.id.img_drawer);
            remove = itemView.findViewById(R.id.removeDrawer);
            ll_list_element = itemView.findViewById(R.id.ll_list_element);
        }
    }

    private AlertDialog AskDelete(String id) {
        return new AlertDialog.Builder(context)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you really want to Delete Drawer?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.collection("Users").document(user_id)
                                .collection("drawerlist")
                                .document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                        ((Activity)context).finish();
                                        context.startActivity(((Activity) context).getIntent());
                                        Toast.makeText(context, "Drawer deleted successfully", Toast.LENGTH_SHORT).show();
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
}