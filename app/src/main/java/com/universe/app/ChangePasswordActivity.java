package com.universe.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.List;
import java.util.Map;

public class ChangePasswordActivity extends BaseActivity {

    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;
    private String profileKey;
    private String userType;
    private String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();
        profileKey = getIntent().getStringExtra("profileKey");
        userType = getIntent().getStringExtra("userType");
        currentName = getIntent().getStringExtra("currentName");

        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            saveButton.setEnabled(false);
            firebaseManager.fetchAllProfiles(new FirebaseManager.ProfilesListener() {
                @Override
                public void onResult(List<Map<String, String>> profiles) {
                    dataManager.importProfiles(profiles);
                    saveButton.setEnabled(true);
                    finishChange(currentPassword, newPassword);
                }

                @Override
                public void onError() {
                    saveButton.setEnabled(true);
                    finishChange(currentPassword, newPassword);
                }
            });
        });
    }

    private void finishChange(String currentPassword, String newPassword) {
        String storedPassword = dataManager.getProfilePassword(profileKey);
        if (storedPassword.isEmpty()) {
            Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!storedPassword.equals(currentPassword)) {
            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        dataManager.updateProfilePassword(profileKey, newPassword);
        try {
            firebaseManager.updateProfilePassword(Long.parseLong(profileKey), newPassword);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

}
