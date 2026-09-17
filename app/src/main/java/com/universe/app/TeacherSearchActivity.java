package com.universe.app;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class TeacherSearchActivity extends BaseActivity 
{
    private Spinner departmentSpinner;
    private Spinner designationSpinner;
    private ListView teacherListView;
    private LocalDataManager dataManager;
    private FirebaseManager firebaseManager;
    private List<Map<String, String>> teacherList;
    private ArrayAdapter<String> adapter;
    private String currentUserName;
    private String currentUserProfileKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_search);
        currentUserName = getIntent().getStringExtra("currentUserName");
        currentUserProfileKey = getIntent().getStringExtra("currentUserProfileKey");
        if (currentUserName == null) 
        {
            currentUserName = "Student";
        }
        dataManager = new LocalDataManager(this);
        firebaseManager = new FirebaseManager();
        teacherList = new ArrayList<>();
        departmentSpinner = findViewById(R.id.departmentSpinner);
        designationSpinner = findViewById(R.id.designationSpinner);
        teacherListView = findViewById(R.id.teacherListView);
        Button seeButton = findViewById(R.id.seeButton);
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"CSE", "ME", "EEE", "CE", "English", "BBA"});
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(deptAdapter);
        ArrayAdapter<String> designationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Professor", "Associate Professor", "Lecturer"});
        designationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        designationSpinner.setAdapter(designationAdapter);
        adapter = new ArrayAdapter<>(this, R.layout.list_item_bold, R.id.listItemText, new ArrayList<>());
        teacherListView.setAdapter(adapter);
        seeButton.setOnClickListener(v -> searchTeachers());
        teacherListView.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> teacher = teacherList.get(position);
            Intent intent = new Intent(TeacherSearchActivity.this, ProfileActivity.class);
            intent.putExtra("name", teacher.get("name"));
            intent.putExtra("story", teacher.get("story"));
            intent.putExtra("profileKey", teacher.get("key"));
            intent.putExtra("currentUserName", currentUserName);
            intent.putExtra("currentUserProfileKey", currentUserProfileKey);
            startActivity(intent);
        });
    }
    private void searchTeachers() 
        firebaseManager.fetchAllProfiles(new FirebaseManager.ProfilesListener() {
            @Override
            public void onResult(List<Map<String, String>> profiles) 
            {
                dataManager.importProfiles(profiles);
                runSearch();
            }
            @Override
            public void onError() 
            {
                runSearch();
            }
        });
    }
    private void runSearch() 
    {
        String department = departmentSpinner.getSelectedItem().toString();
        String designation = designationSpinner.getSelectedItem().toString();
        teacherList = dataManager.getTeachersByDeptAndDesignation(department, designation);
        List<String> names = new ArrayList<>();
        List<Map<String, String>> filteredList = new ArrayList<>();
        for (Map<String, String> teacher : teacherList) 
        {
            String teacherKey = teacher.get("key");
            if (currentUserProfileKey == null || !teacherKey.equals(currentUserProfileKey)) {
                filteredList.add(teacher);
                names.add(teacher.get("name"));
            }
        }
        teacherList = filteredList;
        adapter.clear();
        adapter.addAll(names);
        adapter.notifyDataSetChanged();
        if (names.isEmpty()) 
        {
            Toast.makeText(this, "No teachers found", Toast.LENGTH_SHORT).show();
        }
    }
}