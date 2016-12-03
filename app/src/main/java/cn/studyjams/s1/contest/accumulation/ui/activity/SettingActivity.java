package cn.studyjams.s1.contest.accumulation.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.base.BaseActivity;
import cn.studyjams.s1.contest.accumulation.util.GoActivity;

public class SettingActivity extends BaseActivity {
    private TextView tvCondition;
    private Button btnSunny;
    private Button btnFoggy;

    private Firebase mFireRef;

    @Override
    protected int layoutId() {
        return 0;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        tvCondition = (TextView) findViewById(R.id.text_condition);

        mFireRef = new Firebase("https://fire-weather.firebaseio.com/condition");
        mFireRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String condition = dataSnapshot.getValue(String.class);
                tvCondition.setText(condition);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        btnSunny = (Button) findViewById(R.id.button_sunny);
        btnFoggy = (Button) findViewById(R.id.button_foggy);
        btnSunny.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("logout",true);
            GoActivity.obtain(LoginActivity.class).setBundle(bundle).act();}/*mFireRef.setValue("Sunny")*/);
        btnFoggy.setOnClickListener(v -> mFireRef.setValue("Foggy"));
    }
}
