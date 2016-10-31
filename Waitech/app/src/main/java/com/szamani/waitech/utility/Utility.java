package com.szamani.waitech.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Szamani on 9/15/2016.
 */


public class Utility {
    private static final String SHARED_PREFERENCES = "SharedPreferences";
    private static SharedPreferences sPreferences;
    private static final String COOKIE_KEY = "SessionID";
    private static final String LOGIN_KEY = "LoginKey"; // not exactly the thing, just what user expect to be
    private static final String EMAIL_KEY = "EmailKey";
    private static final String PASSWORD_KEY = "PasswordKey";

    private static void setPreferences(Context context) {
        if (sPreferences == null)
            sPreferences = context.getSharedPreferences(SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);
    }

    public static void setEmail(Context context, String email) {
        setPreferences(context);

        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(EMAIL_KEY, email);
        editor.commit();
    }

    public static void setPassword(Context context, String password) {
        setPreferences(context);

        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(PASSWORD_KEY, password);
        editor.commit();
    }

    public static String getEmail(Context context) {
        setPreferences(context);

        return sPreferences.getString(EMAIL_KEY, null);
    }

    public static String getPassword(Context context) {
        setPreferences(context);

        return sPreferences.getString(PASSWORD_KEY, null);
    }

    public static boolean isLoggedIn(Context context) {
        setPreferences(context);

        return sPreferences.getBoolean(LOGIN_KEY, false);
    }

    public static void setLogin(Context context, boolean login) {
        setPreferences(context);

        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putBoolean(LOGIN_KEY, login);
        editor.commit();
    }

    public static void writeToCookie(Context context, String value) {
        setPreferences(context);

        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(COOKIE_KEY, value);
        editor.commit();
    }

    public static String readFromCookie(Context context) {
        setPreferences(context);

        return sPreferences.getString(COOKIE_KEY, null);
    }
}
