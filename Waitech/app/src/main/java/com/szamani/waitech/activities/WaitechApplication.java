package com.szamani.waitech.activities;
import android.app.Application;
import android.util.Log;

/**
 * Created by Szamani on 9/15/2016.
 */
public class WaitechApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Application ", "Called");
    }
}
