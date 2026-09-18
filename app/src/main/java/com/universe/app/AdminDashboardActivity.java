package com.universe.app;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class AdminDashboardActivity extends BaseActivity
{
    private EditText messageEditText;
    private LinearLayout addInfoLayout;
    private Spinner userTypeSpinner;
    private Spinner departmentSpinner;
    private Spinner designationOrSemesterSpinner;
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText storyEditText;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();
        messageEditText = findViewById(R.id.messageEditText);
        addInfoLayout = findViewById(R.id.addInfoLayout);
        userTypeSpinner = findViewById(R.id.userTypeSpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        designationOrSemesterSpinner = findViewById(R.id.designationOrSemesterSpinner);
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        storyEditText = findViewById(R.id.storyEditText);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button sendButton = findViewById(R.id.sendButton);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        Button addInfoButton = findViewById(R.id.addInfoButton);
        Button enterInfoButton = findViewById(R.id.enterInfoButton);
        Button wipeDataButton = findViewById(R.id.wipeDataButton);
        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Teacher", "Student"});
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(userTypeAdapter);
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"CSE", "ME", "EEE", "CE", "English", "BBA"});
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(deptAdapter);
        userTypeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                {
                    ArrayAdapter<String> designationAdapter = new ArrayAdapter<>(AdminDashboardActivity.this,
                            android.R.layout.simple_spinner_item,
                            new String[]{"Professor", "Associate Professor", "Lecturer"});
                    designationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    designationOrSemesterSpinner.setAdapter(designationAdapter);
                }
                else
                {
                    ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(AdminDashboardActivity.this,
                            android.R.layout.simple_spinner_item,
                            new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
                    semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    designationOrSemesterSpinner.setAdapter(semesterAdapter);
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (message.isEmpty())
            {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = dateFormat.format(new Date());
            long timestamp = System.currentTimeMillis();
            dataManager.saveNotice(message, date, timestamp);
            firebaseManager.postNotice(message, date, timestamp);
            messageEditText.setText("");
            Toast.makeText(this, "Notice sent successfully!", Toast.LENGTH_SHORT).show();
        });
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ChangePasswordAdminActivity.class);
            startActivity(intent);
        });
        addInfoButton.setOnClickListener(v -> {
            if (addInfoLayout.getVisibility() == View.GONE) {
                addInfoLayout.setVisibility(View.VISIBLE);
            } else {
                addInfoLayout.setVisibility(View.GONE);
            }
        });
        enterInfoButton.setOnClickListener(v -> {
            String userType = userTypeSpinner.getSelectedItem().toString();
            String department = departmentSpinner.getSelectedItem().toString();
            String designationOrSemester = designationOrSemesterSpinner.getSelectedItem().toString();
            String name = nameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String story = storyEditText.getText().toString().trim();
            if (!name.isEmpty() && !password.isEmpty() && !story.isEmpty())
            {
                String designation = userType.equals("Teacher") ? designationOrSemester : "";
                String semester = userType.equals("Student") ? designationOrSemester : "";
                long id = System.currentTimeMillis();
                dataManager.saveProfile(name, password, story, department, userType, designation, semester, id);
                firebaseManager.saveProfile(name, password, story, department, userType, designation, semester, id);
                nameEditText.setText("");
                passwordEditText.setText("");
                storyEditText.setText("");
                Toast.makeText(this, "Profile added!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Please fill name, password and bio", Toast.LENGTH_SHORT).show();
            }
        });
        wipeDataButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Clear Notice Board")
                .setMessage("Are you sure you want to delete all notices?\n\nStudent/teacher profiles and chat messages will NOT be affected and stay saved forever.\n\nThis action cannot be undone!")
                .setPositiveButton("Yes, Clear Notices", (dialog, which) -> {
                    dataManager.wipeAllData();
                    firebaseManager.wipeCloudData();
                    Toast.makeText(this, "Notice board cleared!", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        });
    }
}
