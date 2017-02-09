package com.zhuoxin.treasure.user.register;

import android.os.AsyncTask;

import com.zhuoxin.treasure.net.NetClient;
import com.zhuoxin.treasure.user.User;
import com.zhuoxin.treasure.user.UserPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/3.
 */

//注册的业务类
public class RegisterPresenter {
    /**
     * 视频的交互的处理
     * 1、RegisterActivity
     * 2、接口回调  接口实例化
     */

    private RegisterView registerView;

    public RegisterPresenter(RegisterView registerView) {
        this.registerView = registerView;
    }

    public void register(User user) {
        /*//进行注册的功能，模拟场景进行注册，业务逻辑
        *//**
         * 3个泛型：
         * 3. 1. 启动任务输入的参数类型：请求的地址、上传的数据等类型
         * 3. 2. 后台任务执行的进度：一般是Integer类型(int的包装类)
         * 3. 3. 后台返回的结果类型：比如String类型、Void等
         * 模拟注册，三个泛型都不需要的时候都可以设置成Void
         *//*
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //UI处理，进度条展示
                registerView.showProgress();
            }

            @Override
            protected Void doInBackground(Void... params) {
                //后台进程，做网络请求
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //拿到数据，做UI更新
                //1、注册成功之后的处理
                registerView.hideProgress();
                registerView.showMessage("注册成功");
                registerView.navigationToHome();
            }
        }.execute();*/

        //------------------------------------------------

        registerView.showProgress();
        Call<RegisterResult> resultCall = NetClient.getInstances().getTreasureApi().register(user);
        resultCall.enqueue(resultCallback);


        //--------------------------------------------------
    }

    private Callback<RegisterResult> resultCallback = new Callback<RegisterResult>() {

        //请求成功
        @Override
        public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
            registerView.hideProgress();
            if (response.isSuccessful()) {
                RegisterResult result = response.body();
                if (result == null) {
                    registerView.showMessage("发生未知错误");
                    return;
                }
                //
                if (result.getCode() == 1) {
                    //真正的注册成功之后
                    UserPrefs.getInstance().setTokenid(result.getTokenId());
                    registerView.navigationToHome();
                }
                registerView.showMessage(result.getMsg());
            }
        }

        //请求失败
        @Override
        public void onFailure(Call<RegisterResult> call, Throwable t) {
            registerView.hideProgress();
            registerView.showMessage("请求失败" + t.getMessage());
        }
    };
}
