package com.qyt.accumulation.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseActivity;
import com.qyt.accumulation.entity.Goal;
import com.qyt.accumulation.ui.fragment.ChartFragment;
import com.qyt.accumulation.util.Temp;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计
 * Created by Relish on 2016/11/10.
 */
public class StatisticsActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @Override
    protected int layoutId() {
        return R.layout.activity_statistics;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {

    }

    private ViewPager vp;
    private ArrayList<Fragment> mFragments;
    private List<Goal> mGoals;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        vp = (ViewPager) findViewById(R.id.vp);
        mFragments = new ArrayList<>();
        mGoals = Goal.findAll();
        for (Goal goal : mGoals) {
            mFragments.add(ChartFragment.getInstance(goal));
        }
        FragmentsAdapter adapter = new FragmentsAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        vp.setOnPageChangeListener(this);
        vp.setOffscreenPageLimit(Temp.getGoals().size());//保留n个界面
        selectPage(0);
    }


    private void selectPage(int index) {
        vp.setCurrentItem(index);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * pageAdapter
     */
    class FragmentsAdapter extends FragmentPagerAdapter {

        public FragmentsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
