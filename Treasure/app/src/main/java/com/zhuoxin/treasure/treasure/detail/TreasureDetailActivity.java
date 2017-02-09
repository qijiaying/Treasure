package com.zhuoxin.treasure.treasure.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.zhuoxin.treasure.R;
import com.zhuoxin.treasure.commons.ActivityUtils;
import com.zhuoxin.treasure.custom.TreasureView;
import com.zhuoxin.treasure.treasure.Treasure;
import com.zhuoxin.treasure.treasure.map.MapFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TreasureDetailActivity extends AppCompatActivity implements TreasureDetailView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.detail_treasure)
    TreasureView treasureView;
    @BindView(R.id.tv_detail_description)
    TextView tvDetail;

    private static final String KEY_TREASURE = "key_treasure";
    private Treasure treasure;
    private ActivityUtils activityUtils;
    private TreasureDetailPresenter treasureDetailPresenter = new TreasureDetailPresenter(this);
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_detail);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        getIntent().getSerializableExtra(KEY_TREASURE);
    }

    /**
     * 对外提供一个跳转到本页面的方法
     * 1、规范传递的数据：需要数据必须传入
     * 2、Key简练
     */
    public static void open(Context context, Treasure treasure) {
        Intent intent = new Intent(context, TreasureDetailActivity.class);
        intent.putExtra(KEY_TREASURE, treasure);
        context.startActivity(intent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        //拿到传递过来的数据
        treasure = (Treasure) getIntent().getSerializableExtra(KEY_TREASURE);

        //toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(treasure.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //地图和宝藏的展示
        initMapView();

        treasureView.bindTreasure(treasure);

        //进行网络获取得到宝物详情
        TreasureDetail treasureDetail = new TreasureDetail(treasure.getId());
        treasureDetailPresenter.getTreasureDetail(treasureDetail);

    }

    //地图和宝藏的展示
    private void initMapView() {

        LatLng latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());

        //地图状态
        MapStatus mapStatus = new MapStatus.Builder()
                .target(latLng)
                .overlook(-20)//0~(-45°)
                .zoom(18)
                .rotate(0)
                .build();

        //设置地图无法进行操作
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(false)
                .scrollGesturesEnabled(false)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(false)
                .scaleControlEnabled(false)
                .rotateGesturesEnabled(false);

        MapView mapView = new MapView(this, options);
        //填充到布局中
        frameLayout.addView(mapView);

        BaiduMap map = mapView.getMap();

        //添加地图覆盖物
        BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .icon(dot_expand)
                .anchor(0.5f, 0.5f);
        map.addOverlay(marker);

    }

    //处理toolbar上的返回箭头
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.iv_navigation)
    public void showPopupMenu(View view) {
        //展示一个PopupMenu
        //创建PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);

        //菜单布局填充
        popupMenu.inflate(R.menu.menu_navigation);

        //菜单上的点击监听
        popupMenu.setOnMenuItemClickListener(menuItemClickListener);

        //显示PopupMenu
        popupMenu.show();
    }

    //设置菜单项的点击监听
    private PopupMenu.OnMenuItemClickListener menuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            //起点和终点的信息拿到
            //拿到我们自己的位置和地址
            LatLng start = MapFragment.getMyLocation();
            String startAddr = MapFragment.getMyLocationAddr();

            //宝藏的位置和地址
            LatLng end = new LatLng(treasure.getLatitude(), treasure.getLongitude());
            String endAddr = treasure.getLocation();

            switch (item.getItemId()) {
                case R.id.walking_navi:
                    //开始步行导航
                    startWalkingNavi(start, startAddr, end, endAddr);
                    break;
                case R.id.biking_navi:
                    //开始骑行导航
                    startBikingNavi(start, startAddr, end, endAddr);
                    break;
            }
            return false;
        }
    };

    //开始步行导航
    public void startWalkingNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {

        //导航的起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);

        //开启导航
        boolean walkNavi = BaiduMapNavigation.openBaiduMapWalkNavi(option, this);
        //未开启成功
        if (!walkNavi) {
            showDialog();
            //开启网页导航
            //startWebNavi(startPoint, startAddr, endPoint, endAddr);
        }
    }

    //开始骑行导航
    public void startBikingNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {
        //导航的起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);

        //开启导航
        boolean walkNavi = BaiduMapNavigation.openBaiduMapBikeNavi(option, this);
        //未开启成功
        if (!walkNavi) {
            showDialog();
            //开启网页导航
            //startWebNavi(startPoint, startAddr, endPoint, endAddr);
        }
    }

    //开启网页导航
    private void startWebNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {
        //导航的起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);

        //开启导航
        BaiduMapNavigation.openWebBaiduMapNavi(option, this);
    }

    //提示未安装百度地图
    public void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您未安装百度地图的App或者版本过低，是否安装？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OpenClientUtil.getLatestBaiduMapApp(TreasureDetailActivity.this);
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    //------------------------------------------------视图接口里需要实现的方法--------------------------
    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void setData(List<TreasureDetailResult> resultList) {
        if (resultList.size() > 0) {
            TreasureDetailResult result = resultList.get(0);
            tvDetail.setText(result.description);
            return;
        }
        tvDetail.setText("当前宝藏无详细信息");
    }
}
