package com.madehuge_nishant.videostreamer;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    ImageView IvLogo;
    Typeface Fontface;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

//        Fontface = Typeface.createFromAsset(getAssets(), getString(R.string.font_family));

        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        final Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        final Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        Animation anim3 =AnimationUtils.loadAnimation(this,R.anim.animation_slide_down);

        IvLogo =(ImageView) findViewById(R.id.ivlogo);

        IvLogo.startAnimation(anim3);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
//                mp.reset();
                Intent intent = new Intent(getBaseContext(), Login.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        }, 3000);
    }

}
