package com.zhuoxin.treasure.treasure;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhuoxin.treasure.MainActivity;
import com.zhuoxin.treasure.R;
import com.zhuoxin.treasure.commons.ActivityUtils;
import com.zhuoxin.treasure.treasure.list.TreasureListFrgment;
import com.zhuoxin.treasure.treasure.map.MapFragment;
import com.zhuoxin.treasure.user.UserPrefs;
import com.zhuoxin.treasure.user.account.AccountActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private ImageView ivIcon;

    private ActivityUtils activityUtils;
    private MapFragment mapFragment;
    private TreasureListFrgment listFrgment;
    private FragmentManager supportFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        //通过ID找到fragment
        supportFragmentManager = getSupportFragmentManager();
        mapFragment = (MapFragment) supportFragmentManager.findFragmentById(R.id.mapFragment);

        //已进入页面将宝藏数据的缓存清空
        TreasureRepo.getInstance().clear();

        activityUtils = new ActivityUtils(this);

        //处理toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //drawerLayout设置监听
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.syncState();//同步状态
        drawerLayout.addDrawerListener(toggle);

        //设置navigation每一条的点击监听
        navigationView.setNavigationItemSelectedListener(this);

        //设置头部
        ivIcon = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_usericon);
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 更换头像
                //跳转到个人信息的页面
                activityUtils.startActivity(AccountActivity.class);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

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

    //设置navigation每一条的选中监听
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_hide:

                //切换到埋藏宝藏的视图
                mapFragment.changeUIMode(2);

                break;
            case R.id.menu_my_list:
                break;
            case R.id.menu_help:
                break;
            case R.id.menu_logout:
                //清空登陆用户的数据
                UserPrefs.getInstance().clearUser();
                activityUtils.startActivity(MainActivity.class);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //----------------------------------------------------------------
    //准备选项菜单
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //选项菜单里面的菜单项
        MenuItem item = menu.findItem(R.id.action_toggle);
        //根据是否显示，设置不同图标
        if (listFrgment != null && listFrgment.isAdded()) {
            item.setIcon(R.drawable.ic_map);
        } else {
            item.setIcon(R.drawable.ic_view_list);
        }

        return true;
    }

    //创建选项菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    //选择某一个选项菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle:
                //切换视图
                showListFragment();
                //更新选项菜单的视图  invalidateOptionsMenu会出发准备视图（onPrepareOptionsMenu）
                invalidateOptionsMenu();
                break;
        }
        return true;
    }

    //展示列表的Fragment
    public void showListFragment() {
        //add：show和hide
        // TODO: 2017/1/13 需要修改

        //如果在展示
        if (listFrgment != null && listFrgment.isAdded()) {
            supportFragmentManager.popBackStack();
            supportFragmentManager.beginTransaction().remove(listFrgment).commit();
            return;
        }
        listFrgment = new TreasureListFrgment();
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, listFrgment)
                .addToBackStack(null)
                .commit();
    }

    //----------------------------------------------------------------
    //处理返回键的重写
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //MapFragment里面视图可以退出
            if (mapFragment.clickbackPressed()) {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(ivIcon);
    }
}
