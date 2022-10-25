package com.brins.dreamify.app.splash;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.os.Bundle;
import com.brins.dreamify.R;
import com.brins.dreamify.app.MainActivity;

public class SplashActivity extends BaseSplashActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override protected void gotoMediaActivity() {
        gotoMediaActivity(true);
    }

    @Override protected void gotoMediaActivity(boolean withFinish) {
        realGotoMediaActivity(withFinish);
    }

    private void realGotoMediaActivity(boolean withfinish) {
        Intent intent = new Intent(this, MainActivity
            .class);
        startActivity(intent);
        if (withfinish) {
            finish();
        }
    }

    @Override protected boolean checkInterceptInitialize() {

        return super.checkInterceptInitialize();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState,
        @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
}