package com.universe.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

public class TeacherLoginActivity extends BaseActivity {

    private EditText teacherNameEditText;
    private EditText teacherPasswordEditText;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();

        teacherNameEditText = findViewById(R.id.studentNameEditText);
        teacherPasswordEditText = findViewById(R.id.studentPasswordEditText);
        Button enterButton = findViewById(R.id.studentEnterButton);

        enterButton.setOnClickListener(v -> {
            String name = teacherNameEditText.getText().toString().trim();
            String password = teacherPasswordEditText.getText().toString().trim();

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            enterButton.setEnabled(false);
            firebaseManager.fetchAllProfiles(new FirebaseManager.ProfilesListener() {
                @Override
                public void onResult(List<Map<String, String>> profiles) {
                    dataManager.importProfiles(profiles);
                    enterButton.setEnabled(true);
                    attemptLogin(name, password);
                }

                @Override
                public void onError() {
                    enterButton.setEnabled(true);
                    attemptLogin(name, password);
                }
            });
        });
    }

    private void attemptLogin(String name, String password) {
        Map<String, String> teacher = dataManager.validateTeacherLogin(name, password);
        if (teacher != null) {
            Intent intent = new Intent(TeacherLoginActivity.this, TeacherDashboardActivity.class);
            intent.putExtra("teacherName", teacher.get("name"));
            intent.putExtra("profileKey", teacher.get("key"));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
        }
    }
}

