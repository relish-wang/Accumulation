package com.qyt.accumulation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseActivity;
import com.qyt.accumulation.base.IOnExchangeDataListener;
import com.qyt.accumulation.entity.User;
import com.qyt.accumulation.ui.fragment.GoalFragment;
import com.qyt.accumulation.ui.fragment.UntitledFragment;
import com.qyt.accumulation.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @Override
    protected boolean removeParent() {
        //不使用BaseActivity的根布局
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {

    }
//    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = mDatabase.getReference("message");
//    Firebase mDatabase;

    private static final int GOAL = 0x0;
    private static final int UNTITLED = 0x1;

    Toolbar toolbar;
    FragmentManager mManager;
    List<Fragment> mFragments;
    GoalFragment goalFgm;
    UntitledFragment untitledFgm;

    int mCurrentTab = 0;//默认为【目标】页
    private User mUser;
    private IOnExchangeDataListener mListener;

    protected void initViews(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUser = SPUtil.getUser();
//        mDatabase = new Firebase("https://fire-weather.firebaseio.com/condition");


        mManager = getSupportFragmentManager();
        goalFgm = new GoalFragment();
        untitledFgm = new UntitledFragment();

        mListener = goalFgm;
        mFragments = new ArrayList<>();
        mFragments.add(goalFgm);
        mFragments.add(untitledFgm);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setClickable(true);
        View v = navigationView.getHeaderView(0);
        ImageView ivHead = (ImageView) v.findViewById(R.id.ivHead);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        TextView tvMobile = (TextView) v.findViewById(R.id.tvMobile);

        // 2016/11/13 设置个人信息
        Glide.with(this)
                .load(mUser.getPhoto())
                .centerCrop()
                .placeholder(R.mipmap.icon)
                .crossFade()
                .into(ivHead);
        tvName.setText(mUser.getName());
        tvMobile.setText(mUser.getMobile());

        ivHead.setOnClickListener(view -> goActivity(MineActivity.class));
        tvName.setOnClickListener(view -> goActivity(MineActivity.class));
        tvMobile.setOnClickListener(view -> goActivity(MineActivity.class));

        openFragment(GOAL);//默认打开【目标页】
        toolbar.setTitle("目标");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("退出【积累】")
                    .setMessage("是否退出App？")
                    .setPositiveButton("退出", ((dialog, which) -> super.onBackPressed()))
                    .setNegativeButton("取消", null)
                    .create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_statistics:
                Intent intent = new Intent(this, StatisticsActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_goal) {
            openFragment(GOAL);
            toolbar.setTitle("目标");
        }else if(id == R.id.nav_untitle){
            openFragment(UNTITLED);
            toolbar.setTitle("未分类");
        }else if (id == R.id.feedback) {
            goActivity(SettingActivity.class);
        } else if (id == R.id.nav_about) {
            goActivity(AboutActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openFragment(int index) {
        for (int i = 0; i < mFragments.size(); i++) {
            if (i == index) {
                Fragment fragment = mFragments.get(i);
                FragmentTransaction transaction = mManager.beginTransaction();
                mFragments.get(mCurrentTab).onPause(); // 暂停当前tab
                if (fragment.isAdded()) {
                    fragment.onResume(); // 启动目标tab的onResume()
                } else {
                    transaction.add(R.id.content_fragment, fragment);
                }
                showTab(i); // 显示目标tab
                transaction.commit();
            }
        }
    }

    private void showTab(int index) {
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);
            FragmentTransaction transaction = mManager.beginTransaction();
            if (index == i) {
                transaction.show(fragment);
            } else {
                transaction.hide(fragment);
            }
            transaction.commit();
        }
        mCurrentTab = index; // 更新目标tab为当前tab
    }
}
