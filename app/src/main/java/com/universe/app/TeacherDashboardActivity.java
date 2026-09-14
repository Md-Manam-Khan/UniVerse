package com.universe.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class TeacherDashboardActivity extends BaseActivity {

    private String teacherName;
    private String profileKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        teacherName = getIntent().getStringExtra("teacherName");
        profileKey = getIntent().getStringExtra("profileKey");
        if (teacherName == null) {
            teacherName = "Teacher";
        }

        Button logoutButton = findViewById(R.id.logoutButton);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        Button changeNameButton = findViewById(R.id.changeNameButton);
        Button teacherButton = findViewById(R.id.teacherButton);
        Button studentButton = findViewById(R.id.studentButton);
        Button profileButton = findViewById(R.id.profileButton);
        TextView messageNotificationBox = findViewById(R.id.messageNotificationBox);

        FirebaseManager firebaseManager = new FirebaseManager();
        firebaseManager.listenForLatestIncomingMessage(profileKey, teacherName, (senderName, messageText) -> {
            if (senderName != null) {
                messageNotificationBox.setVisibility(android.view.View.VISIBLE);
                messageNotificationBox.setText(senderName + " texted you");
            }
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        teacherButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, TeacherSearchActivity.class);
            intent.putExtra("currentUserName", teacherName);
            intent.putExtra("currentUserProfileKey", profileKey);
            startActivity(intent);
        });

        studentButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, StudentSearchActivity.class);
            intent.putExtra("currentUserName", teacherName);
            intent.putExtra("currentUserProfileKey", profileKey);
            startActivity(intent);
        });

        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, ChangePasswordActivity.class);
            intent.putExtra("profileKey", profileKey);
            intent.putExtra("userType", "Teacher");
            intent.putExtra("currentName", teacherName);
            startActivity(intent);
        });

        changeNameButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, ChangeNameActivity.class);
            intent.putExtra("profileKey", profileKey);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, ProfileEditActivity.class);
            intent.putExtra("profileKey", profileKey);
            intent.putExtra("userType", "Teacher");
            startActivity(intent);
        });
    }
}
