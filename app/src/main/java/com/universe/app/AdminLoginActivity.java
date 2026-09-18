package com.universe.app;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class AdminLoginActivity extends BaseActivity
{
    private EditText adminNameEditText;
    private EditText adminPasswordEditText;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();
        adminNameEditText = findViewById(R.id.adminNameEditText);
        adminPasswordEditText = findViewById(R.id.adminPasswordEditText);
        Button enterButton = findViewById(R.id.adminEnterButton);
        enterButton.setOnClickListener(v -> {
            String name = adminNameEditText.getText().toString().trim();
            String password = adminPasswordEditText.getText().toString().trim();
            enterButton.setEnabled(false);
            firebaseManager.getAdminCredentials(new FirebaseManager.AdminCredentialsListener() {
                @Override
                public void onResult(String cloudName, String cloudPassword) {
                    enterButton.setEnabled(true);
                    boolean matches;
                    if (cloudName != null && cloudPassword != null)
                    {
                        dataManager.updateAdminCredentials(cloudName, cloudPassword);
                        matches = cloudName.equals(name) && cloudPassword.equals(password);
                    }
                    else
                    {
                        matches = dataManager.validateAdminLogin(name, password);
                        if (matches)
                        {
                            firebaseManager.updateAdminCredentials(name, password);
                        }
                    }
                    finishLogin(matches);
                }
                @Override
                public void onError()
                {
                    enterButton.setEnabled(true);
                    finishLogin(dataManager.validateAdminLogin(name, password));
                }
            });
        });
    }
    private void finishLogin(boolean success)
    {
        if (success)
        {
            Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(AdminLoginActivity.this, "Try again", Toast.LENGTH_SHORT).show();
        }
    }
}
