package com.universe.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDataManager {
    private static final String PREFS_NAME = "BAUSTAppData";
    private static final String KEY_NOTICES = "notices";
    private static final String KEY_STUDENTS = "students";
    private static final String KEY_PROFILES = "profiles";
    private static final String KEY_MESSAGES = "messages";
    private static final String KEY_ADMIN_NAME = "admin_name";
    private static final String KEY_ADMIN_PASSWORD = "admin_password";
    private static final String DEFAULT_ADMIN_NAME = "adminbaustk";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private SharedPreferences prefs;
    private Context context;

    public LocalDataManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (!prefs.contains(KEY_ADMIN_NAME)) {
            prefs.edit().putString(KEY_ADMIN_NAME, DEFAULT_ADMIN_NAME).apply();
        }
        if (!prefs.contains(KEY_ADMIN_PASSWORD)) {
            prefs.edit().putString(KEY_ADMIN_PASSWORD, DEFAULT_ADMIN_PASSWORD).apply();
        }
    }

    public boolean validateAdminLogin(String name, String password) {
        String storedName = prefs.getString(KEY_ADMIN_NAME, null);
        String storedPassword = prefs.getString(KEY_ADMIN_PASSWORD, null);

        if (storedName != null && storedPassword != null) {
            return storedName.equals(name) && storedPassword.equals(password);
        }

        return DEFAULT_ADMIN_NAME.equals(name) && DEFAULT_ADMIN_PASSWORD.equals(password);
    }

    public void updateAdminCredentials(String name, String password) {
        prefs.edit()
            .putString(KEY_ADMIN_NAME, name)
            .putString(KEY_ADMIN_PASSWORD, password)
            .apply();
    }

    public String getAdminName() {
        return prefs.getString(KEY_ADMIN_NAME, DEFAULT_ADMIN_NAME);
    }

    public String getAdminPassword() {
        return prefs.getString(KEY_ADMIN_PASSWORD, DEFAULT_ADMIN_PASSWORD);
    }

    public void saveNotice(String message, String date, long timestamp) {
        try {
            JSONArray notices = getNoticesArray();
            JSONObject notice = new JSONObject();
            long id = System.currentTimeMillis();
            notice.put("message", message);
            notice.put("date", date);
            notice.put("timestamp", timestamp);
            notice.put("id", id);
            notices.put(notice);
            prefs.edit().putString(KEY_NOTICES, notices.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLatestNotice() {
        try {
            JSONArray notices = getNoticesArray();
            if (notices.length() == 0) {
                return "No notices yet.";
            }

            JSONObject latest = null;
            long latestTimestamp = Long.MIN_VALUE;

            for (int i = 0; i < notices.length(); i++) {
                JSONObject notice = notices.getJSONObject(i);
                long timestamp = notice.optLong("timestamp", 0);
                if (timestamp > latestTimestamp) {
                    latestTimestamp = timestamp;
                    latest = notice;
                }
            }

            if (latest != null) {
                String date = latest.optString("date", "");
                String message = latest.optString("message", "");
                return date + ": " + message;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "No notices yet.";
    }

    private JSONArray getNoticesArray() {
        String noticesJson = prefs.getString(KEY_NOTICES, "[]");
        try {
            return new JSONArray(noticesJson);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public void saveStudent(String name, String password) {
        try {
            JSONArray students = getStudentsArray();
            JSONObject student = new JSONObject();
            student.put("name", name);
            student.put("password", password);
            students.put(student);
            prefs.edit().putString(KEY_STUDENTS, students.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean validateStudent(String name, String password) {
        try {
            JSONArray students = getStudentsArray();
            for (int i = 0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);
                if (student.optString("name", "").equals(name) &&
                    student.optString("password", "").equals(password)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private JSONArray getStudentsArray() {
        String studentsJson = prefs.getString(KEY_STUDENTS, "[]");
        try {
            return new JSONArray(studentsJson);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public void saveProfile(String name, String password, String story, String department, String userType,
                           String designation, String semester, long id) {
        try {
            JSONArray profiles = getProfilesArray();
            JSONObject profile = new JSONObject();
            profile.put("name", name);
            profile.put("password", password);
            profile.put("story", story);
            profile.put("department", department);
            profile.put("userType", userType);
            if (userType.equals("Teacher")) {
                profile.put("designation", designation);
            } else {
                profile.put("semester", semester);
            }
            profile.put("id", id);
            profiles.put(profile);
            prefs.edit().putString(KEY_PROFILES, profiles.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateProfile(String profileKey, String name, String story) {
        try {
            JSONArray profiles = getProfilesArray();
            JSONArray updatedProfiles = new JSONArray();
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                if (String.valueOf(profile.optLong("id", 0)).equals(profileKey)) {
                    profile.put("name", name);
                    profile.put("story", story);
                }
                updatedProfiles.put(profile);
            }
            prefs.edit().putString(KEY_PROFILES, updatedProfiles.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateProfilePassword(String profileKey, String newPassword) {
        try {
            JSONArray profiles = getProfilesArray();
            JSONArray updatedProfiles = new JSONArray();
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                if (String.valueOf(profile.optLong("id", 0)).equals(profileKey)) {
                    profile.put("password", newPassword);
                }
                updatedProfiles.put(profile);
            }
            prefs.edit().putString(KEY_PROFILES, updatedProfiles.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> validateTeacherLogin(String name, String password) {
        try {
            JSONArray profiles = getProfilesArray();
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                if (profile.optString("userType", "").equals("Teacher") &&
                    profile.optString("name", "").equals(name) &&
                    profile.optString("password", "").equals(password)) {
                    Map<String, String> result = new HashMap<>();
                    result.put("name", profile.optString("name", ""));
                    result.put("key", String.valueOf(profile.optLong("id", 0)));
                    return result;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> validateStudentLogin(String name, String password) {
        try {
            JSONArray profiles = getProfilesArray();
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                if (profile.optString("userType", "").equals("Student") &&
                    profile.optString("name", "").equals(name) &&
                    profile.optString("password", "").equals(password)) {
                    Map<String, String> result = new HashMap<>();
                    result.put("name", profile.optString("name", ""));
                    result.put("key", String.valueOf(profile.optLong("id", 0)));
                    return result;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getProfileByKey(String profileKey) {
        try {
            JSONArray profiles = getProfilesArray();
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                if (String.valueOf(profile.optLong("id", 0)).equals(profileKey)) {
                    Map<String, String> result = new HashMap<>();
                    result.put("name", profile.optString("name", ""));
                    result.put("story", profile.optString("story", ""));
                    result.put("userType", profile.optString("userType", ""));
                    result.put("password", profile.optString("password", ""));
                    return result;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getProfilePassword(String profileKey) {
        try {
            JSONArray profiles = getProfilesArray();
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                if (String.valueOf(profile.optLong("id", 0)).equals(profileKey)) {
                    return profile.optString("password", "");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public List<Map<String, String>> getTeachersByDeptAndDesignation(String department, String designation) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            JSONArray profiles = getProfilesArray();
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                if (profile.optString("userType", "").equals("Teacher") &&
                    profile.optString("department", "").equals(department) &&
                    profile.optString("designation", "").equals(designation)) {
                    Map<String, String> teacher = new HashMap<>();
                    teacher.put("name", profile.optString("name", ""));
                    teacher.put("story", profile.optString("story", ""));
                    teacher.put("key", String.valueOf(profile.optLong("id", 0)));
                    result.add(teacher);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Map<String, String>> getStudentsByDeptAndSemester(String department, String semester) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            JSONArray profiles = getProfilesArray();
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                if (profile.optString("userType", "").equals("Student") &&
                    profile.optString("department", "").equals(department) &&
                    profile.optString("semester", "").equals(semester)) {
                    Map<String, String> student = new HashMap<>();
                    student.put("name", profile.optString("name", ""));
                    student.put("story", profile.optString("story", ""));
                    student.put("key", String.valueOf(profile.optLong("id", 0)));
                    result.add(student);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, String> getProfileById(String profileKey) {
        try {
            JSONArray profiles = getProfilesArray();
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                if (String.valueOf(profile.optLong("id", 0)).equals(profileKey)) {
                    Map<String, String> result = new HashMap<>();
                    result.put("name", profile.optString("name", ""));
                    result.put("story", profile.optString("story", ""));
                    return result;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void importProfiles(List<Map<String, String>> profiles) {
        try {
            JSONArray arr = new JSONArray();
            for (Map<String, String> p : profiles) {
                JSONObject o = new JSONObject();
                o.put("name", p.get("name"));
                o.put("password", p.get("password"));
                o.put("story", p.get("story"));
                o.put("department", p.get("department"));
                o.put("userType", p.get("userType"));
                o.put("designation", p.get("designation"));
                o.put("semester", p.get("semester"));
                long id = 0;
                try {
                    id = Long.parseLong(p.get("id"));
                } catch (NumberFormatException ignored) {
                }
                o.put("id", id);
                arr.put(o);
            }
            prefs.edit().putString(KEY_PROFILES, arr.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray getProfilesArray() {
        String profilesJson = prefs.getString(KEY_PROFILES, "[]");
        try {
            return new JSONArray(profilesJson);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    private String getConversationKey(String profileKey1, String profileKey2) {
        if (profileKey1 == null || profileKey2 == null) {
            return profileKey1 != null ? profileKey1 : (profileKey2 != null ? profileKey2 : "default");
        }
        if (profileKey1.compareTo(profileKey2) < 0) {
            return profileKey1 + "_" + profileKey2;
        } else {
            return profileKey2 + "_" + profileKey1;
        }
    }

    public void saveMessage(String currentUserProfileKey, String otherUserProfileKey, String sender, String message) {
        try {
            String conversationKey = getConversationKey(currentUserProfileKey, otherUserProfileKey);
            JSONArray messages = getMessagesArray(conversationKey);
            JSONObject msg = new JSONObject();
            long timestamp = System.currentTimeMillis();
            msg.put("sender", sender);
            msg.put("message", message);
            msg.put("timestamp", timestamp);
            messages.put(msg);
            prefs.edit().putString(KEY_MESSAGES + "_" + conversationKey, messages.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> getMessages(String currentUserProfileKey, String otherUserProfileKey) {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            String conversationKey = getConversationKey(currentUserProfileKey, otherUserProfileKey);
            JSONArray messages = getMessagesArray(conversationKey);
            for (int i = 0; i < messages.length(); i++) {
                JSONObject msg = messages.getJSONObject(i);
                Map<String, String> message = new HashMap<>();
                message.put("sender", msg.optString("sender", ""));
                message.put("message", msg.optString("message", ""));
                result.add(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private JSONArray getMessagesArray(String conversationKey) {
        String messagesJson = prefs.getString(KEY_MESSAGES + "_" + conversationKey, "[]");
        try {
            return new JSONArray(messagesJson);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public void wipeAllData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_NOTICES);
        editor.apply();
    }
}
