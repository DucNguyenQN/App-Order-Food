package com.example.foodorderingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingapp.Adapter.ChatListAdapter;
import com.example.foodorderingapp.databinding.ActivityChatListBinding;
import com.example.foodorderingapp.model.ChatPreview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    ActivityChatListBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ChatListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter = new ChatListAdapter(new ArrayList<>(), currentUserId, chat -> {
            // Khi click vào đoạn chat
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("chatId", chat.getChatId());
            intent.putExtra("receiverId", chat.getOtherUserId());
            startActivity(intent);
        });
        db.collection("chats")
                .whereArrayContains("participants", currentUserId)
                .orderBy("lastTimestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ChatPreview> chatList = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String chatId = doc.getId();
                        String lastMessage = doc.getString("lastMessage");
                        long lastTimestamp = doc.getLong("lastTimestamp");

                        List<String> participants = (List<String>) doc.get("participants");
                        String otherUserId = "";
                        for (String p : participants) {
                            if (!p.equals(currentUserId)) {
                                otherUserId = p;
                                break;
                            }
                        }

                        chatList.add(new ChatPreview(chatId, otherUserId, lastMessage, lastTimestamp));
                    }

                    // Gán vào RecyclerView adapter để hiển thị
                    Log.d("ChatList", "So doan chat: " + queryDocumentSnapshots.size());
                    Log.d("ChatList1", "So doan chat: " + chatList.size());
                    adapter.updateData(chatList);
                }).addOnFailureListener(e -> {
                    Log.e("ChatList", "Lỗi khi truy vấn: " + e.getMessage());
                });;
        binding.recycleChatList.setLayoutManager(new LinearLayoutManager(this));
        binding.recycleChatList.setAdapter(adapter);
    }
}