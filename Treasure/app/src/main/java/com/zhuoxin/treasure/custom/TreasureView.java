package com.zhuoxin.treasure.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.zhuoxin.treasure.R;
import com.zhuoxin.treasure.treasure.Treasure;
import com.zhuoxin.treasure.treasure.map.MapFragment;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/10.
 */

public class TreasureView extends RelativeLayout {

    @BindView(R.id.tv_treasureTitle)
    TextView tvTreasureTitle;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_treasureLocation)
    TextView tvTreasureLocation;

    public TreasureView(Context context) {
        this(context, null);
    }

    public TreasureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TreasureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_treasure, this, true);
        ButterKnife.bind(this);
    }

    //可以对外提供一个方法，根据宝藏信息填充我们需要的内容
    public void bindTreasure(@NonNull Treasure treasure) {
        //填充标题和地址
        tvTreasureTitle.setText(treasure.getTitle());
        tvTreasureLocation.setText(treasure.getLocation());

        double distace = 0.00d;//距离

        //宝藏经纬度
        LatLng latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());

        //我们的位置
        LatLng myLocation = MapFragment.getMyLocation();
        if (myLocation == null) {
            distace = 0.00d;
        }
        //规范显示的样式
        distace = DistanceUtil.getDistance(latLng, myLocation);//使用百度地图中距离计算的工具类
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String text = decimalFormat.format(distace / 1000) + "km";
        tvDistance.setText(text);
    }

}
