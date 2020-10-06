package com.example.roomquest2019;
/*
Author(s): Jose Banuelos, Jasmine Pena
 */
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

//=====Displays the intro screen when the user starts the app=====
@SuppressWarnings("all")
public class SplashScreen extends AppCompatActivity
{
    private final int SPLASH_DISPLAY_LENGTH = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}