package com.campus_alert_v1;

        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.widget.ProgressBar;

public class SplashActivity extends Activity {
    private ProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);

        new Thread(new Runnable() {
            public void run() {
                doWork();
                startApp();
                finish();
            }
        }).start();
    }

    private void doWork() {
        for (int progress=0; progress<=100; progress+=15) {
            try {
                Thread.sleep(250);
                mProgress.setProgress(progress);
            } catch (Exception e) {
            }
        }
    }

    private void startApp() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}