package com.zhuoxin.treasure.treasure.map;

import com.zhuoxin.treasure.treasure.Treasure;

import java.util.List;

/**
 * Created by Administrator on 2017/1/10.
 */

public interface MapMvpView {

    void showMessage(String msg);//弹吐司

    void SetData(List<Treasure> list);//设置数据
}
