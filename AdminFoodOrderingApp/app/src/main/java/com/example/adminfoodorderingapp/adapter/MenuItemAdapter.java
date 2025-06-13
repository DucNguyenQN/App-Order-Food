package com.example.adminfoodorderingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminfoodorderingapp.EditActivity;
import com.example.adminfoodorderingapp.R;
import com.example.adminfoodorderingapp.model.AllMenu;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.AllItemViewolder>{
    private Context context;
    private List<AllMenu> menuList;

    public MenuItemAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AllItemViewolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item,parent,false);
        return new AllItemViewolder(v);
    }

    public void setData(List<AllMenu> menuList){
        this.menuList = menuList;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull AllItemViewolder holder, int position) {
        AllMenu menu = menuList.get(position);
        holder.txtName.setText(menu.getFoodName());
        holder.txtPrice.setText(formatStringNumber(menu.getFoodPrice()) + " VND");
        Glide.with(context).load(menu.getFoodImage()).into(holder.foodImage);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("itemDetail", menu);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(menuList != null){
            return menuList.size();
        }
        return 0;
    }

    public class AllItemViewolder extends RecyclerView.ViewHolder{
        ImageView foodImage;
        ImageButton btnEdit;
        TextView txtName, txtPrice, txtQuantity;
        public AllItemViewolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
    private String formatStringNumber(String numberString) {
        try {
            double number = Double.parseDouble(numberString);
            int roundedNumber = (int) Math.round(number); // Làm tròn và ép int
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            return formatter.format(roundedNumber);
        } catch (NumberFormatException e) {
            Log.e("Format Error", "Không thể chuyển đổi chuỗi thành số: " + numberString);
            return numberString;
        }
    }
}
