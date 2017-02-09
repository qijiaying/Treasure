package com.zhuoxin.treasure;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuoxin.treasure.commons.ActivityUtils;

import java.io.FileDescriptor;
import java.io.IOException;


/**
 * 主要进行视频播放
 */
public class MainMp4Fragment extends Fragment implements TextureView.SurfaceTextureListener {
    private TextureView textureView;
    private ActivityUtils activityUtils;
    private MediaPlayer mediaPlayer;

    /**
     * 1、使用MediaPlayer进行视频播放
     * 2、展示视频播放：surfaceView和TextureView
     * 3、使用TextureView:需要surfaceTexture：使用这个来渲染，呈现
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //fragment全屏显示播放视频的控件
        textureView = new TextureView(getContext());
        return textureView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置监听：因为播放内容需要surfaceTexture，所以需要设置一个坚硬，看surfaceTexture有没有变化
        activityUtils = new ActivityUtils(this);
        textureView.setSurfaceTextureListener(this);
    }

    //------------------------------start------------------------------
    //确实准备好了，可以展示内容
    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        /**
         * 1、播放：找到播放的资源
         * 2、可以播放了：MediaPlayer来进行播放
         *    创建、设置播放的资源、设置播放的同异步等
         *    MediaPlayer有没有准备好：好则直接开始播放
         * 3、页面销毁了：MediaPlayer资源释放
         */

        try {
            //打开播放的资源文件
            AssetFileDescriptor openFd = getContext().getAssets().openFd("welcome.mp4");
            //拿到MediaPlayer需要的资源类型
            FileDescriptor fileDescriptor = openFd.getFileDescriptor();
            mediaPlayer = new MediaPlayer();
            //设置播放的资源给MediaPlayer
            mediaPlayer.setDataSource(fileDescriptor, openFd.getStartOffset(), openFd.getLength());
            mediaPlayer.prepareAsync();//异步准备
            //设置监听，是否准备好
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                //准备好，可以播放
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Surface mSurface = new Surface(surface);
                    mediaPlayer.setSurface(mSurface);
                    mediaPlayer.setLooping(true);//循环播放
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            activityUtils.showToast("媒体文件播放失败");
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    //------------------------------end------------------------------


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
