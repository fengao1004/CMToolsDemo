package com.dayang.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dayang.cmtools.R;
import com.dayang.common.Constants;
import com.dayang.common.ExitAppUtils;
import com.dayang.dyhsplitmuxerffmpeg.DYHSplitMuxerFFmpeg;
import com.dayang.dyhsplitmuxerffmpeg.DYHSplitMuxerFFmpegSourceInfo;
import com.dayang.dyhsplitmuxerffmpeg.DYHSplitMuxerFFmpegStatusInfo;
import com.dayang.view.CustomEditVideoView;
import com.dayang.view.MyProgress;
import com.dayang.view.MyProgress.MyTouchlistener;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

/**
 * 该页面用于实现单个视频的编辑
 *
 * @author renyuwei
 *
 */
public class PreviewEditActivity extends Activity {
	private CustomEditVideoView vv_player;// 自定义的视频播放控件
	// private CustomEditMediaController mediaController;// 自定义的视频播放控件控制器
	private RelativeLayout rl_load;// 视频缓冲层
	private RelativeLayout iv_error;// 视频错误层
	private RelativeLayout rl_work;// 剪切处理层

	private TextView canceledit;// 取消记录时间段
	private TextView saveedit;// 保存记录时间段

	private FrameLayout previewedit;
	private double starttime;
	private double endtime;
	private boolean isPlaying = false;
	private boolean hasListener = false;

	private DYHSplitMuxerFFmpeg splitMuxer = null;
	private MyProgress myprogress = null;
	private TextView tv_timeshow = null;
	private TextView tv_timestart = null;
	private TextView tv_timeend = null;
	private ImageView iv_play = null;
	private int totaltime;// 单位毫秒
	private boolean onPlaying;// 出页面时关掉死循环

	private DYHSplitMuxerFFmpegSourceInfo sourceInfo = null;

	private File splitFile = null;
	private String indexNO = null;

