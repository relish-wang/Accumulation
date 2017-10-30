package wang.relish.accumulation.ui.activity;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Random;

import wang.relish.accumulation.App;
import wang.relish.accumulation.R;
import wang.relish.accumulation.entity.Goal;
import wang.relish.accumulation.entity.User;
import wang.relish.accumulation.greendao.DaoSession;
import wang.relish.accumulation.greendao.UserDao;
import wang.relish.accumulation.util.PhoneUtils;
import wang.relish.accumulation.util.SPUtil;
import wang.relish.accumulation.util.TimeUtil;


public class RegisterActivity extends IPhotoActivity implements View.OnClickListener {

    private static final String PHOTO = "photo";
    private static final String NAME = "name";
    private static final String MOBILE = "mobile";
    private static final String VERIFY_CODE = "verify_code";
    private static final String PASSWORD = "password";
    private static final String REPEAT_PWD = "repeat_pwd";

    @Override
    protected int layoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle(R.string.register);
    }

    RelativeLayout rl_head;
    ImageView iv_head, iv_camera;
    EditText et_name, et_mobile, et_code, et_pwd, et_repeat_pwd;
    Button btn_get_verify_code;

    private String photo = null;
    private String mobile;
    private String name;
    private String verify_code = "";
    private String password;
    private String repeat_pwd;

    @Override
    protected void parseIntent(Intent intent) {
        super.parseIntent(intent);
        photo = checkStringNull(intent.getStringExtra(PHOTO));
        name = checkStringNull(intent.getStringExtra(NAME));
        mobile = checkStringNull(intent.getStringExtra(MOBILE));
        verify_code = checkStringNull(intent.getStringExtra(VERIFY_CODE));
        password = checkStringNull(intent.getStringExtra(PASSWORD));
        repeat_pwd = checkStringNull(intent.getStringExtra(REPEAT_PWD));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        rl_head = (RelativeLayout) findViewById(R.id.rl_head);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
        et_name = (EditText) findViewById(R.id.et_name);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_code = (EditText) findViewById(R.id.et_code);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_repeat_pwd = (EditText) findViewById(R.id.et_repeat_pwd);
        btn_get_verify_code = (Button) findViewById(R.id.btn_get_verify_code);
        if (TextUtils.isEmpty(photo)) {
            Glide.with(this).load(photo);
        }
        et_name.setText(name);
        et_mobile.setText(mobile);
        et_code.setText(verify_code);
        et_pwd.setText(password);
        et_repeat_pwd.setText(repeat_pwd);

        rl_head.setOnClickListener(this);
        btn_get_verify_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_head:
                setPhoto(new OnPhotoGenerateListener() {
                    @Override
                    public void onPhotoGenerate(Bitmap bitmap, String photo) {
                        RegisterActivity.this.photo = photo;
                        iv_head.setImageBitmap(bitmap);
                        iv_camera.setVisibility(View.GONE);
                    }
                });
                break;
            case R.id.btn_get_verify_code:
                if (!TextUtils.isEmpty(et_mobile.getText().toString().trim())) {
                    if (et_mobile.getText().toString().trim().length() == 11) {
                        mobile = et_mobile.getText().toString().trim();
                        if (mobile.matches(PhoneUtils.MOBILE_PATTERN)) {
                            verify_code = generateCode();
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);
                            } else {
                                sendSms();
                            }
                        } else {
                            Toast.makeText(this, R.string.phone_number_error, Toast.LENGTH_LONG).show();
                            et_mobile.requestFocus();
                        }
                    } else {
                        Toast.makeText(this, R.string.phone_number_error, Toast.LENGTH_LONG).show();
                        et_mobile.requestFocus();
                    }
                } else {
                    Toast.makeText(this, R.string.phone_number_empty, Toast.LENGTH_LONG).show();
                    et_mobile.requestFocus();
                }
                break;
        }
    }


    private void sendSms() {
        sendNotification("新消息", getString(R.string.send_verfiy_code_message, verify_code));
    }


    private static String generateCode() {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    private static Uri uri;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 2:
                    sendSms();
                    break;
            }
        } else {
            showMessage(R.string.permission_denied_message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.register:
                //注册
                String name = et_name.getText().toString();
                String mobile = et_mobile.getText().toString();
                String code = et_code.getText().toString();
                String pwd = et_pwd.getText().toString();
                String repeat_pwd = et_repeat_pwd.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    showMessage("名字不得为空");
                    return false;
                }
                if (TextUtils.isEmpty(mobile)) {
                    showMessage("手机号不得为空");
                    return false;
                }
                if (!mobile.matches(PhoneUtils.MOBILE_PATTERN)) {
                    showMessage("请填写正确的手机号码");
                    return false;
                }
                if (TextUtils.isEmpty(code)) {
                    showMessage("验证码不得为空");
                    return false;
                }
                if (!TextUtils.equals(code, verify_code)) {
                    if (et_code.getText().toString().trim().length() == 4) {
                        String iCord = et_code.getText().toString().trim();
                        if (!TextUtils.equals(verify_code, iCord)) {
                            Toast.makeText(this, R.string.verify_code_error, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    } else {
                        Toast.makeText(this, R.string.please_input_correct_verify_code, Toast.LENGTH_LONG).show();
                        et_code.requestFocus();
                    }

                }
                if (TextUtils.isEmpty(pwd)) {
                    showMessage("密码不得为空");
                    return false;
                }
                if (!TextUtils.equals(pwd, repeat_pwd)) {
                    showMessage("两次密码输入不一致");
                    return false;
                }
                final User user = new User();
                user.setName(name);
                user.setPhoto(photo);
                user.setMobile(mobile);
                user.setPassword(pwd);

                register(user, new Callback() {
                    @Override
                    public void success(User u) {
                        if (u == null) {
                            showMessage("注册失败，请重试");
                            return;
                        }
                        SPUtil.putString("mobile", user.getMobile());
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("注册成功")
                                .setMessage("恭喜用户【" + user.getName() + "】注册成功！")
                                .setPositiveButton("回到登录页", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        App.USER = user;
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }).create().show();
                    }

                    @Override
                    public void error(String message) {
                        showMessage(message);
                    }
                });
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void register(final User user, final Callback callback) {
        final DaoSession daosession = App.getDaosession();
        daosession.runInTx(new Runnable() {
            @Override
            public void run() {
                UserDao userDao = daosession.getUserDao();
                User user1 = userDao.queryBuilder().where(UserDao.Properties.Mobile.eq(user.getMobile())).unique();
                if (user1 != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMessage("该手机号已注册！");
                        }
                    });
                    return;
                }
                long insert = userDao.insert(user);
                if (insert > 0) {
                    long timestamp = System.currentTimeMillis();
                    String time = TimeUtil.longToDateTime(timestamp);

                    Goal goal = new Goal();
                    goal.setId(0L);
                    goal.setMobile(user.getMobile());
                    goal.setName("未分类");
                    goal.setUpdateTime(time);
                    goal.setTime(time);
                    long saveGoal = 0;
                    try {
                        saveGoal = daosession.getGoalDao().insert(goal);
                    } catch (Exception e) {
                        callback.error(e.getMessage());
                        return;
                    }
                    if (saveGoal < 0) {
                        callback.error("插入失败");
                    } else {
                        User u = userDao.queryBuilder()
                                .where(UserDao.Properties.Mobile.eq(user.getMobile()))
                                .unique();
                        callback.success(u);
                    }
                } else {
                    callback.error("插入失败");
                }
            }
        });
    }

    interface Callback {

        void success(User u);

        void error(String message);
    }

    protected void sendNotification(final String title, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMessage("发送验证码成功！");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
                builder.setSmallIcon(R.mipmap.icon_transparent)
                        .setContentText(message)
                        .setContentTitle(title)
                        .setTicker("新消息")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setOnlyAlertOnce(true)
                        .setShowWhen(true)
                        .setWhen(System.currentTimeMillis());
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                String name = et_name.getText().toString();
                String mobile = et_mobile.getText().toString();
                String password = et_pwd.getText().toString();
                String repeat_pwd = et_repeat_pwd.getText().toString();
                intent.putExtra(PHOTO, photo);
                intent.putExtra(NAME, name);
                intent.putExtra(MOBILE, mobile);
                intent.putExtra(VERIFY_CODE, verify_code);
                intent.putExtra(PASSWORD, password);
                intent.putExtra(REPEAT_PWD, repeat_pwd);
                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pendingIntent);
                Notification notification = builder.build();
                NotificationManagerCompat manager = NotificationManagerCompat.from(RegisterActivity.this);
                manager.notify(1, notification);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_code.setText(verify_code);
                    }
                });
            }
        }).start();
    }

    private static String checkStringNull(String str) {
        return TextUtils.isEmpty(str) ? "" : str;
    }
}
