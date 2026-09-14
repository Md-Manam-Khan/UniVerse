package com.universe.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String name = getIntent().getStringExtra("name");
        String story = getIntent().getStringExtra("story");
        String profileKey = getIntent().getStringExtra("profileKey");

        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView storyTextView = findViewById(R.id.storyTextView);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button chatButton = findViewById(R.id.chatButton);

        nameTextView.setText(name != null ? name : "Name");
        storyTextView.setText(story != null ? story : "Story");

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
            intent.putExtra("otherUserProfileKey", profileKey);
            intent.putExtra("profileName", name);
            String currentUserName = getIntent().getStringExtra("currentUserName");
            String currentUserProfileKey = getIntent().getStringExtra("currentUserProfileKey");
            if (currentUserName != null) {
                intent.putExtra("currentUserName", currentUserName);
            }
            if (currentUserProfileKey != null) {
                intent.putExtra("currentUserProfileKey", currentUserProfileKey);
            }
            startActivity(intent);
        });
    }
}

