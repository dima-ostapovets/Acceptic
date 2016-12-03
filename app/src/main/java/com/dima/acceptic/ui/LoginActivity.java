package com.dima.acceptic.ui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dima.acceptic.R;
import com.dima.acceptic.auth.PlusLogin;
import com.dima.acceptic.repository.AuthRepository;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements PlusLogin.Callback {
    @BindView(R.id.activity_login)
    View rootView;
    private AuthRepository authRepository;
    private PlusLogin plusLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authRepository = new AuthRepository(this);
        if (authRepository.isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        plusLogin = new PlusLogin(getString(R.string.googlePlus_client_id), this, this);
    }

    @OnClick(R.id.btnLogin)
    void onLoginClick(){
        plusLogin.signIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        plusLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(String token) {
        authRepository.setAuthToken(token);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onError(Exception e) {
        Snackbar.make(rootView, e.getMessage(), Snackbar.LENGTH_LONG).show();
    }
}
