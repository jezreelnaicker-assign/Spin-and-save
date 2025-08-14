package com.example.spinthewheel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoIcon;
    private TextView mainTitle, subTitle, tagline;
    private View floatingCircle1, floatingCircle2, floatingCircle3;
    private View bottomCircle1, bottomCircle2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        initializeViews();
        
        // Start animations
        startAnimations();
        
        // Navigate to LoginActivity after 4 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 4000);
    }

    private void initializeViews() {
        logoIcon = findViewById(R.id.logoIcon);
        mainTitle = findViewById(R.id.mainTitle);
        subTitle = findViewById(R.id.subTitle);
        tagline = findViewById(R.id.tagline);
        floatingCircle1 = findViewById(R.id.floatingCircle1);
        floatingCircle2 = findViewById(R.id.floatingCircle2);
        floatingCircle3 = findViewById(R.id.floatingCircle3);
        bottomCircle1 = findViewById(R.id.bottomCircle1);
        bottomCircle2 = findViewById(R.id.bottomCircle2);
    }

    private void startAnimations() {
        // Logo bounce animation
        Animation logoBounce = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        logoBounce.setDuration(1000);
        logoIcon.startAnimation(logoBounce);

        // Title slide in from top
        Animation titleSlideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        titleSlideIn.setDuration(800);
        titleSlideIn.setStartOffset(500);
        mainTitle.startAnimation(titleSlideIn);

        // Subtitle slide in from bottom
        Animation subtitleSlideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        subtitleSlideIn.setDuration(800);
        subtitleSlideIn.setStartOffset(700);
        subTitle.startAnimation(subtitleSlideIn);

        // Tagline fade in
        Animation taglineFadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        taglineFadeIn.setDuration(1000);
        taglineFadeIn.setStartOffset(1000);
        tagline.startAnimation(taglineFadeIn);

        // Floating circles animations
        startFloatingAnimations();
    }

    private void startFloatingAnimations() {
        // Floating circle 1 - gentle floating
        Animation float1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        float1.setDuration(3000);
        float1.setRepeatCount(Animation.INFINITE);
        float1.setRepeatMode(Animation.REVERSE);
        floatingCircle1.startAnimation(float1);

        // Floating circle 2 - slower floating
        Animation float2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        float2.setDuration(4000);
        float2.setStartOffset(1000);
        float2.setRepeatCount(Animation.INFINITE);
        float2.setRepeatMode(Animation.REVERSE);
        floatingCircle2.startAnimation(float2);

        // Floating circle 3 - medium floating
        Animation float3 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        float3.setDuration(3500);
        float3.setStartOffset(500);
        float3.setRepeatCount(Animation.INFINITE);
        float3.setRepeatMode(Animation.REVERSE);
        floatingCircle3.startAnimation(float3);

        // Bottom circles - subtle animations
        Animation bottom1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        bottom1.setDuration(2500);
        bottom1.setRepeatCount(Animation.INFINITE);
        bottom1.setRepeatMode(Animation.REVERSE);
        bottomCircle1.startAnimation(bottom1);

        Animation bottom2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        bottom2.setDuration(3000);
        bottom2.setStartOffset(1500);
        bottom2.setRepeatCount(Animation.INFINITE);
        bottom2.setRepeatMode(Animation.REVERSE);
        bottomCircle2.startAnimation(bottom2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Clear animations when activity is paused
        if (logoIcon != null) logoIcon.clearAnimation();
        if (mainTitle != null) mainTitle.clearAnimation();
        if (subTitle != null) subTitle.clearAnimation();
        if (tagline != null) tagline.clearAnimation();
        if (floatingCircle1 != null) floatingCircle1.clearAnimation();
        if (floatingCircle2 != null) floatingCircle2.clearAnimation();
        if (floatingCircle3 != null) floatingCircle3.clearAnimation();
        if (bottomCircle1 != null) bottomCircle1.clearAnimation();
        if (bottomCircle2 != null) bottomCircle2.clearAnimation();
    }
} 