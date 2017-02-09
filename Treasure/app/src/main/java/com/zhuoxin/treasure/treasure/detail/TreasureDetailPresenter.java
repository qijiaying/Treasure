package com.zhuoxin.treasure.treasure.detail;

import com.zhuoxin.treasure.net.NetClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/11.
 */

public class TreasureDetailPresenter {
    private TreasureDetailView detailView;

    public TreasureDetailPresenter(TreasureDetailView detailView) {
        this.detailView = detailView;
    }

    public void getTreasureDetail(TreasureDetail treasureDetail) {
        Call<List<TreasureDetailResult>> detailCall = NetClient.getInstances().getTreasureApi().getTreasureDetail(treasureDetail);
        detailCall.enqueue(listCallback);
    }

    //回调的Callback
    private Callback<List<TreasureDetailResult>> listCallback = new Callback<List<TreasureDetailResult>>() {

        @Override
        public void onResponse(Call<List<TreasureDetailResult>> call, Response<List<TreasureDetailResult>> response) {
            if (response.isSuccessful()) {
                List<TreasureDetailResult> resultList = response.body();
                if (resultList == null) {
                    //弹吐司
                    detailView.showMessage("未知的错误");
                    return;
                }
                //数据获取到
                detailView.setData(resultList);
            }
        }


        @Override
        public void onFailure(Call<List<TreasureDetailResult>> call, Throwable t) {
            detailView.showMessage("请求失败" + t.getMessage());
        }
    };
}
