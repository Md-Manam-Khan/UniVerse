package com.universe.app;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class SignUpActivity extends BaseActivity 
{
    private EditText nameEditText;
    private EditText passwordEditText;
    private LocalDataManager dataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dataManager = new LocalDataManager(this);
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (!name.isEmpty() && !password.isEmpty()) 
            {
                dataManager.saveStudent(name, password);
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } 
            else 
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}