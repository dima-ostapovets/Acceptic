package com.dima.acceptic.repository;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dima on 03.12.16.
 */
public class AuthRepository implements IAuthRepository {

    public static final String PREFS = AuthRepository.class.getSimpleName();
    public static final String TOKEN = "token";
    private final SharedPreferences preferences;
    Context context;

    public AuthRepository(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public void setAuthToken(String token) {
        preferences.edit().putString(TOKEN, token).apply();
    }

    @Override
    public String getAuthToken() {
        return preferences.getString(TOKEN, null);
    }

    @Override
    public boolean isLoggedIn() {
        return preferences.getString(TOKEN, null) != null;
    }

    @Override
    public void logout() {
        setAuthToken(null);
    }
}
