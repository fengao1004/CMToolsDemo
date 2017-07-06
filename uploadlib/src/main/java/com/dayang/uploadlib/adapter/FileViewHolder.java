package com.dayang.uploadlib.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dayang.uploadlib.R;

/**
 * Created by 冯傲 on 2017/6/15.
 * e-mail 897840134@qq.com
 */

class FileViewHolder extends RecyclerView.ViewHolder {
    public ImageView fileImage;
    public ImageView handleImage;
    public ProgressBar progressBar;
    public TextView fileName;
    public TextView missionStatus;
    public TextView speed;

    public FileViewHolder(View itemView) {
        super(itemView);
        fileImage = (ImageView) itemView.findViewById(R.id.iv_file_image);
        handleImage = (ImageView) itemView.findViewById(R.id.iv_handle_image);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_upload);
        fileName = (TextView) itemView.findViewById(R.id.tv_file_name);
        missionStatus = (TextView) itemView.findViewById(R.id.tv_mission_status);
        speed = (TextView) itemView.findViewById(R.id.tv_speed);

    }
}
