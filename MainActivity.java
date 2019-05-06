package itp341.raihan.mahira.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

// Splash Screen - Using https://www.geeksforgeeks.org/android-creating-a-splash-screen/
public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_SCREEN_TIME);

    }




}
