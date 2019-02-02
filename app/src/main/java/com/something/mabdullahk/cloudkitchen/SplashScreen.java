package com.something.mabdullahk.cloudkitchen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 1500) {
                        sleep(100);
                        waited += 100;
                    }

                    preferences = getSharedPreferences("Cloud Kitchen", MODE_PRIVATE);

//                    String pass = decrypt(passEncrypted);
                    if (preferences.contains(encrypt("token"))){

                        String token = decrypt(preferences.getString(encrypt("token"), null));
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        intent.putExtra("token",token);
                        startActivity(intent);
                        SplashScreen.this.finish();

                    } else {


                        Intent intent = new Intent(SplashScreen.this,
                                Startactivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        SplashScreen.this.finish();
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }

            }
        };
        splashTread.start();
    }

    public static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }


}
