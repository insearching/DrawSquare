package com.example.serhiihrabas.drawsquare;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by serhii.hrabas on 5/19/2017.
 */

public class DrawApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
