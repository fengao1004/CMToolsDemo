package com.dayang.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;
/**
 * 重构ProgressBar控件，在控件上添加
 * 百分比数据展示
 * @author 任育伟
 *
 */
public class TextProgressBar extends ProgressBar {

	private String text;
	private Paint mPaint;

	public TextProgressBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initText();
	}

	public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initText();
	}

	public TextProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initText();
	}
   /**
    * 进度条上画出百分比数据展示界面
    */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Rect rect = new Rect();

		this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
		int x = (getWidth() / 2) - rect.centerX();
		int y = (getHeight() / 2) - rect.centerY();
		canvas.drawText(this.text, x, y, this.mPaint);
	}

	@Override
	public synchronized void setProgress(int progress) {
		// TODO Auto-generated method stub
		setText(progress);
		super.setProgress(progress);
	}

	// 初始化，画笔
	private void initText() {
		this.mPaint = new Paint();
		this.mPaint.setFakeBoldText(true);
		this.mPaint.setColor(Color.BLUE);
		this.mPaint.setTextSize(30);
	}

	private void setText() {
		setText(this.getProgress());
	}

	// 设置文字内容
	private void setText(int progress) {
		this.text = String.valueOf(progress) + "%";
	}
}
