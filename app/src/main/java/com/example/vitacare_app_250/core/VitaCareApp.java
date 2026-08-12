package com.example.vitacare_app_250.core;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.vitacare_app_250.data.FirebaseProvider;

/**
 * Application entry point.
 *
 * <p>Its only job is one-time process setup that must happen before any
 * activity runs: installing the Firebase provider (which needs a Context to
 * read the configured database URL and can only enable disk persistence before
 * the first database access) and opting into the system dark-mode setting.
 */
public class VitaCareApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Follow the device theme rather than pinning the app to light mode.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Must run before anything touches FirebaseDatabase: setPersistenceEnabled
        // throws once a database reference has been created.
        FirebaseProvider.install(this);
    }
}
