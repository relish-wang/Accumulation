package wang.relish.accumulation.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import wang.relish.accumulation.App;
import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseActivity;
import wang.relish.accumulation.util.ScoreUtils;

/**
 * 关于页
 * Created by Relish on 2016/11/7.
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected int layoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle(R.string.about);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViews(Bundle savedInstanceState) {
        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("v" + App.getVersionName(this));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_blog:
                goBrowser("http://relish.wang");
                break;

            case R.id.rl_github:
                goBrowser("https://github.com/relish-wang/");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.feedback:
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:relish-wang@gmail.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject, getString(R.string.app_name)));
                data.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_text, getString(R.string.app_name)));
                startActivity(data);
                break;
            case R.id.comment:
                final ArrayList<String> markets = ScoreUtils.InstalledAPPs(getActivity());
                if (markets.size() >= 0 && markets.contains(App.GOOGLE_PLAY)) {
                    ScoreUtils.launchAppDetail(getActivity().getPackageName(), App.GOOGLE_PLAY);
                } else {
                    showMessage(R.string.no_google_play);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
