package com.example.redhelm321;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.redhelm321.authentication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500;
    ImageView splash_logo;
    ImageView glow_background;
    TextView splash_text;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash_logo = findViewById(R.id.splash_logo);
        glow_background = findViewById(R.id.glow_background);
        splash_text = findViewById(R.id.splash_text);
        mAuth = FirebaseAuth.getInstance();

        // Ensure views are visible
        splash_logo.setVisibility(View.VISIBLE);
        glow_background.setVisibility(View.VISIBLE);
        splash_text.setVisibility(View.VISIBLE);

        startSplashAnimations();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HandleUserAuthentication();
                finish();
            }
        }, SPLASH_DELAY);
    }

    private void startSplashAnimations() {
        // Initial setup
        float screenHeight = getWindow().getDecorView().getHeight();
        splash_logo.setTranslationY(-screenHeight);
        splash_logo.setScaleX(0.3f);
        splash_logo.setScaleY(0.3f);
        glow_background.setScaleX(0.3f);
        glow_background.setScaleY(0.3f);
        glow_background.setAlpha(0f);
        
        // Drop animation with impact
        ObjectAnimator dropIn = ObjectAnimator.ofFloat(splash_logo, "translationY", -screenHeight, 50f);
        dropIn.setInterpolator(new AccelerateInterpolator(1.2f));
        dropIn.setDuration(700);

        // Bounce back up slightly
        ObjectAnimator bounceBack = ObjectAnimator.ofFloat(splash_logo, "translationY", 50f, 0f);
        bounceBack.setInterpolator(new DecelerateInterpolator());
        bounceBack.setDuration(300);
        bounceBack.setStartDelay(700);

        // Scale up with impact
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.3f, 1.2f, 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.3f, 1.2f, 1.0f);
        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(splash_logo, scaleX, scaleY);
        scaleUp.setInterpolator(new OvershootInterpolator(0.8f));
        scaleUp.setDuration(600);
        scaleUp.setStartDelay(700);

        // Glow animation
        PropertyValuesHolder glowScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.3f, 1.4f, 1.1f);
        PropertyValuesHolder glowScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.3f, 1.4f, 1.1f);
        ObjectAnimator glowScale = ObjectAnimator.ofPropertyValuesHolder(glow_background, glowScaleX, glowScaleY);
        glowScale.setInterpolator(new DecelerateInterpolator());
        glowScale.setDuration(600);
        glowScale.setStartDelay(700);

        // Glow fade in and pulse
        ObjectAnimator glowFadeIn = ObjectAnimator.ofFloat(glow_background, "alpha", 0f, 0.9f);
        glowFadeIn.setDuration(400);
        glowFadeIn.setStartDelay(700);

        ObjectAnimator glowPulse = ObjectAnimator.ofFloat(glow_background, "alpha", 0.9f, 0.4f);
        glowPulse.setDuration(600);
        glowPulse.setRepeatCount(ObjectAnimator.INFINITE);
        glowPulse.setRepeatMode(ObjectAnimator.REVERSE);
        glowPulse.setStartDelay(1100);

        // Impact shockwave (scale glow quickly)
        ObjectAnimator shockwave = ObjectAnimator.ofFloat(glow_background, "scaleX", 1.1f, 1.3f, 1.1f);
        ObjectAnimator shockwaveY = ObjectAnimator.ofFloat(glow_background, "scaleY", 1.1f, 1.3f, 1.1f);
        shockwave.setDuration(300);
        shockwaveY.setDuration(300);
        shockwave.setStartDelay(700);
        shockwaveY.setStartDelay(700);

        // Text animation
        splash_text.setAlpha(0f);
        splash_text.setTranslationY(50f);
        
        ObjectAnimator textFadeIn = ObjectAnimator.ofFloat(splash_text, "alpha", 0f, 1f);
        ObjectAnimator textSlideUp = ObjectAnimator.ofFloat(splash_text, "translationY", 50f, 0f);
        textFadeIn.setDuration(400);
        textSlideUp.setDuration(400);
        textFadeIn.setStartDelay(1000);
        textSlideUp.setStartDelay(1000);
        textFadeIn.setInterpolator(new DecelerateInterpolator());
        textSlideUp.setInterpolator(new DecelerateInterpolator());

        // Combine all animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
            dropIn, bounceBack,
            scaleUp,
            glowScale, glowFadeIn, glowPulse,
            shockwave, shockwaveY,
            textFadeIn, textSlideUp
        );
        animatorSet.start();
    }

    private void HandleUserAuthentication() {
        if(mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
            openLoginPage();
        }
        else {
            openMainScreen();
        }
    }

    private void openMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
