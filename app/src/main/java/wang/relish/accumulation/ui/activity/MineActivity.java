package wang.relish.accumulation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import wang.relish.accumulation.App;
import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseActivity;
import wang.relish.accumulation.entity.User;
import wang.relish.accumulation.util.SPUtil;

/**
 * 个人中心
 * Created by Relish on 2016/11/13.
 */
public class MineActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int layoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle(R.string.mine);
    }

    private User mUser;

    private ImageView ivHead;
    private TextView tvName;
    private TextView tvMobile;
    private TextView tvEmail;
    private View vSetting;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mUser = SPUtil.getUser();
        if (App.isEmpty(mUser)) finish();
        ivHead = (ImageView) findViewById(R.id.iv_head);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvMobile = (TextView) findViewById(R.id.tv_mobile);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        vSetting = findViewById(R.id.rl_setting);

        Glide.with(this)
                .load(mUser.getPhoto())
                .centerCrop()
                .placeholder(R.mipmap.icon)
                .crossFade()
                .into(ivHead);
        tvName.setText(checkNull(mUser.getName()));
        tvMobile.setText(checkNull(mUser.getMobile()));
        vSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goActivity(SettingActivity.class);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_head:

                break;
            case R.id.rl_name:

                break;
            case R.id.rl_mobile:

                break;
            case R.id.rl_email:

                break;
            case R.id.btn_logout:
                App.exitApp();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("logout", true);
                startActivity(intent);
                SPUtil.putBoolean("autoLogin", false);
                break;
        }
    }

    private String checkNull(String txt) {
        if (TextUtils.isEmpty(txt) || txt.equalsIgnoreCase("null"))
            return getString(R.string.unsetting);
        return txt;
    }
}
