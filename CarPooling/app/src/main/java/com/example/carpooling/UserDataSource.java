package com.example.carpooling;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDataSource {

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private User loggedInUser; // Added field to store the logged-in user

    public UserDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NAME, user.getName());
        values.put(DBHelper.COLUMN_EMAIL, user.getEmail());
        values.put(DBHelper.COLUMN_PASSWORD, user.getPassword());

        return database.insert(DBHelper.TABLE_USERS, null, values);
    }
    public User getUserByEmailAndPassword(String email, String password) {
        User user = null;

        Cursor cursor = database.query(
                DBHelper.TABLE_USERS,
                null,
                DBHelper.COLUMN_EMAIL + " = ? AND " + DBHelper.COLUMN_PASSWORD + " = ?",
                new String[]{email, password},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        return user;
    }

    public User getUserByEmail(String email) {
        User user = null;

        Cursor cursor = database.query(
                DBHelper.TABLE_USERS,
                null,
                DBHelper.COLUMN_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        return user;
    }

    // Modified getLoggedInUser to retrieve the logged-in user without parameters
    public User getLoggedInUser() {
        return loggedInUser;
    }

    // New method to set the logged-in user
    public void setLoggedInUser(User user) {
        if (user != null) {
            Log.d("UserData", "User set: " + user.getName());
        } else {
            Log.d("UserData", "User set to null");
        }
        loggedInUser = user;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserId(String.valueOf(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_USER_ID))));
        user.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PASSWORD)));
        return user;
    }
}
