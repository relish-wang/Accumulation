package cn.studyjams.s1.contest.accumulation.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.base.BaseActivity;
import cn.studyjams.s1.contest.accumulation.entity.Goal;
import cn.studyjams.s1.contest.accumulation.entity.User;
import cn.studyjams.s1.contest.accumulation.ui.fragment.GoalFragment;
import cn.studyjams.s1.contest.accumulation.ui.fragment.StatisticsFragment;
import cn.studyjams.s1.contest.accumulation.util.SPUtil;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    Firebase mDatabase;

    private static final int GOAL = 0x0;
    private static final int STATISTICS = 0x1;
    FloatingActionButton fab;
    Toolbar toolbar;
    FragmentManager mManager;
    List<Fragment> mFragments;
    GoalFragment goalFgm;
    StatisticsFragment statisticsFgm;
    int mCurrentTab = 0;//默认为【目标】页
    private User mUser;

    protected void initViews(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUser = SPUtil.getUser();
        mDatabase = new Firebase("https://fire-weather.firebaseio.com/condition");


        mManager = getSupportFragmentManager();
        goalFgm = new GoalFragment();
        statisticsFgm = new StatisticsFragment();
        mFragments = new ArrayList<>();
        mFragments.add(goalFgm);
        mFragments.add(statisticsFgm);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            @SuppressLint("InflateParams")
            View v = getLayoutInflater().inflate(R.layout.dialog_add_dialog, null);
            EditText etGoalName = (EditText) v.findViewById(R.id.et_goal_name);
            new AlertDialog.Builder(this)
                    .setView(v)
                    .setTitle(R.string.add_goal)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ensure, (DialogInterface dialog, int i) -> {
                        String goalName = etGoalName.getText().toString();
                        addGoal(goalName);
                    }).create().show();
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
        tvEmail.setText(mUser.getEmail());
        ivHead.setOnClickListener(view -> goActivity(MineActivity.class));
        tvName.setOnClickListener(view -> goActivity(MineActivity.class));
        tvEmail.setOnClickListener(view -> goActivity(MineActivity.class));

        openFragment(GOAL);//默认打开【目标页】
    }

    private void addGoal(String goalName) {
        Goal goal = new Goal();
        goal.setName(goalName);
        goal.save();

        mDatabase.child(mUser.getEmail()).child(goal.getGoalId()).setValue(goal);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_goal) {
            openFragment(GOAL);
        } else if (id == R.id.nav_statistics) {
            openFragment(STATISTICS);
        } else if (id == R.id.nav_setting) {
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
