package com.brins.commom.app;

import android.app.Application;

public interface IApplication {

    void onCreate(Application application);

    void attachBaseContext(Application application);

    void onTerminate();

    void leakWatch(Object obj);
}
