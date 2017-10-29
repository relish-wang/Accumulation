package wang.relish.accumulation.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import wang.relish.accumulation.App;
import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseActivity;
import wang.relish.accumulation.util.SPUtil;
import wang.relish.accumulation.util.ThreadPool;

/**
 * @author Relish Wang
 * @since 2017/10/16
 */
public class ImageActivity extends IPhotoActivity {

    public static final int REQUEST_CODE = 0x1002;

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
                setPhoto(new OnPhotoGenerateListener() {
                    @Override
                    public void onPhotoGenerate(final Bitmap bitmap, final String photo) {
                        ThreadPool.DATABASE.execute(new Runnable() {
                            @Override
                            public void run() {
                                App.USER.setPhoto(photo);
                                SPUtil.saveUser(App.USER);
                                App.getDaosession().getUserDao().updateInTx(App.USER);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv.setImageBitmap(bitmap);
                                    }
                                });
                                isModified = true;
                            }
                        });
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isModified) setResult(RESULT_OK);
        super.onBackPressed();
    }
}
