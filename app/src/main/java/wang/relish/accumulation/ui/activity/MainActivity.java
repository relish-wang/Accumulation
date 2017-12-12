package wang.relish.accumulation.ui.activity;

import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseActivity;
import wang.relish.accumulation.base.IOnExchangeDataListener;
import wang.relish.accumulation.entity.User;
import wang.relish.accumulation.ui.fragment.GoalFragment;
import wang.relish.accumulation.ui.fragment.UntitledFragment;
import wang.relish.accumulation.util.SPUtil;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

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


    private ImageView ivHead;
    private TextView tvName;
    private TextView tvMobile;

    protected void initViews(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        ivHead = v.findViewById(R.id.ivHead);
        tvName = v.findViewById(R.id.tvName);
        tvMobile = v.findViewById(R.id.tvMobile);
        setupProfile();

        openFragment(GOAL);//默认打开【目标页】
        toolbar.setTitle("目标");
    }

    private void setupProfile() {
        // 2016/11/13 设置个人信息
        mUser = SPUtil.getUser();
        Glide.with(this)
                .load(mUser.getPhoto())
                .centerCrop()
                .placeholder(R.mipmap.icon)
                .crossFade()
                .into(ivHead);
        tvName.setText(mUser.getName());
        tvMobile.setText(mUser.getMobile());

        ivHead.setOnClickListener(this);
        tvName.setOnClickListener(this);
        tvMobile.setOnClickListener(this);
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
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
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
        } else if (id == R.id.nav_untitle) {
            openFragment(UNTITLED);
            toolbar.setTitle("未分类");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivHead:
            case R.id.tvName:
            case R.id.tvMobile:
                Intent intent = new Intent(this, MineActivity.class);
                startActivityForResult(intent, MineActivity.REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MineActivity.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    setupProfile();
                }
                break;
        }
    }
}
