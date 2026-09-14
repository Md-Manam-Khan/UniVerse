package com.universe.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public abstract class BaseActivity extends AppCompatActivity {

    protected DarkModeManager darkModeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        darkModeManager = new DarkModeManager(this);
        darkModeManager.applySavedMode();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        applyBlackBackgroundIfDark();
        applyKeyboardResize();
    }

    private void applyKeyboardResize() {
        ViewGroup content = findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) {
            return;
        }

        View root = content.getChildAt(0);
        int initialPaddingBottom = root.getPaddingBottom();
        int initialPaddingLeft = root.getPaddingLeft();
        int initialPaddingTop = root.getPaddingTop();
        int initialPaddingRight = root.getPaddingRight();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int navHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            int extraBottom = Math.max(imeHeight - navHeight, 0);
            v.setPadding(initialPaddingLeft, initialPaddingTop, initialPaddingRight, initialPaddingBottom + extraBottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    private void applyBlackBackgroundIfDark() {
        if (!darkModeManager.isDarkMode()) {
            return;
        }

        ViewGroup content = findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) {
            return;
        }

        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        View root = content.getChildAt(0);
        root.setBackgroundColor(Color.BLACK);

        if (root instanceof ViewGroup) {
            ViewGroup rootGroup = (ViewGroup) root;
            for (int i = 0; i < rootGroup.getChildCount(); i++) {
                View child = rootGroup.getChildAt(i);
                boolean isPlainView = child.getClass() == View.class;
                ViewGroup.LayoutParams lp = child.getLayoutParams();
                boolean isFullScreen = lp != null
                        && lp.width == ViewGroup.LayoutParams.MATCH_PARENT
                        && lp.height == ViewGroup.LayoutParams.MATCH_PARENT;
                if (isPlainView && isFullScreen) {
                    child.setBackgroundColor(Color.BLACK);
                }
            }
        }
    }
}
