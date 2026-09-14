package com.universe.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class MainMenuActivity extends BaseActivity {

    private TextView noticeBox;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();
        noticeBox = findViewById(R.id.noticeBox);

        Switch darkModeSwitch = findViewById(R.id.darkModeSwitch);
        if (darkModeSwitch != null) {
            darkModeSwitch.setChecked(darkModeManager.isDarkMode());
            darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                darkModeManager.setDarkMode(isChecked);
                recreate();
            });
        }

        Button devContactButton = findViewById(R.id.devContactButton);
        if (devContactButton != null) {
            devContactButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainMenuActivity.this, DevContactActivity.class);
                startActivity(intent);
            });
        }

        loadNotices();

        Button adminLoginButton = findViewById(R.id.adminLoginButton);
        Button studentLoginButton = findViewById(R.id.studentLoginButton);
        Button teacherLoginButton = findViewById(R.id.teacherLoginButton);

        adminLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });

        studentLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, StudentLoginActivity.class);
            startActivity(intent);
        });

        teacherLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
        });
    }

    private void loadNotices() {
        firebaseManager.listenForLatestNotice(notice -> noticeBox.setText(notice));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotices();
    }
}
