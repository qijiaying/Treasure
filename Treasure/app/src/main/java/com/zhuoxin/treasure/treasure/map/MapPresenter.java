package com.zhuoxin.treasure.treasure.map;

import com.zhuoxin.treasure.net.NetClient;
import com.zhuoxin.treasure.treasure.Area;
import com.zhuoxin.treasure.treasure.Treasure;
import com.zhuoxin.treasure.treasure.TreasureRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/10.
 */

//获取宝藏数据的业务类
public class MapPresenter {
    private MapMvpView mapMvpView;
    private Area area;

    public MapPresenter(MapMvpView mapMvpView) {
        this.mapMvpView = mapMvpView;
    }

    public void getTreasure(Area area) {

        if (TreasureRepo.getInstance().isCached(area)) {
            return;
        }
        this.area = area;
        Call<List<Treasure>> listCall = NetClient.getInstances().getTreasureApi().getTreasureInArea(area);
        listCall.enqueue(listCallback);
    }

    private Callback<List<Treasure>> listCallback = new Callback<List<Treasure>>() {
        //请求成功
        @Override
        public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {
            if (response.isSuccessful()) {
                List<Treasure> treasureList = response.body();
                if (treasureList == null) {
                    //提示，吐司
                    mapMvpView.showMessage("未知错误");
                    return;
                }
                //拿到数据给MapFragment，在地图上展示
                TreasureRepo.getInstance().addTreasure(treasureList);
                TreasureRepo.getInstance().cache(area);
                mapMvpView.SetData(treasureList);
            }
        }

        //请求失败
        @Override
        public void onFailure(Call<List<Treasure>> call, Throwable t) {
            //吐司
            mapMvpView.showMessage("请求失败" + t.getMessage());
        }
    };
}
