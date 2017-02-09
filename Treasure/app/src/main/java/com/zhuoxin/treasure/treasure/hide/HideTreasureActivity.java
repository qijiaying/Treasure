package com.zhuoxin.treasure.treasure.hide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.baidu.mapapi.model.LatLng;
import com.zhuoxin.treasure.R;
import com.zhuoxin.treasure.commons.ActivityUtils;
import com.zhuoxin.treasure.treasure.TreasureRepo;
import com.zhuoxin.treasure.user.UserPrefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HideTreasureActivity extends AppCompatActivity implements HideTreasureView {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_LOCATION = "key_location";
    private static final String KEY_LATLNG = "key_latlng";
    private static final String KEY_ALTITUDE = "key_altitude";
    private ActivityUtils activityUtils;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_description)
    EditText etDescription;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_treasure);

    }

    //对外提供跳转的方法
    public static void open(Context context, String title, String address, LatLng latLng, double altitude) {
        Intent intent = new Intent(context, HideTreasureActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_LOCATION, address);
        intent.putExtra(KEY_LATLNG, latLng);
        intent.putExtra(KEY_ALTITUDE, altitude);
        context.startActivity(intent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);

        //toobar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(KEY_TITLE));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //处理toolbar的返回箭头
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //点击的时候上传宝藏到服务器
    @OnClick(R.id.hide_send)
    public void onClick() {
        //网络请求的数据上传
        Intent intent = getIntent();
        String title = intent.getStringExtra(KEY_TITLE);
        String address = intent.getStringExtra(KEY_LOCATION);
        double altitude = intent.getDoubleExtra(KEY_ALTITUDE, 0);
        LatLng latlng = intent.getParcelableExtra(KEY_LATLNG);
        int tokenid = UserPrefs.getInstance().getTokenid();
        String string = etDescription.getText().toString();

        //上传的数据实体类
        HideTreasure hideTreasure = new HideTreasure();
        hideTreasure.setTitle(title);
        hideTreasure.setAltitude(altitude);
        hideTreasure.setDescription(string);
        hideTreasure.setLatitude(latlng.latitude);
        hideTreasure.setLongitude(latlng.longitude);
        hideTreasure.setLocation(address);
        hideTreasure.setTokenId(tokenid);

        new HideTreasurePresenter(this).hideTreasure(hideTreasure);
    }

    //---------------------------------------视图接口中的方法--------------------
    @Override
    public void showMessage(String msg) {
        activityUtils.showToast(msg);
    }

    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, "宝藏上传", "宝藏正在上传中，请稍候~");
    }

    @Override
    public void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void navigationToHome() {
        finish();

        //清除缓存，为了回到之前页面重新请求数据，而不是缓存中那
        TreasureRepo.getInstance().clear();
    }
}
