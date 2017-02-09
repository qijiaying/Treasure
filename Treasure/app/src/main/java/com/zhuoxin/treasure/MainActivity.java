package com.zhuoxin.treasure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zhuoxin.treasure.commons.ActivityUtils;
import com.zhuoxin.treasure.treasure.HomeActivity;
import com.zhuoxin.treasure.user.UserPrefs;
import com.zhuoxin.treasure.user.login.LoginActivity;
import com.zhuoxin.treasure.user.register.RegisterActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {


    public static final String MAIN_ACTION = "navigation_to_home";
    private ActivityUtils activityUtils;
    private Unbinder unbinder;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityUtils = new ActivityUtils(this);
        unbinder = ButterKnife.bind(this);

        //判断用户是否登陆过
        SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
        if (preferences != null) {
            if (preferences.getInt("key_tokenid", 0) == UserPrefs.getInstance().getTokenid()) {
                activityUtils.startActivity(HomeActivity.class);
                finish();
            }
        }

        //1、注册本地的广播
        IntentFilter intentFilter = new IntentFilter(MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @OnClick({R.id.btn_Register, R.id.btn_Login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Register:
                activityUtils.startActivity(RegisterActivity.class);
                break;
            case R.id.btn_Login:
                activityUtils.startActivity(LoginActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
