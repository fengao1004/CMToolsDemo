package com.dayang.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.dayang.cmtools.R;
import com.dayang.common.Canstant;
import com.dayang.common.Constants;
import com.dayang.common.ExitAppUtils;
import com.kubility.demo.MP3Recorder;

/***
 * 该acitivity主要用于实现录音并保存的功能，录制成mp3的音频文件
 * 20161118 添加录音暂停功能 by冯傲
 *
 * @author renyuwei
 */
public class RecordAudioActivity extends Activity {
    private static final String TAG = Canstant.TAG;
    private MP3Recorder recorder = null;
    private File fileAudio = null;
    private MediaRecorder mRecorder = null;
    private Chronometer mChronometer;
    private OnClickListener mchronometerListener;
    private boolean pause = false;
    public String fileName = "";
    private Button resetButton;
    private Button recordButton;
    private Button playButton;
    private Button userAudio;
    private Button cancelAudio;
    private TextView alltimelen;
    long addTime;
    MediaPlayer mPlayer = null;
    private boolean recordAudioStatus = false;
    private boolean playAudioStatus = false;
    private boolean firstRecordAudio = true;
    private boolean stopRecordAudio = false;

    //触发录音状态后的回调对象
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MP3Recorder.MSG_REC_STARTED:
                case MP3Recorder.MSG_REC_RESTORE:
                    break;
                case MP3Recorder.MSG_REC_STOPPED:
                    break;
                case MP3Recorder.MSG_REC_PAUSE:
                    break;
            }
        }
    };
    private TextView tv_record_audio;
    private TextView tv_play;

    /**
     * 创建录音界面
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recordaudio);
        ExitAppUtils.getInstance().addActivity(this);
        initView();
        initClickEvent();
    }

    /**
     * 录音界面控件对象的初始化
     */
    public void initView() {
        resetButton = (Button) findViewById(R.id.resetaudio);
        recordButton = (Button) findViewById(R.id.recordaudio);
        playButton = (Button) findViewById(R.id.playaudio);
        playButton.setBackgroundResource(R.drawable.play2);
        userAudio = (Button) findViewById(R.id.useaudio);
        tv_play = (TextView) findViewById(R.id.tv_play);
        cancelAudio = (Button) findViewById(R.id.cancelaudio);
        alltimelen = (TextView) findViewById(R.id.alltimelen);
        recorder = new MP3Recorder();
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.setFormat("%s");
    }

    /**
     * 录音界面控件的点击事件初始化
     */
    public void initClickEvent() {
        resetButton.setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                getFileByDate("audios", "mp3");
                mChronometer.stop();
                addTime = 0;
                alltimelen.setText("00:00");
                tv_record_audio.setText("录制");
                userAudio.setVisibility(View.INVISIBLE);
                mChronometer.setBase(SystemClock.elapsedRealtime());
                recordButton.setBackgroundResource(R.drawable.record);
                playButton.setBackgroundResource(R.drawable.play2);
                firstRecordAudio = true;
                if (recordAudioStatus) {
                    recordAudioStatus = false;
                    recorder.stop();
                } else if (playAudioStatus) {
                    playAudioStatus = false;
                    mPlayer.stop();
                }
            }
        });
        tv_record_audio = (TextView) findViewById(R.id.tv_record_audio);

        recordButton.setOnClickListener(new OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                Log.d("debug", "开始录制视频");
                if (playAudioStatus) {
                    return;
                }
                if (!recordAudioStatus) {
                    playButton.setBackgroundResource(R.drawable.play2);
                    tv_record_audio.setText("暂停");
                    recordAudioStatus = true;
                    firstRecordAudio = false;
                    mChronometer.start();
                    if (fileName.equals("")) {
                        getFileByDate("audios", "mp3");
                    }
                    fileAudio = new File(fileName + "_temporary");
                    v.setBackgroundResource(R.drawable.pause2);
                    Log.i(TAG, "onClick: " + addTime);
                    mChronometer.setBase(addTime == 0 ? SystemClock.elapsedRealtime() : SystemClock.elapsedRealtime() - addTime + 800);
                    userAudio.setVisibility(View.INVISIBLE);
                    recorder.setFilePath(fileAudio.getAbsolutePath());
                    recorder.setSampleRate(8000);
                    recorder.setHandle(handler);
                    recorder.start();
                    Log.i(TAG, "onClick: " + new File(fileName).length());

                } else {
                    playButton.setBackgroundResource(R.drawable.play);
                    tv_record_audio.setText("继续");
                    recordAudioStatus = false;
                    stopRecordAudio = true;
                    v.setBackgroundResource(R.drawable.record);
                    userAudio.setVisibility(View.VISIBLE);
                    mChronometer.stop();
                    recorder.stop();
                    //文件合并操作
                    addTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
                    mergeAudioFile();
                    alltimelen.setText(mChronometer.getText());
                    playButton.setBackgroundResource(R.drawable.play);
                }
            }
        });
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordAudioStatus || !stopRecordAudio) {
                    return;
                }
                File file = new File(fileName);
                if (file.length() == 0) {
                    return;
                }
                if (!playAudioStatus) {
                    playAudioStatus = true;
                    recordButton.setBackgroundResource(R.drawable.record_inable);
                    v.setBackgroundResource(R.drawable.stopplay);
                    playRecordAudio();
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                    mChronometer.start();
                    tv_play.setText("停止");
                } else {
                    playAudioStatus = false;
                    v.setBackgroundResource(R.drawable.play);
                    recordButton.setBackgroundResource(R.drawable.record);
                    if (mPlayer.isPlaying()) {
                        mPlayer.stop();
                    }
                    tv_play.setText("播放");
                    mChronometer.stop();
                }

            }
        });
        //使用录制的音频
        userAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentback = new Intent();
                intentback.putExtra("audiofilePath", fileName);
                setResult(Constants.RECORD_AUDIO_REQUEST, intentback);
                finish();
            }
        });
        //取消录制音频
        cancelAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentback = new Intent();
                intentback.putExtra("audiofilePath", "");
                setResult(Constants.RECORD_AUDIO_REQUEST, intentback);
                finish();
            }
        });

        //为计时器绑定监听事件
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer ch) {
                // 如果从开始计时到现在超过了60s
                if (playAudioStatus && !recordAudioStatus) {
                    if (ch.getText().equals(alltimelen.getText())) {
                        ch.stop();
                       /*  if (mPlayer.isPlaying()) {
                             mPlayer.stop();
     					}*/
                    }
                }
            }
        });


    }

    private boolean mergeAudioFile() {
        boolean success = false;
        try {
            File file = new File(fileName);
            Log.i(TAG, "mergeAudioFile: filePath" + fileName);
            boolean fristMerae = false;//合并文件时除了第一个文件都要减去6个字节
            FileOutputStream os = new FileOutputStream(file, true);
            long length = file.length();
            Log.i(TAG, "mergeAudioFile: " + "源文件长度" + length);
            fristMerae = length == 0 ? true : false;
            boolean deleteSixByte = false;
            FileInputStream is = new FileInputStream(new File(fileName + "_temporary"));
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = is.read(b)) != -1) {
                if (!deleteSixByte && !fristMerae) {
                    os.write(b, 6, len - 6);
                    deleteSixByte = true;
                } else {
                    os.write(b, 0, len);
                }
            }
            os.close();
            is.close();
            Log.i(TAG, "mergeAudioFile: " + "合并完毕");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    /**
     * 播放音频
     */
    public void playRecordAudio() {
        mPlayer = new MediaPlayer();
        try {
            if (fileAudio != null && fileAudio.exists()) {
                mPlayer.setDataSource(fileName);
                mPlayer.prepare();
                mPlayer.start();
                int duration = mPlayer.getDuration();
                Log.i(TAG, "playRecordAudio: " + duration);
                mPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d("tag", "播放完毕");
                        playAudioStatus = false;
                        mChronometer.stop();
                        playButton.setBackgroundResource(R.drawable.play);
                        recordButton.setBackgroundResource(R.drawable.record);
                    }
                });
            }
        } catch (IOException e) {
            Log.e("debug", "prepare play failed");
        }
    }

    /**
     * 根据文件夹和扩展名生成文件对象
     *
     * @param fileDir
     * @param fileExtType
     * @return
     */
    public File getFileByDate(String fileDir, String fileExtType) {
        File fileout = null;
        try {
            File out = null;
            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String date = sDateFormat.format(new Date());
            date = date.replaceAll(" |:|-", "");
            String uploadPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + fileDir + "/";
            out = new File(uploadPath);
            if (!out.exists()) {
                out.mkdirs();
            }
            String uplaodFileName = date.toString() + "." + fileExtType;
            fileout = new File(uploadPath, uplaodFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        fileName = fileout.getAbsolutePath();
        fileAudio = fileout;
        return fileout;
    }


}
