package com.example.jd_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
public class Splash extends AppCompatActivity {
    private static int Splash_screen = 5000;

    Animation topAnim,bottomAnim;
    ImageView image;
    TextView a,b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.splash_music);
        mediaPlayer.start();

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = findViewById(R.id.logo_splash);
        a = findViewById(R.id.top_text);
        b = findViewById(R.id.bottom_text);

        image.setAnimation(topAnim);
        a.setAnimation(bottomAnim);
        b.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this,Login.class);
                startActivity(intent);
                finish();
                mediaPlayer.stop();
            }
        },Splash_screen);

    }
}