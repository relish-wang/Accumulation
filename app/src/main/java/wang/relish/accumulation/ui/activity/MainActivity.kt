package wang.relish.accumulation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import wang.relish.accumulation.R
import wang.relish.accumulation.base.BaseActivity
import wang.relish.accumulation.base.IOnExchangeDataListener
import wang.relish.accumulation.entity.User
import wang.relish.accumulation.ui.fragment.GoalFragment
import wang.relish.accumulation.ui.fragment.UntitledFragment
import wang.relish.accumulation.util.SPUtil
import java.util.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun removeParent(): Boolean {
        //不使用BaseActivity的根布局
        return true
    }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {

    }

    internal var toolbar: Toolbar? = null
    internal var mManager: FragmentManager? = null
    internal var mFragments: MutableList<Fragment>? = null
    internal var goalFgm: GoalFragment? = null
    internal var untitledFgm: UntitledFragment? = null

    internal var mCurrentTab = 0//默认为【目标】页
    private var mUser: User? = null
    private var mListener: IOnExchangeDataListener? = null

    override fun initViews(savedInstanceState: Bundle?) {
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mUser = SPUtil.user

        mManager = supportFragmentManager
        goalFgm = GoalFragment(this)
        untitledFgm = UntitledFragment(this)

        mListener = goalFgm
        mFragments = ArrayList<Fragment>()
        mFragments?.add(goalFgm!!)
        mFragments?.add(untitledFgm!!)


        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.isClickable = true
        val v = navigationView.getHeaderView(0)
        val ivHead = v.findViewById(R.id.ivHead) as ImageView
        val tvName = v.findViewById(R.id.tvName) as TextView
        val tvMobile = v.findViewById(R.id.tvMobile) as TextView

        // 2016/11/13 设置个人信息
        Glide.with(this)
                .load(mUser!!.photo)
                .centerCrop()
                .placeholder(R.mipmap.icon)
                .crossFade()
                .into(ivHead)
        tvName.text = mUser!!.name
        tvMobile.text = mUser!!.mobile

        ivHead.setOnClickListener { goActivity(MineActivity::class.java) }
        tvName.setOnClickListener { goActivity(MineActivity::class.java) }
        tvMobile.setOnClickListener { goActivity(MineActivity::class.java) }

        openFragment(GOAL)//默认打开【目标页】
        toolbar?.title = "目标"
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            AlertDialog.Builder(this)
                    .setTitle("退出【积累】")
                    .setMessage("是否退出App？")
                    .setPositiveButton("退出") { dialog, which -> onBackPressedCompat() }
                    .setNegativeButton("取消", null)
                    .create().show()
        }
    }

    fun onBackPressedCompat() {
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_statistics -> {
                val intent = Intent(this, StatisticsActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_goal) {
            openFragment(GOAL)
            toolbar?.title = "目标"
        } else if (id == R.id.nav_untitle) {
            openFragment(UNTITLED)
            toolbar?.title = "未分类"
        } else if (id == R.id.feedback) {
            goActivity(SettingActivity::class.java)
        } else if (id == R.id.nav_about) {
            goActivity(AboutActivity::class.java)
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun openFragment(index: Int) {
        for (i in mFragments!!.indices) {
            if (i == index) {
                val fragment = mFragments!![i]
                val transaction = mManager?.beginTransaction()
                mFragments!![mCurrentTab].onPause() // 暂停当前tab
                if (fragment.isAdded) {
                    fragment.onResume() // 启动目标tab的onResume()
                } else {
                    transaction?.add(R.id.content_fragment, fragment)
                }
                showTab(i) // 显示目标tab
                transaction?.commit()
            }
        }
    }

    private fun showTab(index: Int) {
        for (i in mFragments!!.indices) {
            val fragment = mFragments!![i]
            val transaction = mManager?.beginTransaction()
            if (index == i) {
                transaction?.show(fragment)
            } else {
                transaction?.hide(fragment)
            }
            transaction?.commit()
        }
        mCurrentTab = index // 更新目标tab为当前tab
    }

    companion object {

        private val TAG = "MainActivity"
        //    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        //    DatabaseReference myRef = mDatabase.getReference("message");
        //    Firebase mDatabase;

        private val GOAL = 0x0
        private val UNTITLED = 0x1
    }
}
