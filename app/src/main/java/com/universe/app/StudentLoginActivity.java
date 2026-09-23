package com.universe.app;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;
import java.util.Map;
public class StudentLoginActivity extends BaseActivity
{
    private EditText studentNameEditText;
    private EditText studentPasswordEditText;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();
        studentNameEditText = findViewById(R.id.studentNameEditText);
        studentPasswordEditText = findViewById(R.id.studentPasswordEditText);
        Button enterButton = findViewById(R.id.studentEnterButton);
        enterButton.setOnClickListener(v -> {
            String name = studentNameEditText.getText().toString().trim();
            String password = studentPasswordEditText.getText().toString().trim();
            if (name.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            enterButton.setEnabled(false);
            firebaseManager.fetchAllProfiles(new FirebaseManager.ProfilesListener() {
                @Override
                public void onResult(List<Map<String, String>> profiles)
                {
                    dataManager.importProfiles(profiles);
                    enterButton.setEnabled(true);
                    attemptLogin(name, password);
                }
                @Override
                public void onError()
                {
                    enterButton.setEnabled(true);
                    attemptLogin(name, password);
                }
            });
        });
    }
    private void attemptLogin(String name, String password)
    {
        Map<String, String> student = dataManager.validateStudentLogin(name, password);
        if (student != null)
        {
            Intent intent = new Intent(StudentLoginActivity.this, StudentDashboardActivity.class);
            intent.putExtra("studentName", student.get("name"));
            intent.putExtra("profileKey", student.get("key"));
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
        }
    }
}