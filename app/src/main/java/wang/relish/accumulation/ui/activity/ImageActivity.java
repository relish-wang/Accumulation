package wang.relish.accumulation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseActivity;

/**
 * @author Relish Wang
 * @since 2017/10/16
 */
public class ImageActivity extends BaseActivity {

    public static final int REQUEST_CODE = 0x1002;

    public static void open(BaseActivity activity, String uri, int requestCode) {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putExtra("uri", uri);
        activity.startActivityForResult(intent, requestCode);
    }

    private String mUri;
    private ImageView iv;
    private boolean isModified = false;

    @Override
    protected void parseIntent(Intent intent) {
        mUri = intent.getStringExtra("uri");
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
                .load(mUri)
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
                                //// TODO: 2017/10/17 拍照
                                break;
                            case R.id.v_album:
                                //// TODO: 2017/10/17 相册
                                break;
                            case R.id.v_cancel:
                                popupWindow.dismiss();
                                break;
                        }
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
}
