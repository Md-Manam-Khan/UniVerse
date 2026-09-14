package com.universe.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class StudentDashboardActivity extends BaseActivity {

    private String studentName;
    private String profileKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        studentName = getIntent().getStringExtra("studentName");
        profileKey = getIntent().getStringExtra("profileKey");
        if (studentName == null) {
            studentName = "Student";
        }

        Button logoutButton = findViewById(R.id.logoutButton);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        Button changeNameButton = findViewById(R.id.changeNameButton);
        Button profileButton = findViewById(R.id.profileButton);
        Button teacherButton = findViewById(R.id.teacherButton);
        Button studentButton = findViewById(R.id.studentButton);
        TextView messageNotificationBox = findViewById(R.id.messageNotificationBox);

        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.listenForLatestIncomingMessage(profileKey, studentName, (senderName, messageText) -> {
            if (senderName != null) {
                messageNotificationBox.setVisibility(android.view.View.VISIBLE);
                messageNotificationBox.setText(senderName + " texted you");
            }
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, ChangePasswordActivity.class);
            intent.putExtra("profileKey", profileKey);
            intent.putExtra("userType", "Student");
            intent.putExtra("currentName", studentName);
            startActivity(intent);
        });

        changeNameButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, ChangeNameActivity.class);
            intent.putExtra("profileKey", profileKey);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, ProfileEditActivity.class);
            intent.putExtra("profileKey", profileKey);
            intent.putExtra("userType", "Student");
            startActivity(intent);
        });

        teacherButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, TeacherSearchActivity.class);
            intent.putExtra("currentUserName", studentName);
            intent.putExtra("currentUserProfileKey", profileKey);
            startActivity(intent);
        });

        studentButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentSearchActivity.class);
            intent.putExtra("currentUserName", studentName);
            intent.putExtra("currentUserProfileKey", profileKey);
            startActivity(intent);
        });
    }
}

