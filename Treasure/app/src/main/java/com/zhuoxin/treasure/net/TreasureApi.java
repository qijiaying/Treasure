package com.zhuoxin.treasure.net;

import com.zhuoxin.treasure.treasure.Area;
import com.zhuoxin.treasure.treasure.Treasure;
import com.zhuoxin.treasure.treasure.detail.TreasureDetail;
import com.zhuoxin.treasure.treasure.detail.TreasureDetailResult;
import com.zhuoxin.treasure.treasure.hide.HideTreasure;
import com.zhuoxin.treasure.treasure.hide.HideTreasureResult;
import com.zhuoxin.treasure.user.User;
import com.zhuoxin.treasure.user.account.Update;
import com.zhuoxin.treasure.user.account.UpdateResult;
import com.zhuoxin.treasure.user.account.UploadResult;
import com.zhuoxin.treasure.user.login.LoginResult;
import com.zhuoxin.treasure.user.register.RegisterResult;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Administrator on 2017/1/9.
 */

//请求构建的接口
public interface TreasureApi {

    //登陆的请求
    @POST("/Handler/UserHandler.ashx?action=login")
    Call<LoginResult> login(@Body User user);

    //注册的请求
    @POST("/Handler/UserHandler.ashx?action=register")
    Call<RegisterResult> register(@Body User user);

    //获取区域内的宝藏数据请求
    @POST("/Handler/TreasureHandler.ashx?action=show")
    Call<List<Treasure>> getTreasureInArea(@Body Area area);

    //宝藏详情的请求
    @POST("/Handler/TreasureHandler.ashx?action=tdetails")
    Call<List<TreasureDetailResult>> getTreasureDetail(@Body TreasureDetail treasureDetail);

    //埋藏宝藏的数据请求
    @POST("/Handler/TreasureHandler.ashx?action=hide")
    Call<HideTreasureResult> hideTreasure(@Body HideTreasure hideTreasure);

    /**
     * 关于头像上传的：文件
     *
     * @param part
     * @return
     */
    //关于头像上传的两种方式
    @Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part("file\";filename=\"image.png\"") RequestBody body);

    @Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part MultipartBody.Part part);

    @POST("/Handler/UserHandler.ashx?action=update")
    Call<UpdateResult> update(@Body Update update);
}
