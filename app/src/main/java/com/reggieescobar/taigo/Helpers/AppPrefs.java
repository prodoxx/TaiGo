package com.reggieescobar.taigo.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *  **Note**
 *  You must call the function "initialize" in the main activity and pass the context. You can then set, get, or delete preferences
 *  from anywhere in the application. :)
 */
public class AppPrefs {
    //private Context context;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    private static AppPrefs instance;

    public static AppPrefs getInstance() {
        if(instance != null){
            return instance;
        }else{
            instance = new AppPrefs();
            return instance;
        }
    }

    private AppPrefs() {}

    /**
     * Must be called once from an activity before setting, getting or deleting any prefs
     * @param context - pass in the application context
     */
    public void initialize(Context context){
        //this.context = context;
        prefs = context.getSharedPreferences(Config.APP_PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Call this function to set a boolean pref value
     * @param key - this is the name of what you are storing
     * @param value - this is the actual value you are storing
     */
    public void setBooleanPrefValue(String key, boolean value){
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Call this function to get a boolean pref value that is stored
     * @param key - this is the name of the pref you want to get
     * @param defaultValue - i thing this is a default value you will get if anything goes wrong or the pref doesn't exist
     * @return - it will return the value you requested
     */
    public boolean getBooleanPrefValue(String key, boolean defaultValue){
        return prefs.getBoolean(key, defaultValue);
    }

    /**
     * Call to get an Integer Pref value....bla...bla...lazy to type comments....the same goes for the rest.
     * @param key - same thing here
     * @param value - and here to
     */
    public void setIntegerPrefValue(String key, int value){
        editor.putInt(key, value);
        editor.apply();
    }

    public int getIntegerPrefValue(String key, int defaultValue){
        return prefs.getInt(key, defaultValue);
    }

    public void setFloatPrefValue(String key, float value){
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloatPrefValue(String key, float defaultValue){
        return prefs.getFloat(key, defaultValue);
    }

    public void setLongPrefValue(String key, long value){
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLongPrefValue(String key, long defaultValue){
        return prefs.getLong(key, defaultValue);
    }

    public void setStringPrefValue(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringPrefValue(String key, String defaultValue){
        return prefs.getString(key, defaultValue);
    }

    /**
     * Call this to remove a pref value
     * @param key - pass in the key of the value you want to remove
     */
    public void removePrefValue(String key){
        editor.remove(key);
        editor.apply();
    }

    /**
     * Call this to clear everything from pref
     */
    public void clearAllPrefValues(){
        editor.clear();
        editor.apply();
    }

    public boolean isFirstTimeRunning(){

        return prefs.getBoolean("is_first_time_running", true);
        //return  true;
    }

    public void setFirstTimeRunning(boolean isFirstTime){
        editor.putBoolean("is_first_time_running", isFirstTime);
        editor.apply();
    }
}
