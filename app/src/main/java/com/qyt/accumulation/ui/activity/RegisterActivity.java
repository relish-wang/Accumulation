package com.qyt.accumulation.ui.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import com.qyt.accumulation.App;
import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseActivity;
import com.qyt.accumulation.entity.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int layoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle(R.string.register);
    }

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    RelativeLayout rl_head;
    ImageView iv_head, iv_camera;
    EditText et_name, et_mobile, et_code, et_pwd, et_repeat_pwd;
    Button btn_get_verify_code;

    String photo = null;
    String verify_code = null;

    Handler handler = new Handler();

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

        rl_head.setOnClickListener(this);
        btn_get_verify_code.setOnClickListener(this);

        SMSSDK.initSDK(this, "<1b908e47149a3>", "<3c4fe351b4e0998958c19f1fa4351f5e>");
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_head:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    setHeadPhoto();
                }
                break;
            case R.id.btn_get_verify_code:
                //// TODO: 2017/2/23 获取验证码
                if (!TextUtils.isEmpty(et_mobile.getText().toString().trim())) {
                    if (et_mobile.getText().toString().trim().length() == 11) {
                        String iPhone = et_mobile.getText().toString().trim();
                        SMSSDK.getVerificationCode("86", iPhone);
                        et_code.requestFocus();
                        btn_get_verify_code.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "请输入完整电话号码", Toast.LENGTH_LONG).show();
                        et_mobile.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "请输入您的电话号码", Toast.LENGTH_LONG).show();
                    et_mobile.requestFocus();
                }
                break;
        }
    }

    private static Uri uri;

    private void setHeadPhoto() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setItems(R.array.set_head_click, (d, which) -> {
                    switch (which) {
                        case 0:
                            takePhoto();
                            break;
                        case 1:
                            openAlbum();
                            break;
                        case 2:
                            d.dismiss();
                            break;
                    }
                })
                .create();
        dialog
                .show();

    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void takePhoto() {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this,
                    "com.qyt.accumulation.fileprovider", outputImage);
        } else {
            uri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍摄的照片显示出来
                    try {
                        photo = getImagePath(uri, null);
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        runOnUiThread(() -> {
                            iv_head.setImageBitmap(bitmap);
                            iv_camera.setVisibility(View.GONE);
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        String imagePath = null;
                        Uri uri = data.getData();
                        if (DocumentsContract.isDocumentUri(this, uri)) {
                            //如果是Document类型的uri，则通过document id处理
                            String docId = DocumentsContract.getDocumentId(uri);
                            if (TextUtils.equals("com.android.providers.media.documents",
                                    uri.getAuthority())) {
                                String id = docId.split(":")[1];//解析出数字格式的id
                                String selection = MediaStore.Images.Media._ID + "=" + id;
                                imagePath = getImagePath(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                            } else if (TextUtils.equals("com.android.providers.downloads.documents",
                                    uri.getAuthority())) {
                                Uri contentUri = ContentUris.withAppendedId(
                                        Uri.parse("content://downloads/public_downloads"),
                                        Long.valueOf(docId));
                                imagePath = getImagePath(contentUri, null);
                            }
                        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                            //如果是content类型的Uri，则使用普通方法处理
                            imagePath = getImagePath(uri, null);
                        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            //如果是file类型的Uri，直接获取图片路径即可
                            imagePath = uri.getPath();
                        }
                        photo = imagePath;
                        displayImage(imagePath);
                    } else {
                        Uri uri = data.getData();
                        String imagePath = getImagePath(uri, null);
                        photo = imagePath;
                        displayImage(imagePath);
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    showMessage(R.string.permission_denied_message);
                }
                break;
            default:
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
                if (!mobile.matches("^[\\+86|]1[0-9]{10}$")) {
                    showMessage("请填写正确的手机号码");
                    return false;
                }
                if (TextUtils.isEmpty(code)) {
                    showMessage("验证码不得为空");
                    return false;
                }
                if (!TextUtils.equals(code, verify_code)) {
                    boolean flag;

                    if (et_code.getText().toString().trim().length() == 4) {

                        String iCord = et_code.getText().toString().trim();

                        SMSSDK.submitVerificationCode("86", mobile, iCord);

                        flag = false;

                    } else {

                        Toast.makeText(this, "请输入完整验证码", Toast.LENGTH_LONG).show();

                        et_code.requestFocus();

                    }


                    showMessage("请填写正确的验证码");
                    return false;
                }
                if (TextUtils.isEmpty(pwd)) {
                    showMessage("密码不得为空");
                    return false;
                }
                if (!TextUtils.equals(pwd, repeat_pwd)) {
                    showMessage("两次密码输入不一致");
                    return false;
                }
                User user = new User();
                user.setName(name);
                user.setPhoto(photo);
                user.setMobile(mobile);
                user.setPassword(pwd);
                new RegisterTask().execute(user);
                break;
            default:
        }
        return true;
    }


    class RegisterTask extends AsyncTask<User, Void, User> {

        @Override
        protected User doInBackground(User... params) {
            params[0].save();
            return User.findByMobile(params[0].getMobile());
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (user != null) {
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("注册成功")
                        .setMessage("恭喜用户【" + user.getName() + "】注册成功！")
                        .setPositiveButton("回到登录页", (dialog, which) -> {
                            App.USER = user;
                            setResult(RESULT_OK);
                            finish();
                        }).create().show();
            } else {
                showMessage("注册失败，请重试");
            }
        }
    }

    /**
     * 通过Uri和selection来获取真实的图片路径
     *
     * @param uri       uri
     * @param selection selection
     * @return 真实图片路径
     */
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            iv_head.setImageBitmap(bitmap);
            iv_camera.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "设置头像失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}