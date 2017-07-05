package com.qyt.accumulation.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView

import com.qyt.accumulation.R
import com.qyt.accumulation.base.BaseActivity
import com.qyt.accumulation.entity.Goal
import com.qyt.accumulation.ui.fragment.ChartFragment

import java.util.ArrayList

/**
 * 统计
 * Created by Relish on 2016/11/10.
 */
class StatisticsActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    override fun layoutId(): Int {
        return R.layout.activity_statistics
    }

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        mToolbar?.title = "统计"
    }

    private var vp: ViewPager? = null
    private var mFragments: ArrayList<Fragment>? = null
    private var mGoals: List<Goal>? = null
    private var tv_no_data: TextView? = null

    override fun initViews(savedInstanceState: Bundle?) {
        tv_no_data = findViewById(R.id.tv_no_data) as TextView
        vp = findViewById(R.id.vp) as ViewPager
        mFragments = ArrayList<Fragment>()
        mGoals = Goal.findAll()
        for (goal in mGoals!!) {
            mFragments!!.add(ChartFragment.getInstance(goal))
        }
        val adapter = FragmentsAdapter(supportFragmentManager)
        vp!!.adapter = adapter
        vp!!.setOnPageChangeListener(this)
        vp!!.offscreenPageLimit = mGoals!!.size//保留n个界面
        selectPage(0)
    }

    override fun onResume() {
        super.onResume()
        if (mGoals == null || mGoals!!.size == 0) {
            tv_no_data!!.visibility = View.VISIBLE
            vp!!.visibility = View.GONE
        } else {
            tv_no_data!!.visibility = View.GONE
            vp!!.visibility = View.VISIBLE
        }
    }

    private fun selectPage(index: Int) {
        vp!!.currentItem = index
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        selectPage(position)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    /**
     * pageAdapter
     */
    internal inner class FragmentsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): android.support.v4.app.Fragment {
            return mFragments!![position]
        }

        override fun getCount(): Int {
            return mFragments!!.size
        }
    }
}
