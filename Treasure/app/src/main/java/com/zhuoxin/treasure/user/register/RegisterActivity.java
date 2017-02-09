package com.zhuoxin.treasure.user.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.zhuoxin.treasure.MainActivity;
import com.zhuoxin.treasure.R;
import com.zhuoxin.treasure.commons.ActivityUtils;
import com.zhuoxin.treasure.commons.RegexUtils;
import com.zhuoxin.treasure.custom.AlertDialogFragment;
import com.zhuoxin.treasure.treasure.HomeActivity;
import com.zhuoxin.treasure.user.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements RegisterView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_Username)
    EditText etUsername;
    @BindView(R.id.et_Password)
    EditText etPassword;
    @BindView(R.id.et_Confirm)
    EditText etConfirm;
    @BindView(R.id.btn_Register)
    Button btnRegister;
    private String username;
    private String password;
    private ActivityUtils activityUtils;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //会触发onContentChanged
        setContentView(R.layout.activity_register);
        activityUtils = new ActivityUtils(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        //toolbar的展示和返回监头的监听
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            //激活左上角的返回图标（内部使用选项菜单处理）
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //设置Title
            getSupportActionBar().setTitle(R.string.register);
        }
        //EditText的监听,监听文本的变化
        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etConfirm.addTextChangedListener(textWatcher);
    }

    //文本输入监听
    private TextWatcher textWatcher = new TextWatcher() {
        //文本变化前
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //文本输入变化
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //处理文本输入后的按钮事件
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            String confirm = etConfirm.getText().toString();
            boolean canregister = !(TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(password)
                    || TextUtils.isEmpty(confirm))
                    && password.equals(confirm);
            btnRegister.setEnabled(canregister);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //处理ActionBar返回箭头的事件
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_Register)
    public void onClick() {
        //注册的视图和业务处理
        if (RegexUtils.verifyUsername(username) != RegexUtils.VERIFY_SUCCESS) {
            //显示错误对话框
            AlertDialogFragment.getInstances(getString(R.string.username_error), getString(R.string.username_rules))
                    .show(getSupportFragmentManager(), "usernameError");
            return;
        }
        if (RegexUtils.verifyPassword(password) != RegexUtils.VERIFY_SUCCESS) {
            //显示错误对话框
            AlertDialogFragment.getInstances(getString(R.string.password_error), getString(R.string.password_rules))
                    .show(getSupportFragmentManager(), "passwordError");
            return;
        }
        //进行注册的功能，模拟场景进行注册，业务逻辑
        new RegisterPresenter(this).register(new User(username, password));
    }

    //跳转页面
    @Override
    public void navigationToHome() {
        activityUtils.startActivity(HomeActivity.class);
        finish();

        //发送本地广播，本地广播关闭页面
        Intent intent = new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //展示信息
    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    //隐藏进度
    @Override
    public void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    //展示进度
    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, "注册", "亲，正在注册中，请稍候~");
    }
}

