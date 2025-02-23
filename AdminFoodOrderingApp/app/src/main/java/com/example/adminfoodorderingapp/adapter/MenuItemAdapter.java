package com.example.adminfoodorderingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adminfoodorderingapp.R;
import com.example.adminfoodorderingapp.model.AllMenu;

import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.AllItemViewolder>{
    private Context context;
    private List<AllMenu> menuList;
    private int[] itemQuantities;

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
        itemQuantities = new int[menuList.size()];
        for (int i = 0; i < itemQuantities.length; i++) {
            itemQuantities[i] = 1; // Khởi tạo mỗi phần tử là 1
        }
        AllMenu menu = menuList.get(position);
        holder.txtName.setText(menu.getFoodName());
        holder.txtPrice.setText(menu.getFoodPrice());
        holder.txtQuantity.setText(itemQuantities[position]+"");
        Glide.with(context).load(menu.getFoodImage()).into(holder.foodImage);
        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuanity(holder,holder.getAdapterPosition());
            }
        });
        holder.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increseQuanity(holder,holder.getAdapterPosition());
            }
        });
        holder.imgTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteQuanity(holder,holder.getAdapterPosition());
            }
        });

    }

    private void deleteQuanity(AllItemViewolder holder,int position) {
//        if (position >= 0 && position < menuList.size()) {
//            menuList.remove(position);
//            itemQuantities = new int[menuList.size()]; // Cập nhật lại mảng itemQuantities
//            notifyItemRemoved(position);
//            notifyItemRangeChanged(position, menuList.size()); // Cập nhật lại vị trí các item
//        }
        menuList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, menuList.size());
    }

    private void increseQuanity(AllItemViewolder holder, int position) {
        if (itemQuantities[position] < 10){
            itemQuantities[position]++;
            holder.txtQuantity.setText(itemQuantities[position]+"");
        }
    }

    private void decreaseQuanity(AllItemViewolder holder,int position) {
        if (itemQuantities[position] > 1){
            itemQuantities[position]--;
            holder.txtQuantity.setText(itemQuantities[position]+"");
        }
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
        ImageButton imgMinus, imgPlus, imgTrash;
        TextView txtName, txtPrice, txtQuantity;
        public AllItemViewolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            imgMinus = itemView.findViewById(R.id.imgMinus);
            imgPlus = itemView.findViewById(R.id.imgplus);
            imgTrash = itemView.findViewById(R.id.imgTrash);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuanity);
        }
    }
}
