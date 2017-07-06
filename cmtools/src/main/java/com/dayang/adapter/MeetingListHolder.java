package com.dayang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dayang.cmtools.R;

/**
 * Created by 冯傲 on 2017/3/6.
 * e-mail 897840134@qq.com
 */

public class MeetingListHolder extends RecyclerView.ViewHolder {
    public ImageView iamge;
    public View view;
    public TextView text;

    public MeetingListHolder(View itemView) {
        super(itemView);
        view = itemView;
        iamge = (ImageView) itemView.findViewById(R.id.item_meeting_list_icon);
        text = (TextView) itemView.findViewById(R.id.item_meeting_list_name);
    }
}
