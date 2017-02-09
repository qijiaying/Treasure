package com.zhuoxin.treasure.treasure.detail;

/**
 * Created by Administrator on 2017/1/11.
 */

import com.google.gson.annotations.SerializedName;

// 宝藏详情请求数据
public class TreasureDetail {

    @SerializedName("TreasureID")
    private final int treasureId;

    @SerializedName("PagerSize")
    private final int pageSize;

    @SerializedName("currentPage")
    private final int currentPage;

    public TreasureDetail(int treasureId) {
        this.treasureId = treasureId;
        this.pageSize = 20;
        this.currentPage = 1;
    }
}
