package com.universe.app;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;
import java.util.Map;
public class ChangeNameActivity extends BaseActivity 
{
    private EditText newNameEditText;
    private EditText currentPasswordEditText;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;
    private String profileKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();
        profileKey = getIntent().getStringExtra("profileKey");
        newNameEditText = findViewById(R.id.newNameEditText);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String newName = newNameEditText.getText().toString().trim();
            String currentPassword = currentPasswordEditText.getText().toString().trim();
            if (newName.isEmpty() || currentPassword.isEmpty()) 
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            saveButton.setEnabled(false);
            firebaseManager.fetchAllProfiles(new FirebaseManager.ProfilesListener() 
            {
                @Override
                public void onResult(List<Map<String, String>> profiles) 
                {
                    dataManager.importProfiles(profiles);
                    saveButton.setEnabled(true);
                    finishChange(newName, currentPassword);
                }
                @Override
                public void onError() 
                {
                    saveButton.setEnabled(true);
                    finishChange(newName, currentPassword);
                }
            });
        });
    }
    private void finishChange(String newName, String currentPassword) 
    {
        Map<String, String> profile = dataManager.getProfileByKey(profileKey);
        if (profile == null) 
        {
            Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
            return;
        }
        String storedPassword = profile.get("password");
        if (storedPassword == null || !storedPassword.equals(currentPassword)) 
        {
            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }
        String story = profile.get("story");
        dataManager.updateProfile(profileKey, newName, story);
        try 
        {
            firebaseManager.updateProfileInfo(Long.parseLong(profileKey), newName, story);
        } 
        catch (NumberFormatException e) 
        {
            e.printStackTrace();
        }
        Toast.makeText(this, "Name updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}