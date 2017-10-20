package wang.relish.accumulation.ui.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import wang.relish.accumulation.App;
import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseActivity;
import wang.relish.accumulation.util.SPUtil;
import wang.relish.accumulation.util.ThreadPool;

/**
 * @author Relish Wang
 * @since 2017/10/16
 */
public class ImageActivity extends BaseActivity {

    public static final int REQUEST_CODE = 0x1002;
    private static final int REQUEST_CAMERA_CODE = 0x101;
    private static final int REQUEST_ALBUM_CODE = 0x102;
    private static final int TAKE_PHOTO = 0x201;
    private static final int CHOOSE_PHOTO = 0x202;
    private Uri uri;

    public static void open(BaseActivity activity, String uri, int requestCode) {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putExtra("uri", uri);
        activity.startActivityForResult(intent, requestCode);
    }

    private String photo;
    private ImageView iv;
    private boolean isModified = false;

    @Override
    protected void parseIntent(Intent intent) {
        photo = intent.getStringExtra("uri");
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_image;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle("查看头像");

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        iv = (ImageView) findViewById(R.id.iv);
        Glide.with(this)
                .load(photo)
                .into(iv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_modify_head:
                Window window = getWindow();
                if (window == null) return false;
                View decorView = window.getDecorView();
                if (decorView == null) return false;

                View popupView = LayoutInflater.from(this).inflate(R.layout.popup_photo, null);
                final PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                View.OnClickListener listener = new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.v_camera:
                                if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
                                } else {
                                    takePhoto();
                                }
                                break;
                            case R.id.v_album:
                                if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_ALBUM_CODE);
                                } else {
                                    openAlbum();
                                }
                                break;
                            case R.id.v_cancel:
                                break;
                        }
                        popupWindow.dismiss();
                    }
                };
                popupView.findViewById(R.id.v_camera).setOnClickListener(listener);
                popupView.findViewById(R.id.v_album).setOnClickListener(listener);
                popupView.findViewById(R.id.v_cancel).setOnClickListener(listener);

                popupWindow.setTouchable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setAnimationStyle(R.style.PopupAnimation);
                popupWindow.showAtLocation(decorView, Gravity.BOTTOM, 0, 0);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isModified) setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_CAMERA_CODE:
                    takePhoto();
                    break;
                case REQUEST_ALBUM_CODE:
                    openAlbum();
                    break;
            }
        } else {
            showMessage(R.string.permission_denied_message);
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void takePhoto() {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                if (!outputImage.delete()) {
                    showMessage("储存空间不足！无法拍照！");
                    return;
                }
            }
            if (!outputImage.createNewFile()) {
                showMessage("储存空间不足！无法拍照！");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("储存空间不足！无法拍照！");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this,
                    "wang.relish.accumulation.fileprovider", outputImage);
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
                        photo = uri.getPath();
                        final Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                iv.setImageBitmap(bitmap);
                            }
                        });
                        savePhoto();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        showMessage("储存空间不足！无法拍照！");
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
                        savePhoto();
                        initViews(null);//displayImage
                    } else {
                        Uri uri = data.getData();
                        String imagePath = getImagePath(uri, null);
                        photo = imagePath;
                        initViews(null);//displayImage
                        savePhoto();
                    }
                }
                break;
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

    public void savePhoto() {
        showLoading(true);
        isModified = true;
        ThreadPool.DATABASE.execute(new Runnable() {
            @Override
            public void run() {
                App.USER.setPhoto(photo);
                SPUtil.saveUser(App.USER);
                App.getDaosession().getUserDao().updateInTx(App.USER);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                    }
                });
            }
        });
    }
}
