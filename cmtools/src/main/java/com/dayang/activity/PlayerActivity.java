package com.dayang.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.dayang.cmtools.R;
import com.dayang.common.Canstant;
import com.dayang.common.CommonUtil;
import com.dayang.common.ExitAppUtils;
import com.dayang.view.CustomMediaController;
import com.dayang.view.CustomMediaController.onFullScreenListener;
import com.dayang.view.CustomVideoView;

/**
 * 该页面用于实现单个视频的播放
 * @author renyuwei
 *
 */
public class PlayerActivity extends Activity{
	private static final String TAG =  Canstant.TAG;
	private CustomVideoView vv_player;// 自定义的视频播放控件
	private CustomMediaController mediaController;// 自定义的视频播放控件控制器
	private RelativeLayout rl_load;// 视频缓冲层
	private RelativeLayout iv_error;// 视频错误层
	
	/**
	 * 创建播放器页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_player);
		ExitAppUtils.getInstance().addActivity(this);
		initView(); 
	}
	/**
	 * 初始化控件并添加相应的事件
	 */
	private void initView() {
		//播放测试
		Intent intent = getIntent();
		String path = intent.getExtras().getString("path");
		if(TextUtils.isEmpty(path)){
			return;
		}
		Log.i(TAG, "initView: "+path);
		vv_player = (CustomVideoView) findViewById(R.id.vv_player);
		rl_load = (RelativeLayout) findViewById(R.id.rl_load);
		iv_error = (RelativeLayout) findViewById(R.id.iv_error);

		
		vv_player.setVisibility(View.VISIBLE);// 显示视频控件
		iv_error.setVisibility(View.GONE);// 开始不显示错误层
		rl_load.setVisibility(View.VISIBLE);// 显示视频加载
		Window window = getWindow();
		mediaController = new CustomMediaController(PlayerActivity.this,
				true,window);// 绑定控制器
		mediaController.setFullScreenEnable(true);
		mediaController.setOnFullScreenListener(new onFullScreenListener() {
			@Override
			public void onFullScreen(View v) {
				// 横竖屏切换时，会重走ListFragment的生命周期，缓存任务类型，为重走生命周期刷新数据准备
				if (CommonUtil.isScreenOriatationPortrait(PlayerActivity.this)) {// 当屏幕是竖屏时
																					// 点击后变横屏
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置当前activity为横屏
				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
				}
			}
		});
		vv_player.setMediaController(mediaController);// 视频准备好监听
		vv_player.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.i(TAG, "onPrepared: ");
				rl_load.setVisibility(View.GONE);// 取消视频加载
				vv_player.start();
			}
		});
		// 视频播放错误监听
		vv_player.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				iv_error.setVisibility(View.VISIBLE);// 显示错误层
				vv_player.setVisibility(View.GONE);// 隐藏视频空间
				rl_load.setVisibility(View.GONE);// 取消视频加载
				return false;
			}
		});
		vv_player.setVideoPath(path);
		Log.i(TAG, "init完毕");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (vv_player != null) {
			vv_player.stopPlayback();// 界面销毁时强制挂载视频
		}
	}
	
	/**
	 * 该方法用于界面发生变化时，不让activity进行销毁操作，从而保持原先的上下文状态
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.e("debug", "configuration");
		super.onConfigurationChanged(newConfig);
	}
}
