package com.dayang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dayang.cmtools.R;

/**
 * Created by 冯傲 on 2017/2/7.
 * e-mail 897840134@qq.com
 */

public class UpdateFileListAdapterViewHolder extends RecyclerView.ViewHolder {
    public TextView fileName;
    public TextView updateProgressText;
    public TextView updateState;
    public ProgressBar updateProgressBar;

    public UpdateFileListAdapterViewHolder(View itemView) {
        super(itemView);
        fileName = (TextView) itemView.findViewById(R.id.tv_fileName);
        updateProgressText = (TextView) itemView.findViewById(R.id.tv_progress);
        updateProgressBar = (ProgressBar) itemView.findViewById(R.id.update_progressBar);
        updateState = (TextView) itemView.findViewById(R.id.update_state);
    }
}
