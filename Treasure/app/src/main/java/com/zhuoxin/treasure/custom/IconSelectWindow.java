package com.zhuoxin.treasure.custom;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.zhuoxin.treasure.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/1/12.
 */

//用户头像点击弹出的视图（视图窗口：从相册、相位、取消）
public class IconSelectWindow extends PopupWindow {
    //接口：利用接口回调的方式实现相册相机的实践
    public interface Listener {
        //相册
        void toGallery();

        //相机
        void toCamera();
    }

    private Activity activity;
    private Listener listener;

    //构造方法填充布局
    public IconSelectWindow(@NonNull Activity activity, Listener listener) {
        super(activity.getLayoutInflater().inflate(R.layout.window_select_icon, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this, getContentView());

        this.activity = activity;
        this.listener = listener;

        setFocusable(true);//设置焦点
        //一定要设置背景
        setBackgroundDrawable(new BitmapDrawable());
    }

    //对外提供一个展示的方法
    public void show() {
        showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @OnClick({R.id.btn_gallery, R.id.btn_camera, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            //相册
            case R.id.btn_gallery:
                listener.toGallery();
                break;

            //相机
            case R.id.btn_camera:
                listener.toCamera();
                break;

            //取消
            case R.id.btn_cancel:

                break;
        }
        dismiss();
    }
}
