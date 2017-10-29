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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseActivity;

/**
 * @author Relish Wang
 * @since 2017/10/28
 */
public abstract class IPhotoActivity extends BaseActivity {

    private static final int REQUEST_CAMERA_CODE = 0x101;
    private static final int REQUEST_ALBUM_CODE = 0x102;
    private static final int TAKE_PHOTO = 0x201;
    private static final int CHOOSE_PHOTO = 0x202;

    private Uri uri;
    private String photo;

    private OnPhotoGenerateListener mListener;

    protected void setPhoto(OnPhotoGenerateListener listener) {
        mListener = listener;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            showPopWindow();
        }
    }

    private void showPopWindow() {
        Window window = getWindow();
        if (window == null) return;
        View decorView = window.getDecorView();
        if (decorView == null) return;

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_photo, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.v_camera:
                        if (ContextCompat.checkSelfPermission(IPhotoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(IPhotoActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
                        } else {
                            takePhoto();
                        }
                        break;
                    case R.id.v_album:
                        if (ContextCompat.checkSelfPermission(IPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(IPhotoActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_ALBUM_CODE);
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
                        photo = uri.toString();
                        final Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener != null) mListener.onPhotoGenerate(bitmap, photo);
                            }
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

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 1:
                    openAlbum();
                    break;
            }
        } else {
            showMessage(R.string.permission_denied_message);
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
            if (mListener != null) mListener.onPhotoGenerate(bitmap, photo);
        } else {
            Toast.makeText(this, "显示图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnPhotoGenerateListener {
        void onPhotoGenerate(Bitmap bitmap, String photo);
    }
}
