package cn.studyjams.s1.contest.accumulation.base;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.util.AppLog;
import cn.studyjams.s1.contest.accumulation.util.GoActivity;


/**
 * Fragment基础类
 * Created by Relish on 2016/10/27.
 */
public abstract class BaseFragment extends Fragment implements BaseView {

    /**
     * 布局文件ID
     *
     * @return 布局文件ID
     */
    protected abstract int layoutId();

    /**
     * 初始化
     */
    protected abstract void initViews(View contentView);

    private boolean isLoading;
    private View loadingView;
    private View contentView;
    protected View noMoreDataView;
    private ObjectAnimator contentViewAnimator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int resId = layoutId();
        if (resId != 0) {
            FrameLayout flRoot = (FrameLayout) inflater.inflate(R.layout.fragment_base, container, false);
            loadingView = flRoot.findViewById(R.id.tvLoading);
            noMoreDataView = flRoot.findViewById(R.id.tvNoMoreData);
            contentView = inflater.inflate(layoutId(), container, false);
            contentViewAnimator = ObjectAnimator.ofFloat(contentView, "alpha", 0f, 1f);
            contentViewAnimator.setDuration(500);
            flRoot.addView(contentView);

            initViews(contentView);

            return flRoot;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void goActivity(Class<?> clazz) {
        new GoActivity.Builder(clazz).build().act();
    }


    @Override
    public void showMessage(Object msg) {
        if (msg == null) {
            AppLog.e("BaseActivity", "showMessage", "The incoming parameter is null！！！！");
            return;
        }
        if (msg instanceof Integer) {
            Toast.makeText(getActivity(), (Integer) msg, Toast.LENGTH_SHORT).show();
        } else if (msg instanceof CharSequence) {
            Toast.makeText(getActivity(), (CharSequence) msg, Toast.LENGTH_SHORT).show();
        } else {
            AppLog.e("BaseActivity", "showMessage",
                    "The type of incoming param is " + msg.getClass().getSimpleName());
        }
    }

    public void showLoading(boolean shouldLoading) {
        if (shouldLoading && !isLoading) {
            // 显示加载界面
            isLoading = true;
            contentView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility(View.VISIBLE);
        } else if (!shouldLoading && isLoading) {
            //　隐藏加载界面
            isLoading = false;
            loadingView.setVisibility(View.INVISIBLE);
            contentViewAnimator.start();
            contentView.setVisibility(View.VISIBLE);
        }
    }

    public void noMoreData() {
        noMoreDataView.setVisibility(View.VISIBLE);
    }

    public void showData() {
        noMoreDataView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void goBrowser(String url) {
        if(url==null){
            showMessage(R.string.url_is_null);
            return;
        }

        if ((!url.startsWith("http://")) && (!url.startsWith("https://"))) {
            url = "http://" + url;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }
}
