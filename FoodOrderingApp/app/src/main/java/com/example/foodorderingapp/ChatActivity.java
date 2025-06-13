package com.example.foodorderingapp;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingapp.Adapter.ChatAdapter;
import com.example.foodorderingapp.databinding.ActivityChatBinding;
import com.example.foodorderingapp.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String receiverId; //= "lbMgNjRLPteODnnlZOg6T80MLci1";
    String chatId;//= generateChatId(currentUserId, receiverId);
    ActivityChatBinding binding;
    List<ChatMessage> messageList = new ArrayList<>();
    private int previousHeight = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        //chatId = intent.getStringExtra("chatId");
        receiverId = intent.getStringExtra("receiverId");
        chatId = generateChatId(currentUserId, receiverId);
        getNameRes();
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.main.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            binding.main.getWindowVisibleDisplayFrame(r);

            int screenHeight = binding.main.getRootView().getHeight();
            int heightDiff = screenHeight - r.bottom;

            if (previousHeight != heightDiff) {
                previousHeight = heightDiff;

                // Nếu bàn phím hiện (heightDiff > 15% chiều cao màn hình)
                if (heightDiff > screenHeight * 0.15) {
                    // Đẩy toàn bộ layout lên
                    binding.main.setTranslationY(-heightDiff);
                } else {
                    // Bàn phím tắt -> trả lại layout như cũ
                    binding.main.setTranslationY(0);
                }
            }
        });
        ChatAdapter adapter = new ChatAdapter(this, currentUserId);
        adapter.setData(messageList);
        binding.recycleChat.setAdapter(adapter);
        binding.recycleChat.setLayoutManager(new LinearLayoutManager(this));
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    messageList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        ChatMessage msg = doc.toObject(ChatMessage.class);
                        messageList.add(msg);
                    }

                    // Cập nhật adapter hiển thị tin nhắn
                    adapter.notifyDataSetChanged();
                    binding.recycleChat.scrollToPosition(messageList.size() - 1);
                });
        binding.imgchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = binding.edtinputtext.getText().toString().trim();
                if (!msg.isEmpty()) {
                    ChatMessage message = new ChatMessage(
                            currentUserId,
                            msg,
                            System.currentTimeMillis(),
                            "text"
                    );

                    DocumentReference chatRef = db.collection("chats").document(chatId);

                    chatRef.collection("messages")
                            .add(message)
                            .addOnSuccessListener(documentReference -> {
                                binding.edtinputtext.setText("");

                                // 2. Cập nhật lastMessage và lastTimestamp vào document chính
                                Map<String, Object> chatUpdate = new HashMap<>();
                                chatUpdate.put("lastMessage", message.getMessage());
                                chatUpdate.put("lastTimestamp", System.currentTimeMillis());
                                chatUpdate.put("participants", Arrays.asList(currentUserId, receiverId)); // chỉ khi tạo mới

                                // Set hoặc cập nhật luôn document "chats/{chatId}"
                                chatRef.set(chatUpdate, SetOptions.merge());
                            });
                    db.collection("chats")
                            .document(chatId)
                            .update("lastMessage", message.getMessage(),
                                    "lastTimestamp", System.currentTimeMillis());
                }
            }
        });

    }
    private void getNameRes() {
        DatabaseReference resName = FirebaseDatabase.getInstance().getReference().child("user").child(receiverId).child("namofresturant");
        resName.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String name = task.getResult().getValue(String.class);
                if (name != null) {
                    binding.txtName.setText(name);
                }
            }
        });
    }
    private String generateChatId(String id1, String id2) {
        return (id1.compareTo(id2) < 0) ? id1 + "_" + id2 : id2 + "_" + id1;
    }
}