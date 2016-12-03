package com.dima.acceptic.repository;

/**
 * Created by dima on 03.12.16.
 */

public interface IAuthRepository {
    void setAuthToken(String token);
    String getAuthToken();

    void setName(String name);

    String getName();

    boolean isLoggedIn();

    void logout();
}
