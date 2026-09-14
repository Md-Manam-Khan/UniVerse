package com.universe.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DevContactActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_contact);

        Button facebookButton = findViewById(R.id.facebookButton);
        Button instagramButton = findViewById(R.id.instagramButton);
        Button githubButton = findViewById(R.id.githubButton);

        facebookButton.setOnClickListener(v -> {
            String url = "https://www.facebook.com/share/1DgGA3puAB/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        instagramButton.setOnClickListener(v -> {
            String url = "https://www.instagram.com/manam_the_wildfire?stkn=NDB2YWpsdnQ5MmFq";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        githubButton.setOnClickListener(v -> {
            String url = "https://github.com/Md-Manam-Khan";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}


