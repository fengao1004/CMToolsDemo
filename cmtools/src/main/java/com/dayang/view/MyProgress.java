package com.dayang.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dayang.cmtools.R;
import com.dayang.common.Canstant;
import com.dayang.view.MyprogressBrackGround.ConfigChangesListener;

@SuppressLint("NewApi")
public class MyProgress extends RelativeLayout {

	private static final String TAG =  Canstant.TAG;
	private MyprogressBrackGround my_p;
	private ImageView iv_progress;
	private ImageView iv_start;
	private ImageView iv_end;
	private int progressWidth;
	private int max = 100;
	private int progress;
	private int start;
	private int end;
	private int imgwidth;
	boolean isTouching = false;
	private MyTouchlistener pt;
	private MyTouchlistener st;
	private MyTouchlistener et;
	private MyTouchlistener em;
	private MyTouchlistener sm;
	Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int x = (progressWidth * msg.what / 100) - (imgwidth / 2);
			if (x <= 0) {
				x = 0;
			}

			iv_progress.setX(x);

		}
	};

	public MyProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.myprogress, this);
		my_p = (MyprogressBrackGround) findViewById(R.id.my_progress);
		my_p.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						progressWidth = my_p.getMeasuredWidth();
						initview();
						initprogress();
						my_p.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);

					}
				});
		my_p.setOnConfigChangesListener(new ConfigChangesListener() {

			@Override
			public void ConfigChange() {
				progressWidth = getMeasuredWidth();
				Log.i(TAG, "end  " + end);
				Log.i(TAG, "max  " + max);
				Log.i(TAG, "pro  " + progressWidth);
				int endx = ((end * 100 / max)) * progressWidth / 100;
				if (endx >= progressWidth - imgwidth) {
					endx = progressWidth - imgwidth;
				}
				Log.i(TAG, "endx  " + endx);
				iv_end.setX(endx);
				iv_progress
						.setX(((progress * 100 / max)) * progressWidth / 100);
				iv_start.setX(((start * 100 / max)) * progressWidth / 100);

			}
		});

		iv_start = (ImageView) findViewById(R.id.iv_start);
		iv_progress = (ImageView) findViewById(R.id.iv_progress);
		iv_end = (ImageView) findViewById(R.id.iv_end);
		iv_end.measure(0, 0);
		imgwidth = iv_end.getMeasuredWidth();
		initTouchListener();
	}

	@SuppressLint("NewApi")
	private void initprogress() {
		float xp = iv_progress.getX() + imgwidth / 2;
		float xs = iv_start.getX() + imgwidth / 2;
		float xe = iv_end.getX() + imgwidth / 2;
		float pp = (xp * 100 / progressWidth);
		my_p.setPregross((int) pp);
		float ps = (xs * 100 / progressWidth);
		my_p.setStart((int) ps);
		float pe = (xe * 100 / progressWidth);
		my_p.setEnd((int) pe);
	}

	@SuppressLint("NewApi")
	private void initview() {
		iv_start.setX(0);
		start = 0;
		iv_end.setX(progressWidth - imgwidth);
		end = max;
	}

	@SuppressLint("NewApi")
	private void initTouchListener() {
		iv_start.setOnTouchListener(new OnTouchListener() {

			private float x1;
			private float x2;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x3 = iv_end.getX();
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					x1 = event.getRawX();
					break;
				case MotionEvent.ACTION_MOVE:
					x2 = event.getRawX();
					float x = x2 - x1;
					float moveTo = iv_start.getX() + x;
					if (moveTo >= progressWidth - (imgwidth)) {
						moveTo = progressWidth - (imgwidth);
					}
					if (moveTo <= 0) {
						moveTo = 0;
					}
					if (moveTo > x3 - (progressWidth / 10)) {
						moveTo = x3 - (progressWidth / 10);
					}
					iv_start.setX(moveTo);
					x1 = x2;
					initprogress();
					start = (int) (max * ((iv_start.getX() * 100 / progressWidth) / 100));
					if (sm != null) {
						sm.ProgressTouch(start);
					}
					break;
				case MotionEvent.ACTION_UP:
					start = (int) (max * ((iv_start.getX() * 100 / progressWidth) / 100));
					if (st != null) {
						Log.i(TAG, "MP内start" + start);
						Log.i(TAG, "mp内progress" + iv_start.getX());
						st.ProgressTouch(start);
					}

					break;

				default:
					break;
				}

				return true;
			}
		});
		iv_end.setOnTouchListener(new OnTouchListener() {
			private float x1;
			private float x2;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				float x3 = iv_start.getX();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					x1 = event.getRawX();
					break;
				case MotionEvent.ACTION_MOVE:
					x2 = event.getRawX();
					float x = x2 - x1;
					float moveTo = iv_end.getX() + x;
					if (moveTo >= progressWidth - (imgwidth)) {
						moveTo = progressWidth - (imgwidth);
					}
					if (moveTo <= 0) {
						moveTo = 0;
					}
					if (moveTo < x3 + (progressWidth / 10)) {
						moveTo = x3 + (progressWidth / 10);
					}
					iv_end.setX(moveTo);
					x1 = x2;
					initprogress();
					end = (int) (max * ((iv_end.getX() * 100 / progressWidth) / 100));

					if (em != null) {
						em.ProgressTouch(end);
					}
					break;
				case MotionEvent.ACTION_UP:
					end = (int) (max * ((iv_end.getX() * 100 / progressWidth) / 100));

					if (et != null) {
						et.ProgressTouch(end);
					}
					break;

				default:
					break;
				}

				return true;
			}
		});
		iv_progress.setOnTouchListener(new OnTouchListener() {
			private float x1;
			private float x2;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				isTouching = true;
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					x1 = event.getRawX();
					break;
				case MotionEvent.ACTION_MOVE:
					x2 = event.getRawX();
					float x = x2 - x1;
					float moveTo = iv_progress.getX() + x;
					if (moveTo >= progressWidth - (imgwidth)) {
						moveTo = progressWidth - (imgwidth);
					}
					if (moveTo <= 0) {
						moveTo = 0;
					}
					iv_progress.setX(moveTo);
					x1 = x2;
					initprogress();
					break;
				case MotionEvent.ACTION_UP:

					progress = (int) (max * (((iv_progress.getX() + imgwidth) * 100 / progressWidth) / 100));
					if (pt != null) {
						pt.ProgressTouch(progress);
					}
					isTouching = false;
					break;

				default:
					break;
				}

				return true;
			}
		});

	}

	public void setProgressOnTouchListener(MyTouchlistener l) {
		pt = l;
	}

	public void setStartOnTouchListener(MyTouchlistener l) {
		st = l;
	}

	public void setEndOnTouchListener(MyTouchlistener l) {
		et = l;
	}

	public void setEndOnMoveListener(MyTouchlistener l) {
		em = l;
	}

	public void setStartrOnMoveListener(MyTouchlistener l) {
		sm = l;
	}

	public void setStart(int start) {
		this.start = start;
		start = 100 * start / max;
		int x = (progressWidth * start / 100);
		iv_start.setX(x);

	}

	public void setProgress(int progress) {
		if (isTouching) {
			return;
		}
		progress = 100 * progress / max;

		Message msg = new Message();
		msg.what = progress;
		hand.sendMessage(msg);

		my_p.setPregross(progress);
	}

	public void setMax(int max) {
		this.max = max;
		updateStartAndEnd();
	}

	private void updateStartAndEnd() {
		start = (int) ((iv_start.getX() / progressWidth) * max);
		end = (int) ((iv_end.getX() / progressWidth) * max);
	}

	public interface MyTouchlistener {
		void ProgressTouch(int progeress);
	}

	public int getProgress() {
		return this.progress;
	}

}
