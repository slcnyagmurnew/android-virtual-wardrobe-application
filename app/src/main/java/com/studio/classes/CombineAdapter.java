package com.studio.classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import com.studio.sanaldolabim.R;
import java.util.ArrayList;

public class CombineAdapter extends RecyclerView.Adapter<CombineAdapter.ViewHolder> {

    ArrayList<Combine> arrayList;
    LayoutInflater layoutInflater;
    Context context;

    public CombineAdapter(ArrayList<Combine> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    // Her bir satır için temsil edilecek olan arayüz belirlenir.
    @NonNull
    @Override
    public CombineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.combine_item,parent,false);
        return new ViewHolder(view);
    }

    // Her bir satırın içeriği belirlenir.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Combine item = arrayList.get(position);
        holder.imgHead.setImageDrawable(Drawable.createFromPath(item.getOverheadPath()));
        holder.imgFace.setImageDrawable(Drawable.createFromPath(item.getFacePath()));
        holder.imgUpper.setImageDrawable(Drawable.createFromPath(item.getUpperPath()));
        holder.imgLower.setImageDrawable(Drawable.createFromPath(item.getLowerPath()));
        holder.imgFoot.setImageDrawable(Drawable.createFromPath(item.getFootPath()));
        holder.name.setText(item.getName());
        holder.llcombine_list_element.setTag(holder);
        holder.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String combineId = arrayList.get(position).getId();
                Intent intent = new Intent("send_combine");
                intent.putExtra("combine_id", combineId);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String combineId = arrayList.get(position).getId();
                Intent intent = new Intent("delete_combine");
                intent.putExtra("combine_id", combineId);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
        TextView name;
        ImageView imgHead, imgFace, imgUpper, imgLower, imgFoot;
        LinearLayout llcombine_list_element;
        ImageButton btnDelete, btnSend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.combine_preview);
            imgHead = itemView.findViewById(R.id.imgHeadCombine);
            imgFace = itemView.findViewById(R.id.imgFaceCombine);
            imgUpper = itemView.findViewById(R.id.imgUpperCombine);
            imgLower = itemView.findViewById(R.id.imgLowerCombine);
            imgFoot = itemView.findViewById(R.id.imgFootCombine);
            btnDelete = itemView.findViewById(R.id.btnDeleteCombine);
            btnSend = itemView.findViewById(R.id.btnSendCombine);
            llcombine_list_element = itemView.findViewById(R.id.llcombine_list_element);
        }
    }
}