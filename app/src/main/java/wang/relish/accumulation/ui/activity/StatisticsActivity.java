package wang.relish.accumulation.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseActivity;
import wang.relish.accumulation.entity.Goal;
import wang.relish.accumulation.ui.fragment.ChartFragment;

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
        mToolbar.setTitle("统计");
    }

    private ViewPager vp;
    private ArrayList<Fragment> mFragments;
    private List<Goal> mGoals;
    private TextView tv_no_data;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        vp = (ViewPager) findViewById(R.id.vp);
        mFragments = new ArrayList<>();
        mGoals = Goal.findAll();
        for (Goal goal : mGoals) {
            mFragments.add(ChartFragment.getInstance(goal));
        }
        FragmentsAdapter adapter = new FragmentsAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        vp.setOnPageChangeListener(this);
        vp.setOffscreenPageLimit(mGoals.size());//保留n个界面
        selectPage(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoals==null||mGoals.size()==0){
            tv_no_data.setVisibility(View.VISIBLE);
            vp.setVisibility(View.GONE);
        }else{
            tv_no_data.setVisibility(View.GONE);
            vp.setVisibility(View.VISIBLE);
        }
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
