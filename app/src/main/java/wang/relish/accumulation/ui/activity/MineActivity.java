package wang.relish.accumulation.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import wang.relish.accumulation.App;
import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseActivity;
import wang.relish.accumulation.entity.User;
import wang.relish.accumulation.util.SPUtil;
import wang.relish.accumulation.util.ThreadPool;

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

    public static final int  REQUEST_CODE = 0x928;
    boolean isModified = false;
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
                ImageActivity.open(this, mUser.getPhoto(), ImageActivity.REQUEST_CODE);
                break;
            case R.id.rl_name: {
                @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(
                        R.layout.dialog_modify_profile, null);
                final EditText etName = v.findViewById(R.id.et_profile);
                String mUserName = mUser.getName();
                String name = TextUtils.isEmpty(mUserName) ? "手机号" : mUserName;
                etName.setHint(name);
                new AlertDialog.Builder(getActivity())
                        .setView(v)
                        .setTitle(R.string.modify_name)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                String newName = etName.getText().toString().trim();
                                modifyName(newName);
                            }
                        }).create().show();
            }
                break;
            case R.id.rl_mobile: {
                // TODO 需要短信验证
                break;
            }
            case R.id.rl_email:
                //TODO 绑定邮箱
                // 发送验证邮件等
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

    private void modifyName(final String newName) {
        ThreadPool.DATABASE.execute(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = false;
                try {
                    User user = mUser;
                    user.setName(newName);
                    App.getDaosession().getUserDao().updateInTx(user);
                    mUser.setName(newName);
                    SPUtil.saveUser(mUser);
                    isSuccess = true;
                    isModified = true;
                } catch (Exception ignore) {
                } finally {
                    final boolean finalIsSuccess = isSuccess;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalIsSuccess) {
                                tvName.setText(newName);
                            } else {
                                Toast.makeText(MineActivity.this, "名字修改失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private String checkNull(String txt) {
        if (TextUtils.isEmpty(txt) || txt.equalsIgnoreCase("null"))
            return getString(R.string.unsetting);
        return txt;
    }

    @Override
    public void onBackPressed() {
        if (isModified) setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ImageActivity.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mUser = App.USER;
                    Glide.with(this)
                            .load(mUser.getPhoto())
                            .centerCrop()
                            .placeholder(R.mipmap.icon)
                            .crossFade()
                            .into(ivHead);
                    isModified = true;
                }
        }
    }
}
