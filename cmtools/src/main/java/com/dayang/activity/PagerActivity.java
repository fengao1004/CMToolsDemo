package com.dayang.activity;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dayang.cmtools.R;
import com.dayang.common.CommonUtil;
import com.dayang.common.ExitAppUtils;
import com.dayang.common.JSONFactory;
import com.dayang.common.MediaFile;
import com.dayang.inter.KeyValueData;
import com.dayang.inter.impl.KeyValueDataImpl;
import com.dayang.view.CustomMediaController;
import com.dayang.view.CustomMediaController.onFullScreenListener;
import com.dayang.view.CustomVideoView;
import com.google.gson.JsonObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 该activity页面用于实现媒体文件（其中包括图片，视频，音频）文件的橱窗展示或者播放
 *
 * @author renyuwei
 */
public class PagerActivity extends Activity {
    private ViewPager viewPager;// viewpager对象
    private View inflate;
    private CustomVideoView vv_player;// 自定义的视频播放控件
    private CustomMediaController mediaController;// 自定义的视频播放控件控制器

    boolean recordAudioStatus = false;
    boolean playAudioStatus = false;
    boolean pauseAudioStatus = false;
    ImageView point_White;
    MediaPlayer mPlayer = null;
    Chronometer mChronometer = null;
    Button playButton = null;
    Button stopButton = null;
    TextView alltimelen = null;
    KeyValueData keyValueData = null;
    int viewPagerItem ;
    @SuppressWarnings("unused")
    private RelativeLayout rl_player;// videoview外面的布局层
    private RelativeLayout iv_palyaudio;// videoview外面的布局层
    private RelativeLayout iv_error;// 视频错误层
    private AudioManager manager;// 系统声音管理器对象
    private int currentVolume;// 当前媒体声音音量

    private PhotoView iv_image;// viewpager中的图片
    private List<String> fileNameList;
    private int index;
    private LinearLayout ll_point;
    private float lly;
    private float llx;

    /**
     * 生命周期创建的方法
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.viewpager);// 设置绑定界面布局
        ExitAppUtils.getInstance().addActivity(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            index = bundle.getInt("index");
            fileNameList = bundle.getStringArrayList("fileNamePath");
        }



        viewPagerItem=index;
        keyValueData = new KeyValueDataImpl(this.getApplicationContext(),
                PagerActivity.this);
        iniView();
    }

    /**
     * 初始化布局中的控件对象并添加相应的鼠标事件
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void iniView() {
        ll_point = (LinearLayout) findViewById(R.id.ll_point);
        manager = (AudioManager) PagerActivity.this
                .getSystemService(PagerActivity.this.AUDIO_SERVICE);
        currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);// 获取当前媒体音量

        // 右滑图标

        viewPager = (ViewPager) findViewById(R.id.view_pager);// 创建viewpager对象
        // viewpager页面改变监听
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            // 当viewpager切换后调用的方法
            @Override
            public void onPageSelected(int arg0) {
                if(MediaFile.isImageFileType(fileNameList.get(arg0))){
                    ll_point.setVisibility(View.VISIBLE);
                }else{
                    ll_point.setVisibility(View.INVISIBLE);
                }
                ll_point.removeAllViews();
                int pointCount = fileNameList.size();
                for (int i = 0; i < pointCount; i++) {
                    ImageView imageView = new ImageView(getApplicationContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(15, 15, 15, 15);
                    imageView.setLayoutParams(params);
                    if(i==arg0){
                        imageView.setImageResource(R.drawable.shape_point_white);
                    }else{
                        imageView.setImageResource(R.drawable.shape_point_black);
                    }
                    ll_point.addView(imageView);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int
                    arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }

        });

        MyPagerAdapter adapter = new MyPagerAdapter(fileNameList);// 创建viewpager适配器
        viewPager.setAdapter(adapter);// 绑定适配器
        viewPager.setCurrentItem(index);
        initPoint();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initPoint() {
        int pointCount = fileNameList.size();
        if(pointCount==1){
            return;
        }
        ll_point.removeAllViews();
        for (int i = 0; i < pointCount; i++) {

           ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(15, 15, 15, 15);
            imageView.setLayoutParams(params);
            if(i==index){
                imageView.setImageResource(R.drawable.shape_point_white);
            }else{
                imageView.setImageResource(R.drawable.shape_point_black);
            }
            ll_point.addView(imageView);
        }


    }

    /**
     * viewpager的适配器
     */
    class MyPagerAdapter extends PagerAdapter {
        private List<String> fileNameList;
        private BitmapUtils bitmapUtils;
        // Xutils中异步处理图片的配置参数
        private BitmapDisplayConfig bigPicDisplayConfig;

