package com.qyt.accumulation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseActivity;
import com.qyt.accumulation.util.BarUtil;
import com.qyt.accumulation.util.SPUtil;

public class SplashActivity extends BaseActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected boolean removeParent() {
        return true;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        BarUtil.setStatusBarTransparent(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        View view = findViewById(R.id.background);
        ImageView icon = (ImageView) findViewById(R.id.icon);
        TextView tv = (TextView) findViewById(R.id.propaganda);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        view.setAnimation(animation);
        icon.setAnimation(animation);
        tv.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                boolean autoLogin = SPUtil.getBoolean("autoLogin", false);
                if (autoLogin) {
                    String username = SPUtil.getString("account");
                    String password = SPUtil.getString("password");
                    //// TODO: 2017/2/23 自动登录
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
