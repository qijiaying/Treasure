package com.zhuoxin.treasure.treasure.hide;

/**
 * Created by Administrator on 2017/1/12.
 */

public interface HideTreasureView {

    //宝藏上传中视图的交互
    void showMessage(String msg);

    void showProgress();

    void hideProgress();

    void navigationToHome();
}
