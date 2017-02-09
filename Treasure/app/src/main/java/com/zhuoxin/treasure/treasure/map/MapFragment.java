package com.zhuoxin.treasure.treasure.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zhuoxin.treasure.R;
import com.zhuoxin.treasure.commons.ActivityUtils;
import com.zhuoxin.treasure.custom.TreasureView;
import com.zhuoxin.treasure.treasure.Area;
import com.zhuoxin.treasure.treasure.Treasure;
import com.zhuoxin.treasure.treasure.TreasureRepo;
import com.zhuoxin.treasure.treasure.detail.TreasureDetailActivity;
import com.zhuoxin.treasure.treasure.hide.HideTreasureActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/1/4.
 */

//宝藏页面、地图的展示
public class MapFragment extends Fragment implements MapMvpView {
    @BindView(R.id.map_frame)
    FrameLayout mapFrame;
    @BindView(R.id.btn_HideHere)
    Button btnHideHere;
    @BindView(R.id.centerLayout)
    RelativeLayout centerLayout;
    @BindView(R.id.iv_scaleUp)
    ImageView ivScaleUp;
    @BindView(R.id.iv_scaleDown)
    ImageView ivScaleDown;
    @BindView(R.id.tv_located)
    TextView tvLocated;
    @BindView(R.id.tv_satellite)
    TextView tvSatellite;
    @BindView(R.id.tv_compass)
    TextView tvCompass;
    @BindView(R.id.tv_currentLocation)
    TextView tvCurrentLocation;
    @BindView(R.id.iv_toTreasureInfo)
    ImageView ivToTreasureInfo;
    @BindView(R.id.et_treasureTitle)
    EditText etTreasureTitle;
    @BindView(R.id.layout_bottom)
    FrameLayout layoutBottom;
    private BaiduMap baiduMap;
    private LocationClient locationClient;
    private static LatLng currentLocation;
    private LatLng currentStatus;
    private Marker currentMarker;
    private boolean isFirst = true;
    private MapView mapView;
    private ActivityUtils activityUtils;
    private MapPresenter mapPresenter;
    @BindView(R.id.treasureView)
    TreasureView treasureView;
    @BindView(R.id.hide_treasure)
    RelativeLayout hideTreasure;
    private GeoCoder geoCoder;
    private String currentAddr;
    private static String MyLocationAddr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container);
        ButterKnife.bind(this, view);
        activityUtils = new ActivityUtils(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        // 初始化百度地图
        initMapView();
        mapPresenter = new MapPresenter(this);
        //初始化定位相关
        initLocation();

        //地理编码的初始化相关
        initGeoCoder();

    }

    //地理编码的初始化相关
    private void initGeoCoder() {
        //初始化：创建出一个地理编码的查询对象
        geoCoder = GeoCoder.newInstance();
        //设置结果的监听,地理编码的监听
        geoCoder.setOnGetGeoCodeResultListener(geoCoderResultListener);
    }

    //地理编码的监听
    private OnGetGeoCoderResultListener geoCoderResultListener = new OnGetGeoCoderResultListener() {
        //得到地理编码的结果:地址->经纬度
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        //得到反地理编码的结果：经纬度->地址
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            //当前是拿到结果以后给标题录入的卡片上的文本设置上
            if (reverseGeoCodeResult == null) {
                currentAddr = "未知的位置";
                return;
            }

            //拿到反向地理编码得到的位置信息
            currentAddr = reverseGeoCodeResult.getAddress();

            //将地址信息给tvCurrentLocation设置上
            tvCurrentLocation.setText(currentAddr);
        }
    };

    //初始化定位相关
    private void initLocation() {
        //前置：激活定位图层
        baiduMap.setMyLocationEnabled(true);

//      第一步，初始化LocationClient类:LocationClient类必须在主线程中声明，需要Context类型的参数
        locationClient = new LocationClient(getContext().getApplicationContext());

//      第二步，配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//开启GPS
        option.setCoorType("bd09ll");//百度坐标类型，默认gcj02，会有偏差，设置为百度坐标类型，将无偏差
        option.setIsNeedAddress(true);//需要地址信息
        locationClient.setLocOption(option);

//      第三步，实现BDLocationListener接口
        locationClient.registerLocationListener(bdLocationListener);

//      第四步，开始定位
        locationClient.start();

    }

    //定位监听
    private BDLocationListener bdLocationListener = new BDLocationListener() {
        //获取定位结果
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                locationClient.requestLocation();
                return;
            }
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            //定位的经纬度的类
            currentLocation = new LatLng(latitude, longitude);

            //定位的地址
            MyLocationAddr = bdLocation.getAddrStr();

            Log.i("TAG", "定位的位置" + MyLocationAddr + ".经纬度" + latitude + "," + longitude);

            //设置定位图层展示的数据
            MyLocationData data = new MyLocationData.Builder()
                    //定位数据展示的经纬度
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)//定位精度大小
                    .build();

            //定位数据展示到地图上
            baiduMap.setMyLocationData(data);

            //移动定位的地方，在地图上展示定位的信息、位置
            if (isFirst) {
                moveToLocation();
                isFirst = false;
            }

        }
    };

    // 初始化百度地图
    private void initMapView() {
        // 设置地图状态
        MapStatus mapStatus = new MapStatus.Builder()
                .zoom(19)// 3--21级别：默认12
                .overlook(0)// 俯仰角度
                .rotate(0)// 旋转角度
                .build();

        // 设置百度地图的设置信息
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(true)// 指南针
                .zoomGesturesEnabled(true)// 缩放手势
                .scaleControlEnabled(false)// 比例尺
                .zoomControlsEnabled(false);// 缩放的控件


        // 创建MapView
        mapView = new MapView(getContext(), options);

        // 在布局上添加地图控件：0，代表第一位
        mapFrame.addView(mapView, 0);

        // 拿到地图的操作类(控制器：操作地图时使用)
        baiduMap = mapView.getMap();

        //设置地图状态变化的监听
        baiduMap.setOnMapStatusChangeListener(statusChangeListener);

        //设置地图标注物的点击监听
        baiduMap.setOnMarkerClickListener(markerClickListener);
    }

    //标注物的点击监听
    private BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (currentMarker != null) {
                if (currentMarker != marker) {
                    currentMarker.setVisible(true);
                }
                currentMarker.setVisible(true);
            }
            currentMarker = marker;
            //点击marker展示InfoWindow,当前不可见
            currentMarker.setVisible(false);

            //创建一个InfoWindow
            InfoWindow infoWindow = new InfoWindow(dot_expand, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick() {
                    /*//currentMarker的点击监听
                    if (currentMarker != null) {
                        currentMarker.setVisible(true);
                    }
                    //隐藏InfoWindow
                    baiduMap.hideInfoWindow();*/

                    // 切换回普通的视图
                    changeUIMode(UI_MODE_NORMAL);

                }
            });
            // 地图上显示一个InfoWindow
            baiduMap.showInfoWindow(infoWindow);

            //宝藏信息的取出和展示
            int id = marker.getExtraInfo().getInt("id");
            Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
            treasureView.bindTreasure(treasure);

            //切换宝藏选中视图
            changeUIMode(UI_MODE_SELECT);
            return false;
        }
    };

    //地图状态变化的监听
    private BaiduMap.OnMapStatusChangeListener statusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        //变化前
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        //变化中
        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        //变化结束后
        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            //当前地图的位置
            LatLng target = mapStatus.target;
            if (target != MapFragment.this.currentStatus) {

                /*//练习添加覆盖物的功能
                    addMarker(target);*/
                //请求数据
                //地图状态发生变化以后
                updataMapArea();

                //当埋藏宝藏是发起
                if (UIMode == UI_MODE_HIDE) {

                    //设置反地理编码的位置信息
                    ReverseGeoCodeOption option = new ReverseGeoCodeOption();
                    option.location(target);

                    //发起反地理编码
                    geoCoder.reverseGeoCode(option);
                }

                MapFragment.this.currentStatus = target;
            }
        }
    };


    //卫星视图和普通视图的切换
    @OnClick(R.id.tv_satellite)
    public void switchMapType() {
        int mapType = baiduMap.getMapType();//获取当前的地图类型
        //切换类型
        mapType = (mapType == BaiduMap.MAP_TYPE_NORMAL) ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL;
        //卫星和普通文字的显示
        String msg = mapType == BaiduMap.MAP_TYPE_NORMAL ? "卫星" : "普通";
        baiduMap.setMapType(mapType);
        tvSatellite.setText(msg);
    }

    //指南针
    @OnClick(R.id.tv_compass)
    public void switchCompass() {
        //指南针是否显示
        boolean compassEnabled = baiduMap.getUiSettings().isCompassEnabled();
        baiduMap.getUiSettings().setCompassEnabled(!compassEnabled);
    }

    //缩放
    @OnClick({R.id.iv_scaleDown, R.id.iv_scaleUp})
    public void scaleMap(View view) {
        switch (view.getId()) {
            case R.id.iv_scaleDown:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
            case R.id.iv_scaleUp:
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
        }
    }

    //点击移动到定位处
    @OnClick(R.id.tv_located)
    public void moveToLocation() {

        //地图状态的设置：设置到定位的地方
        MapStatus mapStatus = new MapStatus.Builder()
                .target(currentLocation)//定位位置
                .rotate(0)
                .overlook(0)
                .zoom(18)
                .build();

        //更新状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);

        //更新展示的地图状态
        baiduMap.animateMapStatus(update);
    }

    //宝藏显示的卡片点击事件
    @OnClick(R.id.treasureView)
    public void clickTreasureView() {
        //跳转到详情页面,拿到当前的Marker的宝藏，并传递过去
        int id = currentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
        TreasureDetailActivity.open(getContext(), treasure);
    }

    //点击宝藏录入的标题卡片，跳转宝藏埋藏的详细页面
    @OnClick(R.id.hide_treasure)
    public void hideTreasure() {
        String title = etTreasureTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            activityUtils.showToast("请输入宝藏标题");
            return;
        }
        //跳转到埋藏宝藏的详细页面
        LatLng latLng = baiduMap.getMapStatus().target;
        HideTreasureActivity.open(getContext(), title, currentAddr, latLng, 0);
    }


    //根据位置变化，区域也发生变化
    private void updataMapArea() {

        //当前地图的状态
        MapStatus mapStatus = baiduMap.getMapStatus();

        //当前地图的经纬度
        double longitude = mapStatus.target.longitude;
        double latitude = mapStatus.target.latitude;

        //根据地图的经纬度拿到的区域
        Area area = new Area();
        area.setMaxLat(Math.ceil(latitude));//向上取整
        area.setMaxLng(Math.ceil(longitude));
        area.setMinLat(Math.floor(latitude));//向下取整
        area.setMinLng(Math.floor(longitude));

        //根据区域获取宝藏数据

        mapPresenter.getTreasure(area);

    }


    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_dot);
    private BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);

    //添加覆盖物
    private void addMarker(LatLng latLng, int treasureId) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);//覆盖物的位置
        options.icon(dot);//覆盖物图标
        options.anchor(0.5f, 0.5f);//覆盖物锚点位置，居中

        //添加宝藏Id
        Bundle bundle = new Bundle();
        bundle.putInt("id", treasureId);
        options.extraInfo(bundle);
        //添加覆盖物
        baiduMap.addOverlay(options);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public static LatLng getMyLocation() {
        return currentLocation;
    }

    public static String getMyLocationAddr() {
        return MyLocationAddr;
    }

    private static final int UI_MODE_NORMAL = 0;//普通视图
    private static final int UI_MODE_SELECT = 1;//宝藏选中视图
    private static final int UI_MODE_HIDE = 2;//埋藏宝藏视图

    private static int UIMode = UI_MODE_NORMAL;

    //所有视图变化切换的方法，根据布局控件切换试图（marker，infowindow）
    public void changeUIMode(int uiMode) {
        if (UIMode == uiMode) return;
        UIMode = uiMode;
        switch (uiMode) {
            case UI_MODE_NORMAL:
                if (currentMarker != null) {
                    currentMarker.setVisible(true);
                }
                baiduMap.hideInfoWindow();
                layoutBottom.setVisibility(View.GONE);
                centerLayout.setVisibility(View.GONE);
                break;

            //宝藏选中
            case UI_MODE_SELECT:
                layoutBottom.setVisibility(View.VISIBLE);
                treasureView.setVisibility(View.VISIBLE);
                centerLayout.setVisibility(View.GONE);
                hideTreasure.setVisibility(View.GONE);
                break;

            //宝藏埋藏试图
            case UI_MODE_HIDE:
                if (currentMarker != null) {
                    currentMarker.setVisible(true);
                }
                baiduMap.hideInfoWindow();
                centerLayout.setVisibility(View.VISIBLE);
                layoutBottom.setVisibility(View.GONE);
                btnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutBottom.setVisibility(View.VISIBLE);
                        treasureView.setVisibility(View.GONE);
                        hideTreasure.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }

    //------------------------------------------实现类方法------------------------------------------
    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void SetData(List<Treasure> list) {
        //再次网络请求拿到数据添加覆盖物，清理之前的覆盖物
        baiduMap.clear();//清空地图上的覆盖物和infoWindow

        for (Treasure treasure : list) {

            LatLng latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());
            addMarker(latLng, treasure.getId());
        }
    }

    //对外提供一个方法，可以退出
    public boolean clickbackPressed() {
        //如果不是普通视图，切换普通视图
        if (UIMode != UI_MODE_NORMAL) {
            changeUIMode(UI_MODE_NORMAL);
            return false;
        }
        //是普通视图就可以告诉HomeACtivity可以退出
        return true;
    }

}
