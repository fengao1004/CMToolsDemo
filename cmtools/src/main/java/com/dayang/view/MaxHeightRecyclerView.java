package com.dayang.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * 这个RecyclerView最大的高度为屏幕的1.2倍填补了RecyclerView没有maxHeight的问题，但是这个控件没暴露出设置最大高度的属性所以它的最大高度是固定的不能更高
 * Created by 冯傲 on 2017/2/10.
 * e-mail 897840134@qq.com
 */

public class MaxHeightRecyclerView extends RecyclerView {
    public MaxHeightRecyclerView(Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxHeightRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        float width = getWidth();
        float height = width * 1.2f;
        int maxHeightSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, maxHeightSpec);
    }
}