        public MyPagerAdapter(List<String> fileNameList) {
            this.fileNameList = fileNameList;
            bitmapUtils = new BitmapUtils(PagerActivity.this);
            bigPicDisplayConfig = new BitmapDisplayConfig();
            bigPicDisplayConfig.setAutoRotation(true);
            bigPicDisplayConfig.setBitmapConfig(Bitmap.Config.ARGB_8888);// 设置图片每像素所占的字节
            // 2个字节
            bigPicDisplayConfig.setBitmapMaxSize(BitmapCommonUtils// 设置图片显示的最大尺寸
                    .getScreenSize(PagerActivity.this));
//            bigPicDisplayConfig.setLoadingDrawable(getResources().getDrawable(
//                    R.drawable.product_loading));// 设置加载中显示的图片
            bigPicDisplayConfig.setLoadFailedDrawable(getResources()
                    .getDrawable(R.drawable.shibia));// 设置加载失败显示的图片
        }

        @Override
        /**
         * 返回页面的个数
         */
        public int getCount() {
            if (this.fileNameList != null) {
                return this.fileNameList.size();
            }
            return 0;
        }

        @Override
        /**
         * 获得指定位置上的view
         * container 就是viewPager自身
         * position 是指定的位置
         */
        public Object instantiateItem(final ViewGroup container, int position) {
            // 必须在这里创建对象，不然无法使用viewPager.setCurrentItem();方法，该方法切换的是对象
            initPagerView();// 初始化视图控件
            displayViewAndData(position);// 显示视图和数据
            container.addView(inflate);
            // 返回一个与该view相关的一个对象
            return inflate;
        }