	private String clipVideoName = null;
	StringBuilder mFormatBuilder;
	Formatter mFormatter;
	private Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			int time = msg.what;
			String stime = stringForTime(time);
			tv_timeshow.setText(stime + "/" + stringForTime(totaltime));
			progressListener();

		}
	};
	private String stotaltime;

	/**
	 * 创建播放器页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_player_edit);
		ExitAppUtils.getInstance().addActivity(this);
		initView();
	}

	/**
	 * 初始化控件并添加相应的事件
	 */
	@SuppressLint("NewApi")
	private void initView() {
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
		Intent intent = getIntent();
		String path = intent.getExtras().getString("path");
		indexNO = intent.getExtras().getString("indexNO");
		if (TextUtils.isEmpty(path)) {
			return;
		}

		splitFile = new File(path);
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(path);
		String duration = mmr
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		totaltime = Integer.parseInt(duration);
		stotaltime = stringForTime(totaltime);
		vv_player = (CustomEditVideoView) findViewById(R.id.vvedit_player);

		rl_load = (RelativeLayout) findViewById(R.id.rledit_load);
		iv_error = (RelativeLayout) findViewById(R.id.ivedit_error);
		previewedit = (FrameLayout) findViewById(R.id.previewedit_edit);
		canceledit = (TextView) findViewById(R.id.canceledit_edit);// 取消
		saveedit = (TextView) findViewById(R.id.saveedit_edit);// 保存
		tv_timeshow = (TextView) findViewById(R.id.tv_timeshow);// 时间显示
		iv_play = (ImageView) findViewById(R.id.tv_play);// 播放暂停按钮
		rl_work = (RelativeLayout) findViewById(R.id.rv_working);// 剪切操作时得提示
		tv_timestart = (TextView) findViewById(R.id.tv_timestart);// 开始时间
		tv_timeend = (TextView) findViewById(R.id.tv_timeend);// 结束时间
		myprogress = (MyProgress) findViewById(R.id.myprogress);// 自定义的用来截取时间的进度条
		myprogress.setMax(totaltime); // 初始化进度条
		starttime = 0;
		endtime = totaltime;
		vv_player.setVisibility(View.VISIBLE);// 显示视频控件
		iv_error.setVisibility(View.GONE);// 开始不显示错误层
		rl_load.setVisibility(View.VISIBLE);// 显示视频加载
		tv_timeend.setText("出点 " + stringForTime(totaltime));
		iv_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				myPlay();
			}
		});
		myprogress.setStartOnTouchListener(new MyTouchlistener() {

			@Override
			public void ProgressTouch(int progeress) {
				vv_player.seekTo(progeress);
				myprogress.setProgress(progeress);
				starttime = progeress;
				String stringForTime = stringForTime(progeress);
				tv_timestart.setText("入点 " + stringForTime);
			}
		});
		myprogress.setProgressOnTouchListener(new MyTouchlistener() {

			@Override
			public void ProgressTouch(int progeress) {
				vv_player.seekTo(progeress);
			}
		});
		myprogress.setEndOnTouchListener(new MyTouchlistener() {

			@Override
			public void ProgressTouch(int progeress) {
				endtime = progeress;
				String stringForTime = stringForTime(progeress);
				tv_timeend.setText("出点 " + stringForTime);
			}
		});
		vv_player.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				rl_load.setVisibility(View.GONE);// 取消视频加载
				vv_player.start();
				SystemClock.sleep(200);
				vv_player.pause();
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
		// 取消编辑
		canceledit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentback = new Intent();
				intentback.putExtra("status", "2");
				setResult(Constants.VIDEOEDIT_REQUEST, intentback);
				finish();

			}
		});
		saveedit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				splitMuxer = new DYHSplitMuxerFFmpeg();
				splitMuxer.init();
				sourceInfo = new DYHSplitMuxerFFmpegSourceInfo();
				sourceInfo.sourceFile = splitFile.getAbsolutePath();
				sourceInfo.trimIn = starttime / 1000.0;
				sourceInfo.trimOut = endtime / 1000.0;
				rl_work.setVisibility(View.VISIBLE);
				String name = splitFile.getName();
				String oldName = name.substring(0, name.indexOf("."));
				String newName = oldName + "_clip_in" + sourceInfo.trimIn
						+ "_out" + sourceInfo.trimOut;
				newName = newName.replaceAll("\\.", "");
				clipVideoName = splitFile.getAbsolutePath().replace(oldName,
						newName);
				splitMuxer.start(sourceInfo, clipVideoName);
				splitMuxer
						.setOnStatusUpatedListener(new DYHSplitMuxerFFmpeg.OnStatusUpatedListener() {
							@Override
							public void onStatusUpated(
									DYHSplitMuxerFFmpegStatusInfo arg0) {
								Intent intentback = new Intent();
								if (arg0.status == arg0.StatusFinished) {
									intentback.putExtra("filePath",
											clipVideoName);
									intentback.putExtra("indexNO", indexNO);
									intentback.putExtra("status", "1");
									customSetResult(intentback);
								} else if (arg0.status == arg0.StatusError) {
									intentback.putExtra("filePath", "");
									intentback.putExtra("indexNO", indexNO);
									intentback.putExtra("status", "0");
									customSetResult(intentback);
								}

							}
						});
			}
		});

		myprogress.setStartrOnMoveListener(new MyTouchlistener() {

			@Override
			public void ProgressTouch(int progeress) {

				String stringForTime = stringForTime(progeress);
				tv_timestart.setText("入点 " + stringForTime);
			}
		});
		myprogress.setEndOnMoveListener(new MyTouchlistener() {

			@Override
			public void ProgressTouch(int progeress) {
				String stringForTime = stringForTime(progeress);
				tv_timeend.setText("出点 " + stringForTime);
			}
		});
		rl_work.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		vv_player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				vv_player.start();
				vv_player.seekTo((int) starttime);
			}
		});

	}

	private void progressListener() {
		int position = vv_player.getCurrentPosition();
		myprogress.setProgress(position);
		Message msg = new Message();
		msg.what = position + 100;
		mHandler.sendMessageDelayed(msg, 300);
		if (position >= endtime) {
			vv_player.seekTo((int) starttime);
		}
	}

	protected void myPlay() {
		if (!hasListener) {
			progressListener();
			hasListener = true;
		}
		if (isPlaying) {
			vv_player.pause();
			iv_play.setImageResource(R.drawable.ic_media_play);
			isPlaying = false;
			return;
		}
		if (!isPlaying) {
			vv_player.start();
			iv_play.setImageResource(R.drawable.ic_media_pause);
			isPlaying = true;
			return;
		}

	}

	public void customSetResult(Intent intent) {
		try {
			splitMuxer.release();
			this.setResult(Constants.VIDEOEDIT_REQUEST, intent);
			this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;
		if (vv_player != null) {
			vv_player.stopPlayback();// 界面销毁时强制挂载视频
		}
	}

	/*
	 * @Override public boolean onTouchEvent(MotionEvent event) { // TODO
	 * Auto-generated method stub if (mediaController.isShowing()) {
	 * previewedit.setVisibility(View.VISIBLE); } else {
	 * previewedit.setVisibility(View.VISIBLE); } return true; }
	 */
	/**
	 * 该方法用于界面发生变化时，不让activity进行销毁操作，从而保持原先的上下文状态
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.e("debug", "configuration");
		super.onConfigurationChanged(newConfig);
	}

	private String stringForTime(int timeMs) {

		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

}
