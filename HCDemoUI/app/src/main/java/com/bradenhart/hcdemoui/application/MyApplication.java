package com.bradenhart.hcdemoui.application;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by bradenhart on 17/12/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // [Optional] Power your app with Local Datastore. For more info, go to
        // https://parse.com/docs/android/guide#local-datastore
        // Parse.enableLocalDatastore(this);

        Parse.initialize(this);

    }
}
