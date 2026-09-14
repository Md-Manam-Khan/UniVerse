package com.universe.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends BaseActivity {

    private EditText messageEditText;
    private RecyclerView messagesRecyclerView;
    private FirebaseManager firebaseManager;
    private String currentUserProfileKey;
    private String otherUserProfileKey;
    private String conversationKey;
    private String profileName;
    private String currentUserName = "You";
    private MessageAdapter adapter;
    private List<Map<String, String>> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentUserProfileKey = getIntent().getStringExtra("currentUserProfileKey");
        otherUserProfileKey = getIntent().getStringExtra("otherUserProfileKey");
        profileName = getIntent().getStringExtra("profileName");
        currentUserName = getIntent().getStringExtra("currentUserName");
        if (currentUserName == null) {
            currentUserName = "You";
        }

        if (currentUserProfileKey == null || currentUserProfileKey.isEmpty()) {
            currentUserProfileKey = "default_current";
        }
        if (otherUserProfileKey == null || otherUserProfileKey.isEmpty()) {
            otherUserProfileKey = "default_other";
        }

        conversationKey = currentUserProfileKey.compareTo(otherUserProfileKey) < 0
                ? currentUserProfileKey + "_" + otherUserProfileKey
                : otherUserProfileKey + "_" + currentUserProfileKey;

        firebaseManager = new FirebaseManager();
        messages = new ArrayList<>();

        messageEditText = findViewById(R.id.messageEditText);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        Button sendButton = findViewById(R.id.sendButton);

        adapter = new MessageAdapter(messages);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(adapter);

        firebaseManager.listenForMessages(conversationKey, newMessages -> {
            messages.clear();
            messages.addAll(newMessages);
            adapter.notifyDataSetChanged();
            if (messages.size() > 0) {
                messagesRecyclerView.post(() -> messagesRecyclerView.scrollToPosition(messages.size() - 1));
            }
        });

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                firebaseManager.sendMessage(conversationKey, currentUserName, message);
                messageEditText.setText("");
            }
        });
    }


    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        private List<Map<String, String>> messages;

        public MessageAdapter(List<Map<String, String>> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            Map<String, String> message = messages.get(position);
            String sender = message.get("sender");
            boolean isCurrentUser = sender != null && sender.equals(currentUserName);
            
            holder.senderTextView.setText(sender);
            holder.messageTextView.setText(message.get("message"));
            
            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params = 
                (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) holder.messageBubble.getLayoutParams();
            
            if (isCurrentUser) {
                params.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
                params.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET;
                params.setMargins(0, 0, 0, 0);
                holder.messageBubble.setBackgroundResource(R.drawable.message_bubble_sender);
                holder.senderTextView.setTextColor(0xFF2E7D32);
            } else {
                params.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
                params.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET;
                params.setMargins(0, 0, 0, 0);
                holder.messageBubble.setBackgroundResource(R.drawable.message_bubble_background);
                holder.senderTextView.setTextColor(0xFF1565C0);
            }
            holder.messageBubble.setLayoutParams(params);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView senderTextView;
            TextView messageTextView;
            android.view.ViewGroup messageBubble;

            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                senderTextView = itemView.findViewById(R.id.senderTextView);
                messageTextView = itemView.findViewById(R.id.messageTextView);
                messageBubble = itemView.findViewById(R.id.messageBubble);
            }
        }
    }
}

