package com.universe.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentSearchActivity extends BaseActivity {

    private Spinner departmentSpinner;
    private Spinner semesterSpinner;
    private ListView studentListView;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;
    private List<Map<String, String>> studentList;
    private ArrayAdapter<String> adapter;
    private String currentUserName;
    private String currentUserProfileKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);

        currentUserName = getIntent().getStringExtra("currentUserName");
        currentUserProfileKey = getIntent().getStringExtra("currentUserProfileKey");
        if (currentUserName == null) {
            currentUserName = "Student";
        }

        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();
        studentList = new ArrayList<>();

        departmentSpinner = findViewById(R.id.departmentSpinner);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        studentListView = findViewById(R.id.studentListView);
        Button searchButton = findViewById(R.id.searchButton);

        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"CSE", "ME", "EEE", "CE", "English", "BBA"});
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(deptAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(semesterAdapter);

        adapter = new ArrayAdapter<>(this, R.layout.list_item_bold, R.id.listItemText, new ArrayList<>());
        studentListView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> searchStudents());

        studentListView.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> student = studentList.get(position);
            Intent intent = new Intent(StudentSearchActivity.this, ProfileActivity.class);
            intent.putExtra("name", student.get("name"));
            intent.putExtra("story", student.get("story"));
            intent.putExtra("profileKey", student.get("key"));
            intent.putExtra("currentUserName", currentUserName);
            intent.putExtra("currentUserProfileKey", currentUserProfileKey);
            startActivity(intent);
        });
    }

    private void searchStudents() {
        firebaseManager.fetchAllProfiles(new FirebaseManager.ProfilesListener() {
            @Override
            public void onResult(List<Map<String, String>> profiles) {
                dataManager.importProfiles(profiles);
                runSearch();
            }

            @Override
            public void onError() {
                runSearch();
            }
        });
    }

    private void runSearch() {
        String department = departmentSpinner.getSelectedItem().toString();
        String semester = semesterSpinner.getSelectedItem().toString();

        studentList = dataManager.getStudentsByDeptAndSemester(department, semester);
        List<String> names = new ArrayList<>();
        List<Map<String, String>> filteredList = new ArrayList<>();
        
        for (Map<String, String> student : studentList) {
            String studentKey = student.get("key");
            if (currentUserProfileKey == null || !studentKey.equals(currentUserProfileKey)) {
                filteredList.add(student);
                names.add(student.get("name"));
            }
        }
        
        studentList = filteredList;

        adapter.clear();
        adapter.addAll(names);
        adapter.notifyDataSetChanged();

        if (names.isEmpty()) {
            Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
        }
    }
}

