package com.example.foodorderingapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingapp.R;
import com.example.foodorderingapp.model.ChatPreview;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatPreview> chatList;
    private String currentUserId;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(ChatPreview chat);
    }

    public ChatListAdapter(List<ChatPreview> chatList, String currentUserId, OnChatClickListener listener) {
        this.chatList = chatList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    public void updateData(List<ChatPreview> newList) {
        this.chatList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_preview, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatPreview chat = chatList.get(position);
        //holder.txtName.setText(chat.getOtherUserId()); // Có thể thay bằng tên user thật nếu cần
        getNameRes(chat.getOtherUserId(), holder);
        holder.txtMessage.setText(chat.getLastMessage());

        // Định dạng thời gian
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = sdf.format(new Date(chat.getLastTimestamp()));
        holder.txtTime.setText(time);

        holder.itemView.setOnClickListener(v -> {
            listener.onChatClick(chat);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtMessage, txtTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
    private void getNameRes(String otherUserId, ChatViewHolder holder ){
        DatabaseReference resName = FirebaseDatabase.getInstance().getReference().child("user").child(otherUserId).child("namofresturant");
        resName.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String name = task.getResult().getValue(String.class);
                if (name != null) {
                    // Cập nhật tên nhà hàng vào giao diện
                    holder.txtName.setText(name);
                }
            }
        });
    }
}
