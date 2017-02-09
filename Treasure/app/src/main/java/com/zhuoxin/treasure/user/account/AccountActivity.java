package com.zhuoxin.treasure.user.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pkmmte.view.CircularImageView;
import com.zhuoxin.treasure.R;
import com.zhuoxin.treasure.commons.ActivityUtils;
import com.zhuoxin.treasure.custom.IconSelectWindow;
import com.zhuoxin.treasure.net.NetClient;
import com.zhuoxin.treasure.user.UserPrefs;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AccountActivity extends AppCompatActivity implements AccountView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_userIcon)
    CircularImageView ivIcon;


    private IconSelectWindow mSelectWindow;
    private ProgressDialog dialog;
    private ActivityUtils activityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        //toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("个人信息");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //加载头像
        //更新侧滑上头像信息
        String photo = UserPrefs.getInstance().getPhoto();
        if (photo != null) {
            //加载头像
            Glide.with(this)
                    .load(photo)
                    .error(R.mipmap.user_icon)//设置错误占位图
                    .placeholder(R.mipmap.user_icon)//设置占位图
                    .dontAnimate()
                    .into(ivIcon);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.iv_userIcon)
    public void onClick() {
        // 点击头像会弹出一个选择的PopupWindow
        if (mSelectWindow == null) {
            mSelectWindow = new IconSelectWindow(this, listener);
        }
        if (mSelectWindow.isShowing()) {
            mSelectWindow.dismiss();
            return;
        }
        mSelectWindow.show();
    }

    /**
     * 1. 依赖：compile 'org.hybridsquad.android.photocropper:library:2.1.0'
     * 清单合并的问题：aar
     * 2. 使用：
     * 1. 拿到结果处理
     * 2. 处理的回调
     * 3. 分别调用到相册到相机：之前一定要清理上次剪切的图片的缓存
     */


    //用第三方库，到相册，到相机，剪切的功能 photoCropper
    private IconSelectWindow.Listener listener = new IconSelectWindow.Listener() {
        @Override
        public void toGallery() {
            //清除上一次剪切的图片缓存
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            //到相册的方法
            Intent intent = CropHelper.buildCropFromGalleryIntent(cropHandler.getCropParams());
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        }

        @Override
        public void toCamera() {
            //清除上一次剪切的图片缓存
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            //到相机的方法
            Intent intent = CropHelper.buildCaptureIntent(cropHandler.getCropParams().uri);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
        }
    };

    //图片处理
    private CropHandler cropHandler = new CropHandler() {
        //图片剪切完以后的结果
        @Override
        public void onPhotoCropped(Uri uri) {

            //拿到剪切完成以后的图片
            File file = new File(uri.getPath());
            //要惊醒将图片上传到服务器：头像上传、更新
            new AccountPresenter(AccountActivity.this).uploadPhoto(file);

        }

        //剪切取消
        @Override
        public void onCropCancel() {
            Toast.makeText(AccountActivity.this, "取消", Toast.LENGTH_SHORT).show();
        }

        //剪切失败
        @Override
        public void onCropFailed(String message) {
            Toast.makeText(AccountActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }

        //剪切的选项设置:Uri(剪切图片保存的路径)
        @Override
        public CropParams getCropParams() {
            CropParams cropParams = new CropParams();
            return cropParams;
        }

        @Override
        public Activity getContext() {
            return AccountActivity.this;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理得到的结果
        CropHelper.handleResult(cropHandler, requestCode, resultCode, data);

    }


    @Override
    protected void onDestroy() {
        if (cropHandler.getCropParams() != null)
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
        super.onDestroy();
    }

    //---------------------------------------------视图接口的方法------------------------------------
    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, "头像上传", "正在上传中，请稍候~");
    }

    @Override
    public void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void updataPhoto(String photoUrl) {
        // 更新页面的头像
        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .error(R.mipmap.user_icon)
                    .placeholder(R.mipmap.user_icon)
                    .dontAnimate()
                    .into(ivIcon);
        }
    }
}
