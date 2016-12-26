package cn.studyjams.s1.contest.accumulation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.base.BaseActivity;
import cn.studyjams.s1.contest.accumulation.entity.User;
import cn.studyjams.s1.contest.accumulation.util.AppLog;
import cn.studyjams.s1.contest.accumulation.util.SPUtil;

/**
 * 登录页
 * Created by Relish on 2016/11/4.
 */
public class LoginActivity extends BaseActivity implements FirebaseAuth.AuthStateListener,
        View.OnClickListener {
    @Override
    protected int layoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle(R.string.login);

    }

    AutoCompleteTextView etEmail;
    EditText etPwd;
    Button btnLogin;

    User mUser;

    private FirebaseAuth mAuth;

    private FirebaseUser user;

    private boolean isLogout = false;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            isLogout = intent.getBooleanExtra("logout", false);
            mUser = SPUtil.getUser();
        }

        mAuth = FirebaseAuth.getInstance();
        etEmail = (AutoCompleteTextView) findViewById(R.id.etEmail);
        etPwd = (EditText) findViewById(R.id.etPwd);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        if (mUser.isEmpty()) {
            if (user != null) {
                etEmail.setText(user.getEmail());
            }
        } else {
            etEmail.setText(mUser.getEmail());
            etPwd.setText(mUser.getPassword());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isLogout) {
            isLogout = false;
            return;
        }
        mAuth.addAuthStateListener(this);//这玩意儿会自动登录
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }


    @Override
    public void onClick(View v) {
        String email = etEmail.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Snackbar.make(v, R.string.email_not_be_null, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (email.matches("^[a-z0-9]+@[a-z0-9]+\\.[a-z]+$")) {
            Snackbar.make(v, R.string.email_format_error, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Snackbar.make(v, R.string.pwd_not_be_null, Snackbar.LENGTH_SHORT).show();
            return;
        }
        showLoading(R.string.logining);
        login(email,pwd);//// TODO: 2016/12/4 firebase的用户信息验证不好用
        mAuth.signInWithEmailAndPassword(email, pwd);
    }

    private void login(String email, String pwd) {

    }

    private static final String TAG = "LoginActivity";

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            AppLog.d(TAG, "onAuthStateChanged", "signed_in:" + user.getUid());
            showLoading(false);
            String email = etEmail.getText().toString().trim();
            String pwd = etPwd.getText().toString().trim();
            User user = new User();
            user.setPassword(pwd);
            user.setEmail(email);
            user.save();
            SPUtil.saveUser(user);
            goActivity(MainActivity.class);
            finish();
        } else {
            // User is signed out
            AppLog.d(TAG, "onAuthStateChanged", "signed_out");
            showLoading(false);
            showMessage(R.string.login_failed);
        }

    }
}
