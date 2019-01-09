package com.xinzy.component;

import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ComponentApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }
}