package com.universe.app;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
public class SplashActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView universityLogo = findViewById(R.id.universityLogo);
        universityLogo.setOnClickListener(v -> {
            String url = "https://baustkhulna.ac.bd/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
        Button enterButton = findViewById(R.id.enterButton);
        enterButton.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
            startActivity(intent);
        });
    }
}