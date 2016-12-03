package cn.studyjams.s1.contest.accumulation.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.base.BaseFragment;
import cn.studyjams.s1.contest.accumulation.entity.Goal;
import cn.studyjams.s1.contest.accumulation.util.Temp;

/**
 * 统计
 * Created by Relish on 2016/11/10.
 */
public class StatisticsFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    @Override
    protected int layoutId() {
        return R.layout.fragment_statistics;
    }

    private ViewPager vp;
    private ArrayList<Fragment> mFragments;
    
    @Override
    protected void initViews(View contentView) {
        vp = (ViewPager) contentView.findViewById(R.id.vp);
        mFragments = new ArrayList<>();
        for (Goal goal : Temp.getGoals()) {
            mFragments.add(ChartFragment.getInstance(goal));
        }
        FragmentsAdapter adapter = new FragmentsAdapter(getActivity().getSupportFragmentManager());
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
