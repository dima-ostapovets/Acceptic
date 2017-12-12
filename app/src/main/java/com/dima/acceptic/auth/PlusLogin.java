package com.dima.acceptic.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

/**
 * Created by dima on 03.12.16.
 */

public class PlusLogin implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_CODE = 201;
    private String key;
    private FragmentActivity activity;
    private Callback callback;
    private GoogleSignInOptions mGso;
    private GoogleApiClient apiClient;
    private PendingRequest pendingRequest;

    public PlusLogin(String key, FragmentActivity activity, Callback callback) {
        this.key = key;
        this.activity = activity;
        this.callback = callback;
        initClient();
    }

    private void initClient() {
        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestServerAuthCode(key)
                .build();
        apiClient = new GoogleApiClient.Builder(activity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .enableAutoManage(activity, this)
                .addConnectionCallbacks(this)
                .build();
    }

    public void signIn() {
        pendingRequest = PendingRequest.SIGN_IN;
        executeRequestIfConnected();
    }

    public void signOut() {
        pendingRequest = PendingRequest.SIGN_OUT;
        executeRequestIfConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        executePendingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        callback.onError(new RuntimeException(connectionResult.getErrorMessage()));
    }

    private void executeRequestIfConnected() {
        if (apiClient.isConnected()) {
            executePendingRequest();
        }
    }

    private void executePendingRequest() {
        if (pendingRequest == PendingRequest.SIGN_OUT) {
            executeSignOutRequest();
        } else if (pendingRequest == PendingRequest.SIGN_IN) {
            executeSignInRequest();
        }
        pendingRequest = PendingRequest.NONE;
    }

    private void executeSignInRequest() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        if (activity != null) {
            activity.startActivityForResult(signInIntent, REQUEST_CODE);
        } else {
            callback.onError(new RuntimeException("Activity is null"));
        }
    }

    private void executeSignOutRequest() {
        Auth.GoogleSignInApi.revokeAccess(apiClient);
        Auth.GoogleSignInApi.signOut(apiClient);
    }

    public void onActivityResult(int request, int result, Intent data) {
        if (request == REQUEST_CODE) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result != null && result.isSuccess()) {
            GoogleSignInAccount signInAccount = result.getSignInAccount();
            if (signInAccount == null) {
                callback.onError(new RuntimeException("account == null"));
            } else {
                callback.onSuccess(signInAccount);
            }

        } else {
            callback.onError(new RuntimeException("Login canceled"));
        }

    }


    private enum PendingRequest {
        SIGN_IN,
        SIGN_OUT,
        NONE
    }

    public interface Callback {
        void onSuccess(GoogleSignInAccount account);

        void onError(Exception e);
    }
}
