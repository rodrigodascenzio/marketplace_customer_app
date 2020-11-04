package com.nuppin.User.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.nuppin.User.activity.TelaListaTodasLojas;
import com.nuppin.Util.AppConstants;
import com.nuppin.Util.UtilShaPre;
import com.nuppin.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it;
                if (UtilShaPre.getDefaultsBool("user_logged", SplashScreen.this)) {
                    it = new Intent(SplashScreen.this, TelaListaTodasLojas.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);
                }else{
                    it = new Intent(SplashScreen.this, TelaIntro.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);
                }
            }
        }, 2000);
    }
}