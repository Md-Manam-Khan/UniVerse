package com.universe.app;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class ChangePasswordAdminActivity extends BaseActivity 
{
    private EditText nameEditText;
    private EditText passwordEditText;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_admin);
        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button saveButton = findViewById(R.id.saveButton);
        nameEditText.setText(dataManager.getAdminName());
        passwordEditText.setText(dataManager.getAdminPassword());
        firebaseManager.getAdminCredentials(new FirebaseManager.AdminCredentialsListener() 
        {
            @Override
            public void onResult(String cloudName, String cloudPassword) 
            {
                if (cloudName != null && cloudPassword != null) 
                {
                    dataManager.updateAdminCredentials(cloudName, cloudPassword);
                    nameEditText.setText(cloudName);
                    passwordEditText.setText(cloudPassword);
                }
            }
            @Override
            public void onError() 
            {
            }
        });
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (!name.isEmpty() && !password.isEmpty()) 
            {
                dataManager.updateAdminCredentials(name, password);
                firebaseManager.updateAdminCredentials(name, password);
                Toast.makeText(this, "Admin credentials updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } 
            else 
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}