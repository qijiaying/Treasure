package com.zhuoxin.treasure.user.login;

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

public class LoginActivity extends AppCompatActivity implements LoginView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_Username)
    EditText etUsername;
    @BindView(R.id.et_Password)
    EditText etPassword;
    @BindView(R.id.btn_Login)
    Button btnLogin;
    private String username;
    private String password;
    private ProgressDialog dialog;

    private ActivityUtils activityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        //toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.login);
        }


        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            boolean canLogin = !(TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(password));
            btnLogin.setEnabled(canLogin);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_Login)
    public void onClick() {

        if (RegexUtils.verifyUsername(username) != RegexUtils.VERIFY_SUCCESS) {
            AlertDialogFragment.getInstances(getString(R.string.username_error)
                    , getString(R.string.username_rules))
                    .show(getSupportFragmentManager(), "usernameError");
            return;
        }
        if (RegexUtils.verifyPassword(password) != RegexUtils.VERIFY_SUCCESS) {
            //显示错误对话框
            AlertDialogFragment.getInstances(getString(R.string.password_error)
                    , getString(R.string.password_rules))
                    .show(getSupportFragmentManager(), "passwordError");
            return;
        }

        //业务逻辑处理
        new LoginPresenter(this).login(new User(username, password));
    }

    //-------------------------------视图接口方法的具体实现-------------------------------
    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, "登陆", "亲，正在登录中，请稍候~");
    }

    @Override
    public void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void navigationToHome() {
        activityUtils.startActivity(HomeActivity.class);
        finish();
        //发广播，关闭Main页面
        Intent intent = new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}