        /**
         * 显示数据
         *
         * @param position 当前item下标
         */
        private void displayViewAndData(int position) {
            int indexposition = position % this.fileNameList.size();

            String fileNamePath = this.fileNameList.get(indexposition);
            if (MediaFile.isImageFileType(fileNamePath)) {// 图片
                inflate.setBackgroundColor(0xff181c18);// 0xffD1C9D1设置当前背景颜色
                vv_player.setVisibility(View.GONE);// 隐藏视频控件
                iv_palyaudio.setVisibility(View.GONE);
                iv_image.setVisibility(View.VISIBLE);// 显示图片控件
                bitmapUtils
                        .display(iv_image, fileNamePath, bigPicDisplayConfig);
                iv_image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        finish();
                    }


                });

            } else if (MediaFile.isVideoFileType(fileNamePath)) {// 视频
                currentVolume = manager
                        .getStreamVolume(AudioManager.STREAM_MUSIC);// 获取当前媒体音量
                inflate.setBackgroundColor(0xff1F1F1F);// 设置背景颜色
                vv_player.setVisibility(View.VISIBLE);// 显示视频控件
                iv_image.setVisibility(View.GONE);// 隐藏图片控件
                iv_palyaudio.setVisibility(View.GONE);
                displayMediaPlayer(fileNamePath);// 显示视频
            } else if (MediaFile.isAudioFileType(fileNamePath)) {
                vv_player.setVisibility(View.GONE);// 显示视频控件
                iv_image.setVisibility(View.GONE);// 隐藏图片控件
                iv_palyaudio.setVisibility(View.VISIBLE);
                iv_palyaudio.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳出界面
                        finish();
                    }
                });
                displayAudioPlayer(fileNamePath);
            }
        }

        /**
         * 初始化viewpager条目视图
         */
        private void initPagerView() {
            inflate = View.inflate(PagerActivity.this,
                    R.layout.viewpager_category, null);// viewpager显示的布局
            rl_player = (RelativeLayout) inflate.findViewById(R.id.rl_player);// 视频外布局
            vv_player = (CustomVideoView) inflate.findViewById(R.id.vv_player);// 视频控件
            iv_error = (RelativeLayout) inflate.findViewById(R.id.iv_error);// 视频无法播放时显示的布局
            iv_image = (PhotoView) inflate.findViewById(R.id.iv_image);// 图片控件
            iv_palyaudio = (RelativeLayout) inflate
                    .findViewById(R.id.iv_palyaudio);
        }

        @Override
        /**
         * 判断指定的的view和object是否有关联关系
         * view 某一位置上的显示的页面
         * object 某一位置上返回的object 就是instantiateItem返回的object
         */
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        /**
         * 销毁指定位置上的view
         *
         * object 就是instantiateItem 返回的object
         */
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 下面这句如果不注掉，会抛异常
            // super.destroyItem(container, position, object);
            // 恢复正常音量
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            container.removeView((View) object);
        }

    }

    /**
     * 显示视频
     *
     * @param url地址
     */
    private void displayMediaPlayer(String filePath) {
        vv_player.setVisibility(View.VISIBLE);// 显示视频控件
        iv_error.setVisibility(View.GONE);// 开始不显示错误层
        Window window = getWindow();
        mediaController = new CustomMediaController(PagerActivity.this, true,
                window);// 绑定控制器
        mediaController.setFullScreenEnable(true);
        // 全屏按钮
        mediaController.setOnFullScreenListener(new onFullScreenListener() {
            @Override
            public void onFullScreen(View v) {
                // 横竖屏切换时，会重走ListFragment的生命周期，缓存任务类型，为重走生命周期刷新数据准备
                if (CommonUtil.isScreenOriatationPortrait(PagerActivity.this)) {// 当屏幕是竖屏时
                    // 点击后变横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置当前activity为横屏
                    // 当横屏时 把除了视频以外的都隐藏
                    // 全屏时取消滑动翻页功能
                    viewPager.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置当前activity为竖屏
                    if (fileNameList != null && fileNameList.size() > 0) {// 此类型有多个页面
                    } else {
                    }
                    // 屏幕恢复正常时加回滑动翻页功能
                    viewPager.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                }
            }
        });
        vv_player.setMediaController(mediaController);
        // 视频准备好监听
        vv_player.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                vv_player.start();// 播放视频
            }
        });
        // 视频播放错误监听
        vv_player.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                iv_error.setVisibility(View.VISIBLE);// 显示错误层
                vv_player.setVisibility(View.GONE);// 隐藏视频空间
                return false;
            }
        });
        vv_player.setVideoPath(filePath);// 设置视频播放的网络路径
    }

    /**
     * 显示音频播放界面并初始化按钮对象和事件
     *
     * @param filePath
     */
    private void displayAudioPlayer(final String filePath) {
        iv_palyaudio.setVisibility(View.VISIBLE);// 显示视频控件
        mChronometer = (Chronometer) inflate
                .findViewById(R.id.window_chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.setFormat("%s");
        mChronometer.setText("00:00");
        playButton = (Button) inflate.findViewById(R.id.window_playaudio);
        stopButton = (Button) inflate.findViewById(R.id.window_stopaudio);
        alltimelen = (TextView) inflate.findViewById(R.id.window_alltimelen);
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pauseAudioStatus && !playAudioStatus) {
                    pauseAudioStatus = true;
                    playAudioStatus = true;
                    v.setBackgroundResource(R.drawable.pause);
                    playRecordAudio(filePath);
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                    mChronometer.start();
                } else if (pauseAudioStatus && playAudioStatus) {
                    pauseAudioStatus = false;
                    v.setBackgroundResource(R.drawable.window_play);
                    mPlayer.pause();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("key", "stoptime");
                    jsonObject.addProperty(
                            "value",
                            SystemClock.elapsedRealtime()
                                    - mChronometer.getBase());
                    mChronometer.stop();
                    keyValueData.saveKVData(jsonObject.toString());
                } else if (!pauseAudioStatus && playAudioStatus) {
                    if (mChronometer.getText().equals(alltimelen.getText())) {
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        mChronometer.stop();
                    }
                    pauseAudioStatus = true;
                    v.setBackgroundResource(R.drawable.pause);
                    mPlayer.start();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("key", "stoptime");
                    JsonObject jsonObject2 = JSONFactory
                            .parseJsonStr(keyValueData.getKVData(jsonObject
                                    .toString()));
                    mChronometer.setBase((SystemClock.elapsedRealtime() - jsonObject2
                            .get("content").getAsLong()));
                    mChronometer.start();
                }

            }
        });
        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudioStatus = false;
                pauseAudioStatus = false;
                playButton.setBackgroundResource(R.drawable.window_play);
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
                        playButton
                                .setBackgroundResource(R.drawable.window_play);
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
