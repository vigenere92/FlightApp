package com.flightapp.myapp;

import android.app.Application;

public class MyAppApplication extends Application {
    private static MyAppApplication instance;

    public MyAppApplication(){
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static MyAppApplication getInstance(){
        return instance;
    }
}
