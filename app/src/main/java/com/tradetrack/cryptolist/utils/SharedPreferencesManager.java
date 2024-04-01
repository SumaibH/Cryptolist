package com.tradetrack.cryptolist.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String SHARED_PREF_NAME = "Crytolist";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static SharedPreferencesManager instance;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    // Method to save a string value in SharedPreferences
    public void saveString(String key, String value) {
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Method to retrieve a string value from SharedPreferences
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Method to save an integer value in SharedPreferences
    public void saveInt(String key, int value) {
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // Method to retrieve an integer value from SharedPreferences
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    // Method to save a boolean value in SharedPreferences
    public void saveBoolean(String key, boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Method to retrieve a boolean value from SharedPreferences
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // Method to clear all data in SharedPreferences
    public void clearAll() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // Method to remove a specific key from SharedPreferences
    public void removeKey(String key) {
        editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
