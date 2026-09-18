package com.universe.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.List;
import java.util.Map;

public class ProfileEditActivity extends BaseActivity {

    private EditText nameEditText;
    private EditText storyEditText;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;
    private String profileKey;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        profileKey = getIntent().getStringExtra("profileKey");
        userType = getIntent().getStringExtra("userType");
        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();

        nameEditText = findViewById(R.id.editNameEditText);
        storyEditText = findViewById(R.id.editStoryEditText);
        Button backButton = findViewById(R.id.backButton);
        Button saveButton = findViewById(R.id.saveButton);

        loadProfile();

        backButton.setOnClickListener(v -> {
            finish();
        });

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String story = storyEditText.getText().toString().trim();

            if (!name.isEmpty() && !story.isEmpty()) {
                dataManager.updateProfile(profileKey, name, story);
                try {
                    firebaseManager.updateProfileInfo(Long.parseLong(profileKey), name, story);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfile() {
        showProfile(dataManager.getProfileByKey(profileKey));
        firebaseManager.fetchAllProfiles(new FirebaseManager.ProfilesListener() {
            @Override
            public void onResult(List<Map<String, String>> profiles) {
                dataManager.importProfiles(profiles);
                showProfile(dataManager.getProfileByKey(profileKey));
            }

            @Override
            public void onError() {
            }
        });
    }

    private void showProfile(Map<String, String> profile) {
        if (profile != null) {
            nameEditText.setText(profile.get("name"));
            storyEditText.setText(profile.get("story"));
        }
    }
}
