package com.qyt.accumulation.ui.activity

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

import com.qyt.accumulation.R
import com.qyt.accumulation.base.BaseActivity
import com.qyt.accumulation.entity.User
import com.qyt.accumulation.util.PhoneUtils
import com.qyt.accumulation.util.SPUtil

import java.util.Random

/**
 * 忘记密码
 * Created by Relish on 2016/12/4.
 */
class ForgetPwdActivity : BaseActivity(), View.OnClickListener {
    override fun layoutId(): Int {
        return R.layout.activity_forget_pwd
    }

    var currentPage = 0

    private var mMobile: String? = null

    override fun parseIntent(intent: Intent) {
        super.parseIntent(intent)
        mMobile = intent.getStringExtra("mobile")

    }

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        mToolbar?.title = "忘记密码"
    }

    private var code = ""

    private var iv_mobile_or_pwd: ImageView? = null
    private var et_mobile_or_pwd: EditText? = null
    private var et_verify_code_or_repeat_pwd: EditText? = null
    private var btn_get_verify_code: Button? = null
    private var btn_next_or_commit: Button? = null

    override fun initViews(savedInstanceState: Bundle?) {
        iv_mobile_or_pwd = findViewById(R.id.iv_mobile_or_pwd) as ImageView
        et_mobile_or_pwd = findViewById(R.id.et_mobile_or_pwd) as EditText
        et_verify_code_or_repeat_pwd = findViewById(R.id.et_verify_code_or_repeat_pwd) as EditText
        btn_get_verify_code = findViewById(R.id.btn_get_verify_code) as Button
        btn_next_or_commit = findViewById(R.id.btn_next_or_commit) as Button

        if (!TextUtils.isEmpty(mMobile)) {
            et_mobile_or_pwd!!.setText(mMobile)
        }

        btn_get_verify_code!!.setOnClickListener(this)
        btn_next_or_commit!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_get_verify_code -> if (!TextUtils.isEmpty(et_mobile_or_pwd!!.text.toString().trim { it <= ' ' })) {
                if (et_mobile_or_pwd!!.text.toString().trim { it <= ' ' }.length == 11) {
                    mMobile = et_mobile_or_pwd!!.text.toString().trim { it <= ' ' }
                    if (mMobile!!.matches(PhoneUtils.MOBILE_PATTERN.toRegex())) {
                        code = generateCode()
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 2)
                        } else {
                            sendSms()
                        }
                    } else {
                        Toast.makeText(this, R.string.phone_number_error, Toast.LENGTH_LONG).show()
                        et_mobile_or_pwd!!.requestFocus()
                    }
                } else {
                    Toast.makeText(this, R.string.phone_number_error, Toast.LENGTH_LONG).show()
                    et_mobile_or_pwd!!.requestFocus()
                }
            } else {
                Toast.makeText(this, R.string.phone_number_empty, Toast.LENGTH_LONG).show()
                et_mobile_or_pwd!!.requestFocus()
            }
            R.id.btn_next_or_commit -> if (currentPage == 0) {
                val code = et_verify_code_or_repeat_pwd!!.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(code)) {
                    showMessage("验证码不得为空！")
                    return
                }
                if (!code.matches("^[0-9]{4}$".toRegex())) {
                    showMessage("验证码格式不正确！")
                    return
                }
                if (!TextUtils.equals(this.code, code)) {
                    showMessage("验证码不正确！")
                    return
                }
                turnPage()
            } else {
                val pwd = et_mobile_or_pwd!!.text.toString().trim { it <= ' ' }
                val repeat_pwd = et_verify_code_or_repeat_pwd!!.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(pwd)) {
                    showMessage("密码不得为空")
                    return
                }
                if (!TextUtils.equals(pwd, repeat_pwd)) {
                    showMessage("两次密码输入不一致")
                    return
                }
                resetPwdSuccess(pwd)
            }
        }
    }

    private fun sendSms() {
        CheckUserExistTask().execute(mMobile)
    }


    protected fun sendNotification(title: String, message: String) {
        Thread(

                Runnable {
                    runOnUiThread { showMessage("发送验证码成功！") }
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    val builder = NotificationCompat.Builder(activity)
                    builder.setSmallIcon(R.mipmap.icon_transparent)
                            .setContentText(message)
                            .setContentTitle(title)
                            .setTicker("新消息")
                            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS)
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setOnlyAlertOnce(true)
                            .setShowWhen(true)
                            .setWhen(System.currentTimeMillis())
                    val notification = builder.build()
                    val manager = NotificationManagerCompat.from(this@ForgetPwdActivity)
                    manager.notify(1, notification)
                    runOnUiThread { et_verify_code_or_repeat_pwd!!.setText(code) }
                }).start()
    }

    private fun turnPage() {
        currentPage = 1
        iv_mobile_or_pwd!!.setImageResource(R.drawable.ic_password)
        et_mobile_or_pwd!!.hint = "新密码"
        et_mobile_or_pwd!!.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        et_mobile_or_pwd!!.setText("")
        et_mobile_or_pwd!!.invalidate()
        et_verify_code_or_repeat_pwd!!.hint = "重复密码"
        et_verify_code_or_repeat_pwd!!.setText("")
        et_verify_code_or_repeat_pwd!!.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        et_verify_code_or_repeat_pwd!!.invalidate()
        btn_get_verify_code!!.visibility = View.GONE
        btn_next_or_commit!!.text = "提交"
    }

    private fun resetPwdSuccess(pwd: String) {
        ResetPwdTask().execute(pwd)
    }

    private inner class CheckUserExistTask : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg params: String): Boolean? {
            val mobile = params[0]
            val user = User.findByMobile(mobile)
            return user != null
        }

        override fun onPostExecute(aBoolean: Boolean?) {
            super.onPostExecute(aBoolean)
            if (aBoolean!!) {
                sendNotification("新消息", getString(R.string.send_verfiy_code_message, code))//// TODO: 2017/4/22
            } else {
                showMessage("用户不存在！")
            }
        }
    }

    private inner class ResetPwdTask : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg params: String): Void? {
            val pwd = params[0]
            val user = User.findByMobile(mMobile!!)
            user?.password = pwd
            user?.save()
            SPUtil.saveUser(user)
            return null
        }

        override fun onPostExecute(aVoid: Void) {
            super.onPostExecute(aVoid)

            showMessage("密码重置成功！")
            val intent = Intent(this@ForgetPwdActivity, LoginActivity::class.java)
            intent.putExtra("logout", true)
            startActivity(intent)
            finish()
        }
    }

    private fun backPage() {
        currentPage = 0
        iv_mobile_or_pwd!!.setImageResource(R.drawable.ic_phone)
        et_mobile_or_pwd!!.hint = "手机"
        et_mobile_or_pwd!!.setText(mMobile)
        et_mobile_or_pwd!!.inputType = InputType.TYPE_CLASS_NUMBER
        et_mobile_or_pwd!!.invalidate()
        et_verify_code_or_repeat_pwd!!.hint = "验证码"
        et_verify_code_or_repeat_pwd!!.setText(code)
        et_verify_code_or_repeat_pwd!!.inputType = InputType.TYPE_CLASS_NUMBER
        et_verify_code_or_repeat_pwd!!.invalidate()
        btn_get_verify_code!!.visibility = View.VISIBLE
        btn_next_or_commit!!.text = "下一步"
    }

    override fun onBackPressed() {
        if (currentPage == 1) {
            backPage()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                2 -> sendSms()
            }
        } else {
            showMessage(R.string.permission_denied_message)
        }
    }

    companion object {


        fun open(context: Context, mobile: String) {
            val intent = Intent(context, ForgetPwdActivity::class.java)
            intent.putExtra("mobile", mobile)
            context.startActivity(intent)
        }

        private fun generateCode(): String {
            val sb = StringBuilder()
            val r = Random()
            for (i in 0..3) {
                sb.append(r.nextInt(10))
            }
            return sb.toString()
        }
    }

}
