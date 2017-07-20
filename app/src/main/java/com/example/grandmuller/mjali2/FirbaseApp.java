package com.example.grandmuller.mjali2;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by oroni on 7/20/17.
 */

public class FirbaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
