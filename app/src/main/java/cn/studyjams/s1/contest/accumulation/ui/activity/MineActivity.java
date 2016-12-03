package cn.studyjams.s1.contest.accumulation.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.base.BaseActivity;
import cn.studyjams.s1.contest.accumulation.entity.User;
import cn.studyjams.s1.contest.accumulation.util.GoActivity;
import cn.studyjams.s1.contest.accumulation.util.SPUtil;

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

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mUser = SPUtil.getUser();
        if (mUser.isEmpty()) {
            finish();
        }
        ivHead = (ImageView) findViewById(R.id.iv_head);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvMobile = (TextView) findViewById(R.id.tv_mobile);
        tvEmail = (TextView) findViewById(R.id.tv_email);

        ivHead.setImageResource(R.mipmap.icon);//// TODO: 2016/12/3 网络加载
        tvName.setText(checkNull(mUser.getName()));
        tvMobile.setText(checkNull(mUser.getMobile()));
        tvEmail.setText(checkNull(mUser.getEmail()));
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
                Bundle bundle = new Bundle();
                bundle.putBoolean("logout",true);
                GoActivity.obtain(LoginActivity.class).setBundle(bundle).act();
                break;
        }
    }

    private String checkNull(String txt) {
        if (TextUtils.isEmpty(txt) || txt.equalsIgnoreCase("null"))
            return getString(R.string.unsetting);
        return txt;
    }
}
