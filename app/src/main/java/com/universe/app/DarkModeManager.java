package com.universe.app;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
public class DarkModeManager 
{
    private static final String PREFS_NAME = "DarkModePrefs";
    private static final String KEY_IS_DARK_MODE = "isDarkMode";
    private final SharedPreferences prefs;
    public DarkModeManager(Context context) 
    {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public boolean isDarkMode() 
    {
        return prefs.getBoolean(KEY_IS_DARK_MODE, false);
    }
    public void setDarkMode(boolean isDarkMode) 
    {
        prefs.edit().putBoolean(KEY_IS_DARK_MODE, isDarkMode).apply();
        applyToApp(isDarkMode);
    }
    public void toggleDarkMode() 
    {
        setDarkMode(!isDarkMode());
    }
    public void applySavedMode() 
    {
        applyToApp(isDarkMode());
    }
    private void applyToApp(boolean isDarkMode) 
    {
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}