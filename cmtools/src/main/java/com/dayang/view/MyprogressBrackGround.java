package com.dayang.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dayang.common.Canstant;

public class MyprogressBrackGround extends View {

    private static final String TAG =  Canstant.TAG;
    private ConfigChangesListener configChangesListener = null;
    private int max = 100;
    private int type = 0;
    private int pregrossWhith;
    private int pregross = 30;
    private int imageWhith = 0;
    private int pregrossPx;
    private int start = 45;
    private int end = 90;
    private String color1 = "#53ddfa";
    private String color2 = "#57C9FA";
    private String color3 = "#AAF89C51";

    private int miss = 5;// ���϶�Բ��������λ��
    Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    };
    private int pl;
    private int pr;

    public MyprogressBrackGround(Context context) {
        super(context);
        measure(0, 0);
    }

    public MyprogressBrackGround(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        measure(0, 0);
    }

    public MyprogressBrackGround(Context context, AttributeSet attrs) {
        super(context, attrs);
        measure(0, 0);
    }

    public int getPregross() {
        return pregross;
    }

    public void setPregross(int pregross) {
        this.pregross = pregross;
        Message msg = new Message();
        msg.what = 1;
        hand.sendMessage(msg);
    }

    public int getStart() {
        return start;
    }

    public void setImageWhith(int imageWhith) {
        this.imageWhith = imageWhith;
    }

    public void setStart(int start) {
        this.start = start;
        Message msg = new Message();
        msg.what = 1;
        hand.sendMessage(msg);
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
        Message msg = new Message();
        msg.what = 1;
        hand.sendMessage(msg);
    }

    public int getMax() {
        return max;
    }

    public void setType(int type) {
        this.type = type;
        if (type == 1) {
            miss = 0;
            setBackgroundColor(Color.parseColor("#666666"));
            color1="#444444";
        }

    }

    public void setMax(int max) {
        this.max = max;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (pregrossWhith != 0 && pregrossWhith != getMeasuredWidth()) {
            pregrossWhith = getMeasuredWidth();
            configChangesListener.ConfigChange();
            Log.i(TAG, "横竖屏切换了");
        } else if (pregrossWhith == 0) {
            pregrossWhith = getMeasuredWidth();
        }
        Paint paint1 = new Paint();
        Paint paint2 = new Paint();
        Paint paint3 = new Paint();
        paint1.setColor(Color.parseColor(color1));
        paint2.setColor(Color.parseColor(color2));
        paint3.setColor(Color.parseColor(color3));
        int height = getMeasuredHeight();

        int pro = (pregrossWhith * (pregross * 100 / max)) / 100;
        if (pro < miss) {
            pro = miss;
        }
        if (pro > pregrossWhith - miss) {
            pro = pregrossWhith - miss;
        }
        if (type == 0) {
            canvas.drawRect(miss, height / 2 - 2, pregrossWhith - miss,
                    height / 2 + 2, paint1);
        } else if (type == 1) {
            canvas.drawRect(0, 2, pregrossWhith,
                    height-2, paint1);
        }


        pl = (pregrossWhith * (start * 100 / max)) / 100 + miss;

        pr = pro + miss + imageWhith;

        if (pl >= pr) {
            pr = pl;
        }
        canvas.drawRect(pl, 0, pr, height, paint2);

        canvas.drawRect((pregrossWhith * (start * 100 / max)) / 100, 0,
                (pregrossWhith * (end * 100 / max)) / 100, height, paint3);

    }

    public void setOnConfigChangesListener(
            ConfigChangesListener configChangesListener) {
        this.configChangesListener = configChangesListener;
    }

    public interface ConfigChangesListener {
        void ConfigChange();
    }

}
