package com.dayang.activity;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dayang.cmtools.R;
import com.dayang.common.JSONFactory;
import com.dayang.inter.KeyValueData;
import com.dayang.inter.impl.KeyValueDataImpl;
import com.google.gson.JsonObject;

/**
 * 该界面用于实现单个音频的播放功能
 * @author renyuwei
 *
 */
public class PlayAudioActivity extends Activity {
	boolean recordAudioStatus = false;
	boolean playAudioStatus = false;
	boolean pauseAudioStatus = false;
	MediaPlayer mPlayer = null;
	Chronometer mChronometer = null;
	Button playButton = null;
	Button stopButton = null;
	TextView alltimelen = null;
	String filePath = null;
	RelativeLayout iv_palyaudio = null;
    KeyValueData keyValueData = null;
	private TextView tv_play_audio_play;


	/**
	 * 创建播放音频的页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_play_audio);
		Intent intent = getIntent();
		filePath = intent.getExtras().getString("path");
		keyValueData = new KeyValueDataImpl(this.getApplicationContext(), PlayAudioActivity.this);
		initView();
	}
	
    /**
     * 初始化音频播放界面的各控件对象并添加相应的鼠标事件
    */
	public void initView() {
		mChronometer = (Chronometer) this
				.findViewById(R.id.window_chronometer_single);
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.setFormat("%s");
		mChronometer.setText("00:00");
		playButton = (Button) this.findViewById(R.id.window_playaudio_single);
		stopButton = (Button) this.findViewById(R.id.window_stopaudio_single);
		alltimelen = (TextView) this
				.findViewById(R.id.window_alltimelen_single);
		iv_palyaudio = (RelativeLayout) this.findViewById(R.id.window_relative_layout);
		tv_play_audio_play = (TextView) findViewById(R.id.tv_play_audio_play);
		playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!pauseAudioStatus && !playAudioStatus) {
					pauseAudioStatus = true;
					playAudioStatus = true;
					v.setBackgroundResource(R.drawable.pause);
					playRecordAudio(filePath);
					tv_play_audio_play.setText("暂停");
					mChronometer.setBase(SystemClock.elapsedRealtime());
					mChronometer.start();
				} else if (pauseAudioStatus && playAudioStatus) {
					pauseAudioStatus = false;
					tv_play_audio_play.setText("播放");
					v.setBackgroundResource(R.drawable.window_play);
					mPlayer.pause();
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("key", "stoptime");
					jsonObject.addProperty("value", SystemClock.elapsedRealtime()-mChronometer.getBase());
					mChronometer.stop();
					keyValueData.saveKVData(jsonObject.toString());
				} else if (!pauseAudioStatus && playAudioStatus) {
					if (mChronometer.getText().equals(alltimelen.getText())) {
						mChronometer.setBase(SystemClock.elapsedRealtime());
						mChronometer.stop();
					}
					tv_play_audio_play.setText("暂停");
					pauseAudioStatus = true;
					v.setBackgroundResource(R.drawable.pause);
					mPlayer.start();
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("key", "stoptime");
					JsonObject jsonObject2 = JSONFactory.parseJsonStr(keyValueData.getKVData(jsonObject.toString()));
					mChronometer.setBase((SystemClock.elapsedRealtime() - jsonObject2.get("content").getAsLong()));
				    mChronometer.start();
				}
			}
		});
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mPlayer==null) {
					return;
				}
				playAudioStatus = false;
				pauseAudioStatus = false;
				playButton.setBackgroundResource(R.drawable.window_play);
				tv_play_audio_play.setText("播放");
				mPlayer.stop();
				mChronometer.stop();
				mChronometer.setBase(SystemClock.elapsedRealtime());
			}
		});
		// 为计时器绑定监听事件
		mChronometer
				.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
					@Override
					public void onChronometerTick(Chronometer ch) {
						// 如果从开始计时到现在超过了60s
						if (ch.getText().equals(alltimelen.getText())) {
							ch.stop();
							// mPlayer.stop();
						}
					}
				});

		iv_palyaudio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//跳出界面
				finish();
			}
		});
	}

	/**
	 * 播放音频
	 */
	public void playRecordAudio(String filePath) {
		mPlayer = new MediaPlayer();
		try {
			if (filePath != null && !filePath.equals("")) {
				mPlayer.setDataSource(filePath);
				mPlayer.prepare();
				if (mPlayer.getDuration() != -1) {
					String miniteStr = "";
					String secondStr = "";
					Calendar c = Calendar.getInstance();
					c.setTime(new Date(mPlayer.getDuration()));
					int minite = c.get(Calendar.MINUTE);
					int second = c.get(Calendar.SECOND);
					if (second < 10) {
						secondStr = "0" + second;
					} else {
						secondStr = second + "";
					}
					if (minite < 10) {
						miniteStr = "0" + minite;
					} else {
						miniteStr = minite + "";
					}
					alltimelen.setText(miniteStr + ":" + secondStr);
				}
				mPlayer.start();
				mPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						Log.d("tag", "播放完毕");
						playAudioStatus = false;
						pauseAudioStatus = false;
						playButton.setBackgroundResource(R.drawable.window_play);
						mChronometer.stop();
						mChronometer.setBase(SystemClock.elapsedRealtime());
					}
				});
			}
		} catch (IOException e) {
			Log.e("debug", e.toString());
		}
	}
	
}
