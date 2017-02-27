package com.dayang.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.dayang.cmtools.R;
import com.dayang.view.CustomEditMediaController.MediaPlayerControl;

import java.io.IOException;
import java.util.Map;
/**
 * 
 * @author 任育伟 
 * @version 1.0
 */
@SuppressWarnings("unused")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class CustomEditVideoView extends SurfaceView implements MediaPlayerControl {  
    private String TAG = "VideoView";  
    private Uri         mUri;  
    private Map<String, String> mHeaders;  //the headers associated with the http request for the stream you want to play
    private int         mDuration;  
  
    private static final int STATE_ERROR              = -1;  
    private static final int STATE_IDLE               = 0;  
    private static final int STATE_PREPARING          = 1;  
    private static final int STATE_PREPARED           = 2;  
    private static final int STATE_PLAYING            = 3;  
    private static final int STATE_PAUSED             = 4;  
    private static final int STATE_PLAYBACK_COMPLETED = 5;  
  
    private int mCurrentState = STATE_IDLE;  
    private int mTargetState  = STATE_IDLE;  
  
    private SurfaceHolder mSurfaceHolder = null;  //Abstract interface to someone holding a display surface
    private MediaPlayer mMediaPlayer = null;//MediaPlayer class can be used to control playback of audio/video files and streams.
    private int         mAudioSession;
    private int         mVideoWidth;  
    private int         mVideoHeight;  
    private int         mSurfaceWidth;  
    private int         mSurfaceHeight;  
    private CustomEditMediaController mMediaController;  
    private OnCompletionListener mOnCompletionListener;  
    private MediaPlayer.OnPreparedListener mOnPreparedListener;  
    private int         mCurrentBufferPercentage;  
    private OnErrorListener mOnErrorListener;  
    private OnInfoListener  mOnInfoListener;  
    private int         mSeekWhenPrepared;    
    private boolean     mCanPause;  
    private boolean     mCanSeekBack;  
    private boolean     mCanSeekForward;  
    Context mContext;  
  
    public CustomEditVideoView(Context context) {  
        super(context);  
        mContext = context;  
        initVideoView();  
    }  
  
    public CustomEditVideoView(Context context, AttributeSet attrs) {  
        this(context, attrs, 0);  
        mContext = context;  
        initVideoView();  
    }  
  
    public CustomEditVideoView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        mContext = context;  
        initVideoView();  
    }  
    /**
     * 娴嬮噺  绛夋瘮渚嬫樉绀鸿棰�
     */
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
    	int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {

            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        setMeasuredDimension(width, height);
    }
    
  
    @SuppressLint("NewApi")
	@Override  
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {  
        super.onInitializeAccessibilityEvent(event);  
        event.setClassName(CustomEditVideoView.class.getName());  
    }  
  
	@SuppressLint("NewApi")
	@Override  
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {  
        super.onInitializeAccessibilityNodeInfo(info);  
        info.setClassName(CustomEditVideoView.class.getName());  
    }  
  
    public int resolveAdjustedSize(int desiredSize, int measureSpec) {  
        int result = desiredSize;  
        int specMode = MeasureSpec.getMode(measureSpec);  
        int specSize =  MeasureSpec.getSize(measureSpec);  
  
        switch (specMode) {  
            case MeasureSpec.UNSPECIFIED:  
                result = desiredSize;  
                break;  
  
            case MeasureSpec.AT_MOST:  
                result = Math.min(desiredSize, specSize);  
                break;  
  
            case MeasureSpec.EXACTLY:  
                result = specSize;  
                break;  
        }  
        return result;  
}  
    /**
     * 鍒濆鍖�
     */
    @SuppressWarnings("deprecation")
	private void initVideoView() {  
        mVideoWidth = 0;  
        mVideoHeight = 0;  
        getHolder().addCallback(mSHCallback);  //Add a Callback interface for this holde
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
        setFocusable(true);  
        setFocusableInTouchMode(true);  
        requestFocus();  
        mCurrentState = STATE_IDLE;  
        mTargetState  = STATE_IDLE;  
    }  
    /**
     * 璁剧疆鏈湴瑙嗛鎾斁鍦板潃
     * @param path
     */
    public void setVideoPath(String path) {  
        setVideoURI(Uri.parse(path));  
    }  
    /**
     * 璁剧疆缃戠粶瑙嗛鎾斁uri
     * @param uri
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);  
    }  
  
    /** 
     * @hide 
     */  
    public void setVideoURI(Uri uri, Map<String, String> headers) {  
        mUri = uri;  
        mHeaders = headers;  
        mSeekWhenPrepared = 0;  
        openVideo();  
        requestLayout();  
        invalidate();  
    }  
    
    public void stopPlayback() {  
        if (mMediaPlayer != null) {  
            mMediaPlayer.stop();  
            mMediaPlayer.release();  
            mMediaPlayer = null;  
            mCurrentState = STATE_IDLE;  
            mTargetState  = STATE_IDLE;  
        }  
    }  
  
    @SuppressLint("NewApi")
	private void openVideo() {  
        if (mUri == null || mSurfaceHolder == null) {  
            return;  
        }  
  
        Intent i = new Intent("com.android.music.musicservicecommand");  
        i.putExtra("command", "pause");  
        mContext.sendBroadcast(i);  
  
        release(false);  
        try {  
            mMediaPlayer = new MediaPlayer();
            if (mAudioSession != 0) {
                mMediaPlayer.setAudioSessionId(mAudioSession);
            } else {
                mAudioSession = mMediaPlayer.getAudioSessionId();
            }
            mMediaPlayer.setOnPreparedListener(mPreparedListener);  
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);  
            mDuration = -1;  
            mMediaPlayer.setOnCompletionListener(mCompletionListener);  
            mMediaPlayer.setOnErrorListener(mErrorListener);  
            mMediaPlayer.setOnInfoListener(mOnInfoListener);  
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);  
            mCurrentBufferPercentage = 0;  
            mMediaPlayer.setDataSource(mContext, mUri, mHeaders);  
            mMediaPlayer.setDisplay(mSurfaceHolder);  
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  
            mMediaPlayer.setScreenOnWhilePlaying(true);  
            mMediaPlayer.prepareAsync();  
  
            mCurrentState = STATE_PREPARING;  
            attachMediaController();  
        } catch (IOException ex) {  
            Log.w(TAG, "Unable to open content: " + mUri, ex);  
            mCurrentState = STATE_ERROR;  
            mTargetState = STATE_ERROR;  
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);  
            return;  
        } catch (IllegalArgumentException ex) {  
            Log.w(TAG, "Unable to open content: " + mUri, ex);  
            mCurrentState = STATE_ERROR;  
            mTargetState = STATE_ERROR;  
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);  
            return;  
        }  
    }  
    /**
     * 缁戝畾mediaController
     * @param controller
     */
    public void setMediaController(CustomEditMediaController controller) {  
        if (mMediaController != null) {  
            mMediaController.hide();  
        }  
        mMediaController = controller;  
        attachMediaController();  
    }  
  
    private void attachMediaController() {  
        if (mMediaPlayer != null && mMediaController != null) {  
            mMediaController.setMediaPlayer(this);  
            View anchorView = this.getParent() instanceof View ?  
                    (View)this.getParent() : this;  
            mMediaController.setAnchorView(anchorView);  //Set the view that acts as the anchor for the control view
            mMediaController.setEnabled(isInPlaybackState());  
        }  
    }  
  
    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =  
        new MediaPlayer.OnVideoSizeChangedListener() {  
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {  
                mVideoWidth = mp.getVideoWidth();  
                mVideoHeight = mp.getVideoHeight();  
                if (mVideoWidth != 0 && mVideoHeight != 0) {  
                    getHolder().setFixedSize(mVideoWidth, mVideoHeight);  
                    requestLayout();  
                }  
            }  
    };  
  
    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {  
        public void onPrepared(MediaPlayer mp) {  
            mCurrentState = STATE_PREPARED;  
  
            mCanPause = mCanSeekBack = mCanSeekForward = true;
            
  
            if (mOnPreparedListener != null) {  
                mOnPreparedListener.onPrepared(mMediaPlayer);  
            }  
            if (mMediaController != null) {  
                mMediaController.setEnabled(true);  
            }  
            mVideoWidth = mp.getVideoWidth();  
            mVideoHeight = mp.getVideoHeight();  
  
            int seekToPosition = mSeekWhenPrepared;    
            if (seekToPosition != 0) {  
                seekTo(seekToPosition);  
            }  
            if (mVideoWidth != 0 && mVideoHeight != 0) {  
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);  
                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {  
                    if (mTargetState == STATE_PLAYING) {  
                        start();  
                        if (mMediaController != null) {  
//                            mMediaController.show();  
                        }  
                    } else if (!isPlaying() &&  
                               (seekToPosition != 0 || getCurrentPosition() > 0)) {  
                       if (mMediaController != null) {  
                           mMediaController.show(0);  
                       }  
                   }  
                }  
            } else {  
                if (mTargetState == STATE_PLAYING) {  
                    start();  
                }  
            }  
        }  
    };  
  
    private MediaPlayer.OnCompletionListener mCompletionListener =  
        new MediaPlayer.OnCompletionListener() {  
        public void onCompletion(MediaPlayer mp) {  
            mCurrentState = STATE_PLAYBACK_COMPLETED;  
            mTargetState = STATE_PLAYBACK_COMPLETED;  
            if (mMediaController != null) {  
                mMediaController.hide();  
            }  
            if (mOnCompletionListener != null) {  
                mOnCompletionListener.onCompletion(mMediaPlayer);  
            }  
        }  
    };  
  
    private MediaPlayer.OnErrorListener mErrorListener =  
        new MediaPlayer.OnErrorListener() {  
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {  
            Log.d(TAG, "Error: " + framework_err + "," + impl_err);  
            mCurrentState = STATE_ERROR;  
            mTargetState = STATE_ERROR;  
            if (mMediaController != null) {  
                mMediaController.hide();  
            }  
  
            if (mOnErrorListener != null) {  
                if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {  
                    return true;  
                }  
            }  
  
            if (getWindowToken() != null) {  
                //Resources r = mContext.getResources();  
                int messageId;  
  
                if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {  
                    messageId = R.string.VideoView_error_text_invalid_progressive_playback;  
                } else {  
                    messageId = R.string.VideoView_error_text_unknown;  
                }  
                //寮瑰嚭閿欒鎻愮ずdialog
//                new AlertDialog.Builder(mContext)  
//                        .setMessage(messageId)  
//                        .setPositiveButton(R.string.VideoView_error_button,  
//                                new DialogInterface.OnClickListener() {  
//                                    public void onClick(DialogInterface dialog, int whichButton) {  
//                                          
//                                        if (mOnCompletionListener != null) {  
//                                            mOnCompletionListener.onCompletion(mMediaPlayer);  
//                                        }  
//                                    }  
//                                })  
//                        .setCancelable(false)  
//                        .show();  
            }  
            return true;  
        }  
    };  
  
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =  
        new MediaPlayer.OnBufferingUpdateListener() {  
        public void onBufferingUpdate(MediaPlayer mp, int percent) {  
            mCurrentBufferPercentage = percent;  
        }  
    };  
  
    /** 
     * Register a callback to be invoked when the media file 
     * is loaded and ready to go. 
     * 
     * @param l The callback that will be run 
     */  
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l)  
    {  
        mOnPreparedListener = l;  
    }  
  
    /** 
     * Register a callback to be invoked when the end of a media file 
     * has been reached during playback. 
     * 
     * @param l The callback that will be run 
     */  
    public void setOnCompletionListener(OnCompletionListener l)  
    {  
        mOnCompletionListener = l;  
    }  
  
    /** 
     * Register a callback to be invoked when an error occurs 
     * during playback or setup.  If no listener is specified, 
     * or if the listener returned false, VideoView will inform 
     * the user of any errors. 
     * 
     * @param l The callback that will be run 
     */  
    public void setOnErrorListener(OnErrorListener l)  
    {  
        mOnErrorListener = l;  
    }  
  
    /** 
     * Register a callback to be invoked when an informational event 
     * occurs during playback or setup. 
     * 
     * @param l The callback that will be run 
     */  
    public void setOnInfoListener(OnInfoListener l) {  
        mOnInfoListener = l;  
    }  
  
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback()  
    {  
        public void surfaceChanged(SurfaceHolder holder, int format,  
                                    int w, int h)  
        {  
            mSurfaceWidth = w;  
            mSurfaceHeight = h;  
            boolean isValidState =  (mTargetState == STATE_PLAYING);  
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);  
            if (mMediaPlayer != null && isValidState && hasValidSize) {  
                if (mSeekWhenPrepared != 0) {  
                    seekTo(mSeekWhenPrepared);  
                }  
                start();  
            }  
        }  
  
        public void surfaceCreated(SurfaceHolder holder)  
        {  
            mSurfaceHolder = holder;  
            openVideo();  
        }  
  
        public void surfaceDestroyed(SurfaceHolder holder)  
        {  
            mSurfaceHolder = null;  
            if (mMediaController != null) mMediaController.hide();  
            release(true);  
        }  
    };  
  
    private void release(boolean cleartargetstate) {  
        if (mMediaPlayer != null) {  
            mMediaPlayer.reset();  
            mMediaPlayer.release();  
            mMediaPlayer = null;  
            mCurrentState = STATE_IDLE;  
            if (cleartargetstate) {  
                mTargetState  = STATE_IDLE;  
            }  
        }  
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent ev) {  
        if (isInPlaybackState() && mMediaController != null) {  
            toggleMediaControlsVisiblity();  
        }  
        return false;  
    }  
  
    @Override  
    public boolean onTrackballEvent(MotionEvent ev) {  
        if (isInPlaybackState() && mMediaController != null) {  
            toggleMediaControlsVisiblity();  
        }  
        return false;  
    }  
  
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&  
                                     keyCode != KeyEvent.KEYCODE_VOLUME_UP &&  
                                     keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&  
                                     keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&  
                                     keyCode != KeyEvent.KEYCODE_MENU &&  
                                     keyCode != KeyEvent.KEYCODE_CALL &&  
                                     keyCode != KeyEvent.KEYCODE_ENDCALL;  
        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {  
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||  
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {  
                if (mMediaPlayer.isPlaying()) {  
                    pause();  
                    mMediaController.show();  
                } else {  
                    start();  
                    mMediaController.hide();  
                }  
                return true;  
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {  
                if (!mMediaPlayer.isPlaying()) {  
                    start();  
                    mMediaController.hide();  
                }  
                return true;  
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP  
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {  
                if (mMediaPlayer.isPlaying()) {  
                    pause();  
                    mMediaController.show();  
                }  
                return true;  
            } else {  
                toggleMediaControlsVisiblity();  
            }  
        }  
  
        return super.onKeyDown(keyCode, event);  
    }  
  
    private void toggleMediaControlsVisiblity() {  
        if (mMediaController.isShowing()) {  
            mMediaController.hide();  
        } else {  
            mMediaController.show();  
        }  
    }  
    /**
     * 鎾斁
     */
    public void start() {  
        if (isInPlaybackState()) {  
            mMediaPlayer.start();  
            mCurrentState = STATE_PLAYING;  
        }  
        mTargetState = STATE_PLAYING;  
    }  
    /**
     * 鏆傚仠
     */
    public void pause() {  
        if (isInPlaybackState()) {  
            if (mMediaPlayer.isPlaying()) {  
                mMediaPlayer.pause();  
                mCurrentState = STATE_PAUSED;  
            }  
        }  
        mTargetState = STATE_PAUSED;  
    }  
    
    public void suspend() {  
        release(false);  
    }  
  
    public void resume() {  
        openVideo();  
    }  
    /**
     * 鑾峰彇瑙嗛鐨勬�鏃堕棿 姣
     */
    public int getDuration() {  
    	if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }

        return -1;
    }  
    /**
     * 鑾峰彇瑙嗛褰撳墠鎾斁鍒扮殑浣嶇疆 姣
     */
    public int getCurrentPosition() {  
        if (isInPlaybackState()) {  
            return mMediaPlayer.getCurrentPosition();  
        }  
        return 0;  
    }  
    /**
     * 鐩存帴鍒版寚瀹氱殑鏃堕棿浣嶇疆 
     */
    public void seekTo(int msec) {  
        if (isInPlaybackState()) {  
            mMediaPlayer.seekTo(msec);  //Seeks to specified time position
            mSeekWhenPrepared = 0;  
        } else {  
            mSeekWhenPrepared = msec;  
        }  
    }  
    /**
     * 鍒ゆ柇瑙嗛鏄惁姝ｅ湪鎾斁
     */
    public boolean isPlaying() {  
        return isInPlaybackState() && mMediaPlayer.isPlaying();  
    }  
    /**
     * 鑾峰緱缂撳啿鍖哄ぇ灏�
     */
    public int getBufferPercentage() {  
        if (mMediaPlayer != null) {  
            return mCurrentBufferPercentage;  
        }  
        return 0;  
    }  
  
    private boolean isInPlaybackState() {  
        return (mMediaPlayer != null &&  
                mCurrentState != STATE_ERROR &&  
                mCurrentState != STATE_IDLE &&  
                mCurrentState != STATE_PREPARING);  
    }  
    /**
     * 鑳藉惁鏆傚仠
     */
    public boolean canPause() {  
        return mCanPause;  
    }  
    /**
     * 鑳藉惁蹇�
     */
    public boolean canSeekBackward() {  
        return mCanSeekBack;  
    }  
    /**
     * 鑳藉惁蹇繘
     */
    public boolean canSeekForward() {  
        return mCanSeekForward;  
    }

	@Override
	public int getAudioSessionId() {
		if (mAudioSession == 0) {
            MediaPlayer foo = new MediaPlayer();
            mAudioSession = foo.getAudioSessionId();
            foo.release();
        }
        return mAudioSession;
	}  
}  