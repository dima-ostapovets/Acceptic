package com.dima.acceptic.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dima.acceptic.R;
import com.dima.acceptic.repository.AuthRepository;
import com.dima.acceptic.repository.IAuthRepository;
import com.dima.acceptic.service.RandomService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ServiceConnection, RandomService.ValueListener {
    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.tvResult)
    TextView tvResult;
    @BindView(R.id.toolBar)
    Toolbar toolbar;
    private RandomService service;
    private IAuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        authRepository = new AuthRepository(this);
        setupUi();
    }

    private void setupUi() {
        tvResult.setVisibility(View.GONE);
        tabLayout.setupWithViewPager(viewPager);
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        titles.add(getString(R.string.page_name, "1"));
        titles.add(getString(R.string.page_name, "2"));
        fragments.add(DummyFragment.newInstance(titles.get(0)));
        fragments.add(DummyFragment.newInstance(titles.get(1)));
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), fragments, titles));
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        unbind();
        stopService(new Intent(this, RandomService.class));
        authRepository.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, RandomService.class);
        startService(intent);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        unbind();
        super.onStop();
    }

    private void unbind(){
        if (service != null) {
            service.setValueListener(null);
            unbindService(this);
        }
        service = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        RandomService.LocalBinder binder = (RandomService.LocalBinder) service;
        this.service = binder.getService();
        this.service.setValueListener(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    @Override
    public void onValueReady(Integer integer) {
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText(getString(R.string.result, integer));
    }
}
