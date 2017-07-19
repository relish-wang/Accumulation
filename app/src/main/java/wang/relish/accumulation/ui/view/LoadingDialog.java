package wang.relish.accumulation.ui.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import wang.relish.accumulation.R;

/**
 * 正在加载对话框（获取焦点，只能通过主动调用dismiss()方法消失）
 *
 * @author qiuy qiuy@servyou.com.cn
 * @version 1.0 2015年8月26日
 */
public class LoadingDialog extends DialogFragment {

    public static LoadingDialog getInstance(String msg) {
        LoadingDialog dialog = new LoadingDialog();
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        dialog.setArguments(bundle);
        return dialog;
    }

    TextView tvLoading;
    View v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //noinspection ConstantConditions
        getDialog().getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
//        setCancelable(false);
        v = inflater.inflate(R.layout.dialog_loading, container, false);
        tvLoading = (TextView) v.findViewById(R.id.tv_loading);
        tvLoading.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String msg = bundle.getString("msg");
            if (!TextUtils.isEmpty(msg)) {
                tvLoading.setText(msg);
            }else{
                tvLoading.setText(R.string.loading);
            }
        }else{
            tvLoading.setText(R.string.loading);
        }
        return v;
    }

    public void setText(String msg){
        if(msg==null){
            msg = getString(R.string.loading);
        }
        if(tvLoading==null){
            tvLoading = (TextView) v.findViewById(R.id.tv_loading);
        }
        tvLoading.setText(msg);
    }

    public boolean isShowing() {
        return isAdded();
    }

    public void onShow(String str) {
        setText(str);
    }

    public void onShow() {
        setText(getString(R.string.loading));
    }
}
