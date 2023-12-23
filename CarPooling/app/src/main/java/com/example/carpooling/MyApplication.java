package com.example.carpooling;

import android.app.Application;

public class MyApplication extends Application {
    private UserDataSource userDataSource;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize your UserDataSource here
        userDataSource = new UserDataSource(this);
        userDataSource.open();  // Open the database connection
    }

    public UserDataSource getUserDataSource() {
        return userDataSource;
    }
}