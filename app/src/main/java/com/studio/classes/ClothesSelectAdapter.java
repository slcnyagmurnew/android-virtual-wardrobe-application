package com.studio.classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import com.studio.sanaldolabim.R;
import java.util.ArrayList;

public class ClothesSelectAdapter extends RecyclerView.Adapter<ClothesSelectAdapter.ViewHolder> {

    ArrayList<Clothes> arrayList;
    LayoutInflater layoutInflater;
    Context context;

    public ClothesSelectAdapter(ArrayList<Clothes> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    // Her bir satır için temsil edilecek olan arayüz belirlenir.
    @NonNull
    @Override
    public ClothesSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
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
        holder.llm_list_element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder1 = (ViewHolder)view.getTag();
                int position = holder1.getAdapterPosition();
                String filePath = arrayList.get(position).getFilePath();
                Intent intent = new Intent("selected_clothes_path");
                intent.putExtra("file_path", filePath);
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
        TextView type, pattern, price, date, color;
        ImageView clothes_img;
        LinearLayout llm_list_element;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.txtClothesColor);
            type = itemView.findViewById(R.id.txtClothesType);
            pattern = itemView.findViewById(R.id.txtClothesPattern);
            date = itemView.findViewById(R.id.txtClothesDate);
            price = itemView.findViewById(R.id.txtClothesPrice);
            clothes_img = itemView.findViewById(R.id.img_clothes);
            llm_list_element = itemView.findViewById(R.id.llm_list_element);
        }
    }
}