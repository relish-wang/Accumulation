package com.qyt.accumulation.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseActivity;
import com.qyt.accumulation.base.IOnExchangeDataListener;
import com.qyt.accumulation.entity.Goal;
import com.qyt.accumulation.entity.User;
import com.qyt.accumulation.ui.fragment.GoalFragment;
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
    FloatingActionButton fab;
    Toolbar toolbar;
    FragmentManager mManager;
    List<Fragment> mFragments;
    GoalFragment goalFgm;
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
        mListener = goalFgm;
        mFragments = new ArrayList<>();
        mFragments.add(goalFgm);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            @SuppressLint("InflateParams")
            View v = getLayoutInflater().inflate(R.layout.dialog_add_dialog, null);
            EditText etGoalName = (EditText) v.findViewById(R.id.et_goal_name);
            etGoalName.setHint(R.string.goal_name);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setView(v)
                    .setTitle(R.string.add_goal)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ensure, (DialogInterface dialogInterface, int i) -> {
                        String goalName = etGoalName.getText().toString();
                        addGoal(goalName);
                    }).create();
            WindowManager m = getWindowManager();
            Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
            android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
            p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3
            p.width = (int) (d.getWidth() * 0.5);    //宽度设置为屏幕的0.5
            dialog.getWindow().setAttributes(p);     //设置生效
            dialog.show();
        });

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
        TextView tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        //// TODO: 2016/11/13 设置个人信息
        ivHead.setImageResource(R.mipmap.icon);

        tvName.setText(mUser.getName());
        ivHead.setOnClickListener(view -> goActivity(MineActivity.class));
        tvName.setOnClickListener(view -> goActivity(MineActivity.class));
        tvEmail.setOnClickListener(view -> goActivity(MineActivity.class));

        openFragment(GOAL);//默认打开【目标页】
    }

    /**
     * 点击fab后添加目标
     *
     * @param goalName 目标名
     */
    private void addGoal(String goalName) {
        Goal goal = new Goal();
        goal.setId(Goal.getMaxId() + 1);
        goal.setName(goalName);
        goal.setTime(System.currentTimeMillis());
        goal.save();
        mListener.onSendMessage(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        } else if (id == R.id.feedback) {
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
