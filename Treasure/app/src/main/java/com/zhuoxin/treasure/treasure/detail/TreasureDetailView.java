package com.zhuoxin.treasure.treasure.detail;

import java.util.List;

/**
 * Created by Administrator on 2017/1/11.
 */

//进行宝藏详情获取的视图接口
public interface TreasureDetailView {
    //显示信息
    void showMessage(String msg);

    //设置数据
    void setData(List<TreasureDetailResult> resultList);
}
