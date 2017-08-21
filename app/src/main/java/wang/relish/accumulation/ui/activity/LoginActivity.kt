package wang.relish.accumulation.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import wang.relish.accumulation.App
import wang.relish.accumulation.R
import wang.relish.accumulation.base.BaseActivity
import wang.relish.accumulation.entity.User
import wang.relish.accumulation.util.PhoneUtils
import wang.relish.accumulation.util.SPUtil

/**
 * 登录页
 * Created by Relish on 2016/11/4.
 */
class LoginActivity : BaseActivity(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_login
    }

    override var isBtnBackEnable: Boolean = false
        get() = false

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        mToolbar?.setTitle(R.string.login)
    }

    internal var etMobile: AutoCompleteTextView? = null
    internal var etPwd: EditText? = null
    internal var btnLogin: Button? = null

    internal var mUser: User? = null

    private var isLogout = false


    internal var tv_forget_pwd: TextView? = null
    internal var tv_register: TextView? = null

    override fun initViews(savedInstanceState: Bundle?) {
        val intent = intent
        if (intent != null) {
            isLogout = intent.getBooleanExtra("logout", false)
            mUser = SPUtil.user
        }

        etMobile = findViewById(R.id.etEmail) as AutoCompleteTextView
        etPwd = findViewById(R.id.etPwd) as EditText
        tv_forget_pwd = findViewById(R.id.tv_forget_pwd) as TextView
        tv_register = findViewById(R.id.tv_register) as TextView

        tv_forget_pwd?.setOnClickListener(this)
        tv_register?.setOnClickListener(this)

        val user = SPUtil.user
        if (user != null && !user!!.isEmpty) {
            App.USER = user

            etMobile?.setText(App.USER!!.mobile)
            etPwd?.setText(App.USER!!.password)
        }

        btnLogin = findViewById(R.id.btnLogin) as Button
        btnLogin?.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        if (isLogout) {
            isLogout = false
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnLogin -> login(v)
            R.id.tv_forget_pwd -> {
                val mobile = etMobile?.text.toString().trim { it <= ' ' }
                ForgetPwdActivity.open(this, mobile)
            }
            R.id.tv_register -> {
                val intent1 = Intent(this, RegisterActivity::class.java)
                startActivityForResult(intent1, REGISTER)
            }
        }
    }

    private fun login(v: View) {
        val mobile = etMobile?.text.toString().trim { it <= ' ' }
        val pwd = etPwd?.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(mobile)) {
            Snackbar.make(v, R.string.mobile_not_be_null, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (!mobile.matches(PhoneUtils.MOBILE_PATTERN.toRegex())) {
            Snackbar.make(v, R.string.mobile_format_error, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(pwd)) {
            Snackbar.make(v, R.string.pwd_not_be_null, Snackbar.LENGTH_SHORT).show()
            return
        }
        showLoading(R.string.logining)
        login(mobile, pwd)
    }

    private fun login(mobile: String, pwd: String) {
//        EventBus.getDefault().post(LoginEvent(mobile, pwd))

        object : AsyncTask<String, Void, User>() {

            override fun doInBackground(vararg params: String): User? {
                return User.login(params[0], params[1])
            }

            override fun onPostExecute(user: User?) {
                super.onPostExecute(user)
                if (user == null) {
                    showMessage(R.string.account_or_password_is_not_collect)
                    showLoading(false)
                } else {
                    SPUtil.saveUser(user)
                    App.USER = user
                    SPUtil.putBoolean("autoLogin", true)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }.execute(mobile, pwd)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REGISTER && resultCode == Activity.RESULT_OK) {
            etMobile?.setText(App.USER?.mobile)
            etPwd?.setText("")
        }
    }

    companion object {

        private val REGISTER = 0x101
    }
}
