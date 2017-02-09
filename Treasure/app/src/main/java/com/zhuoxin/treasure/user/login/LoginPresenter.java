package com.zhuoxin.treasure.user.login;


import com.zhuoxin.treasure.net.NetClient;
import com.zhuoxin.treasure.user.User;
import com.zhuoxin.treasure.user.UserPrefs;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/3.
 */

//登陆的业务类
public class LoginPresenter {

    private LoginView loginView;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
    }

    public void login(User user) {
        loginView.showProgress();
        Call<LoginResult> loginResultCall = NetClient.getInstances().getTreasureApi().login(user);
        loginResultCall.enqueue(callback);
    }

    private Callback<LoginResult> callback = new Callback<LoginResult>() {
        @Override
        public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
            loginView.hideProgress();
            if (response.isSuccessful()) {
                LoginResult loginResult = response.body();
                if (loginResult == null) {
                    loginView.showMessage("未知错误");
                    return;
                }
                if (loginResult.getCode() == 1) {
                    //真正的登录成功之后
                    //保存头像
                    UserPrefs.getInstance().setPhoto(NetClient.BASE_URL + loginResult.getHeadpic());
                    UserPrefs.getInstance().setTokenid(loginResult.getTokenid());
                    loginView.navigationToHome();
                }
                loginView.showMessage(loginResult.getMsg());
            }
        }

        @Override
        public void onFailure(Call<LoginResult> call, Throwable t) {
            loginView.hideProgress();
            loginView.showMessage("请求失败" + t.getMessage());
        }
    };
}
