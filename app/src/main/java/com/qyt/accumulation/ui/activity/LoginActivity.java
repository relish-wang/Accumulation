package com.qyt.accumulation.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qyt.accumulation.App;
import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseActivity;
import com.qyt.accumulation.entity.User;
import com.qyt.accumulation.util.SPUtil;

/**
 * 登录页
 * Created by Relish on 2016/11/4.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int layoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isBtnBackEnable() {
        return false;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle(R.string.login);
    }

    AutoCompleteTextView etMobile;
    EditText etPwd;
    Button btnLogin;

    User mUser;

    private boolean isLogout = false;


    TextView tv_forget_pwd, tv_register;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            isLogout = intent.getBooleanExtra("logout", false);
            mUser = SPUtil.getUser();
        }

        etMobile = (AutoCompleteTextView) findViewById(R.id.etEmail);
        etPwd = (EditText) findViewById(R.id.etPwd);
        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
        tv_register = (TextView) findViewById(R.id.tv_register);

        tv_forget_pwd.setOnClickListener(this);
        tv_register.setOnClickListener(this);

        etMobile.setText("relish.wang@gmail.com");
        etPwd.setText("qytadwx8023");

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        if (!mUser.isEmpty()) {
            etMobile.setText(mUser.getMobile());
            etPwd.setText(mUser.getPassword());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isLogout) {
            isLogout = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                login(v);
                break;
            case R.id.tv_forget_pwd:
                Intent intent = new Intent(this, ForgetPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_register:
                Intent intent1 = new Intent(this, RegisterActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void login(View v) {
        String mobile = etMobile.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            Snackbar.make(v, R.string.mobile_not_be_null, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (mobile.matches("^1[0-9]{10}$")) {
            Snackbar.make(v, R.string.mobile_format_error, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Snackbar.make(v, R.string.pwd_not_be_null, Snackbar.LENGTH_SHORT).show();
            return;
        }
        showLoading(R.string.logining);
        login(mobile, pwd);
    }

    private void login(String mobile, String pwd) {
        new AsyncTask<String, Void, User>() {

            @Override
            protected User doInBackground(String... params) {
                return User.login(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(User user) {
                super.onPostExecute(user);
                if (user == null) {
                    showMessage(R.string.account_or_password_is_not_collect);
                    showLoading(false);
                } else {
                    SPUtil.saveUser(user);
                    App.USER = user;
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        }.execute(mobile, pwd);
    }
}