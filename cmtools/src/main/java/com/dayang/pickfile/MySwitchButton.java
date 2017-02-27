package com.dayang.pickfile;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 自定义滑动开关
 */
public class MySwitchButton extends View implements OnTouchListener {

	private Bitmap switchOnBkg; // 开关开启时的背景
	private Bitmap switchOffBkg; // 开关关闭时的背景
	private Bitmap slipSwitchButton; // 滑动开关的图片
	private Rect onRect; // 左半边矩形
	private Rect offRect; // 右半边矩形

	private boolean isSlipping = false; // 是否正在滑动
	private boolean isSwitchOn = false; // 当前开关的状态，true表示开启，flase表示关闭
	private float previousX; // 手指按下时的水平坐标x
	private float currentX; // 当前的水平坐标X

	private ArrayList<OnSwitchListener> onSwitchListenerList; // 开关监听器

	public MySwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		this.setOnTouchListener(this); // 设置触摸监听器
		onSwitchListenerList = new ArrayList<OnSwitchListener>();
	}

	public void setImageResource(int switchBkg, int slipBtn) {
		switchOnBkg = BitmapFactory.decodeResource(this.getResources(),
				switchBkg);
		switchOffBkg = BitmapFactory.decodeResource(this.getResources(),
				switchBkg);
		slipSwitchButton = BitmapFactory.decodeResource(this.getResources(),
				slipBtn);

		// 右半边rect，滑动开关在右半边时表示开启
		onRect = new Rect(switchOnBkg.getWidth() - slipSwitchButton.getWidth(),
				0, switchOnBkg.getWidth(), slipSwitchButton.getHeight());
		// 左半边rect，滑动开关在左半边时表示关闭
		offRect = new Rect(0, 0, slipSwitchButton.getWidth(),
				slipSwitchButton.getHeight());
	}

	public void setSwitchState(boolean switchState) {
		this.isSwitchOn = switchState;
		this.invalidate();
	}

	public boolean getSwitchState() {
		return this.isSwitchOn;
	}
	
	public void setOnSwitchStateListener(OnSwitchListener listener){
		onSwitchListenerList.add(listener);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Matrix matrix = new Matrix();
		Paint paint = new Paint();

		float leftSlipBtnX; // 滑动按钮的左边坐标

		System.out.println("currentX=" + currentX + " switchOnBkg.width="
				+ switchOnBkg.getWidth());

		canvas.drawBitmap(switchOnBkg, matrix, paint);

		if (isSlipping) {
			// 如果正在滑动
			if (currentX > switchOnBkg.getWidth()) {
				leftSlipBtnX = switchOnBkg.getWidth()
						- slipSwitchButton.getWidth();
			} else {
				leftSlipBtnX = currentX - slipSwitchButton.getWidth();
			}
		} else {
			if (isSwitchOn) {
				leftSlipBtnX = switchOnBkg.getWidth()
						- slipSwitchButton.getWidth();
			} else {
				leftSlipBtnX = 0;
			}
		}

		if (leftSlipBtnX < 0) {
			leftSlipBtnX = 0;
		} else if (leftSlipBtnX > switchOnBkg.getWidth()
				- slipSwitchButton.getWidth()) {
			leftSlipBtnX = switchOnBkg.getWidth() - slipSwitchButton.getWidth();
		}

		canvas.drawBitmap(slipSwitchButton, leftSlipBtnX, 0, paint);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(switchOnBkg.getWidth(), switchOnBkg.getHeight());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			currentX = event.getX();
			break;
		case MotionEvent.ACTION_DOWN:
			isSlipping = true;
			break;
		case MotionEvent.ACTION_UP:
			isSlipping = false;
			boolean previousState = isSwitchOn;
			if (event.getX() > (switchOnBkg.getWidth() / 2)) {
				isSwitchOn = true;
			} else {
				isSwitchOn = false;
			}

			if(previousState != isSwitchOn){
				if(onSwitchListenerList.size() > 0){
					for(OnSwitchListener listener : onSwitchListenerList){
						listener.onSwitched(isSwitchOn);
					}
				}
			}
			break;

		default:
			break;
		}

		this.invalidate();
		return true;
	}

}
