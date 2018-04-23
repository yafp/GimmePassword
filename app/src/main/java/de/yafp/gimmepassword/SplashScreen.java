package de.yafp.gimmepassword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Used to handle the pseudo splashscreen on app launch
 */
public class SplashScreen extends Activity {

    private static final String TAG = "Gimme Password";

    private final int _splashTime = 500; // time to display the splash screen in ms - default 1000

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < _splashTime) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Catching exception in SlashScreen");

                } finally {

                    startActivity(new Intent(SplashScreen.this,
                            de.yafp.gimmepassword.GimmePassword.class));
                    finish();
                }
            }
        };
        splashTread.start();
    }
}