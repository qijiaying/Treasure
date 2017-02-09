package com.zhuoxin.treasure.treasure.hide;

import com.zhuoxin.treasure.net.NetClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/12.
 */


//埋藏宝藏的业务类
public class HideTreasurePresenter {


    //过程中与视图的交互
    private HideTreasureView hideView;

    public HideTreasurePresenter(HideTreasureView hideView) {
        this.hideView = hideView;
    }

    public void hideTreasure(HideTreasure hideTreasure) {
        //显示进度
        hideView.showProgress();
        Call<HideTreasureResult> resultCall = NetClient.getInstances().getTreasureApi().hideTreasure(hideTreasure);
        resultCall.enqueue(resultCallback);
    }

    private Callback<HideTreasureResult> resultCallback = new Callback<HideTreasureResult>() {

        //请求成功
        @Override
        public void onResponse(Call<HideTreasureResult> call, Response<HideTreasureResult> response) {
            hideView.hideProgress();
            if (response.isSuccessful()) {
                HideTreasureResult treasureResult = response.body();
                if (treasureResult == null) {
                    //吐司
                    hideView.showMessage("未知错误");
                    return;
                }
                //真正的上传成功
                if (treasureResult.getCode() == 1) {
                    //跳转回首页
                    hideView.navigationToHome();
                }
                //提示
                hideView.showMessage(treasureResult.getMsg());
            }
        }

        //请求失败
        @Override
        public void onFailure(Call<HideTreasureResult> call, Throwable t) {
            //隐藏进度
            hideView.hideProgress();
            //提示
            hideView.showMessage("请求失败" + t.getMessage());
        }
    };
}
