package com.universe.app;
import android.content.Intent;
import android.os.Bundle;
public class MainActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}
