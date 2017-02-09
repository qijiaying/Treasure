package com.zhuoxin.treasure.treasure.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuoxin.treasure.R;
import com.zhuoxin.treasure.treasure.TreasureRepo;

/**
 * Created by Administrator on 2017/1/12.
 */

//宝藏列表
public class TreasureListFrgment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //创建recycleView
        recyclerView = new RecyclerView(container.getContext());
        //一定要设置以哪种形式展示，设置布局管理器
        //LinearLayoutManager,GirdLayoutManager,
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //设置动画效果
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置背景
        recyclerView.setBackgroundResource(R.mipmap.scale_bg);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //设置适配器
        TreasureListAdapter adapter = new TreasureListAdapter();
        recyclerView.setAdapter(adapter);

        //数据从缓存的数据中拿到
        adapter.addItemData(TreasureRepo.getInstance().getTreasure());
    }
}
