package com.dayang.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dayang.cmtools.R;
import com.dayang.info.FileUpdateInfo;
import com.dayang.info.FilesUpdateManager;

/**
 * Created by 冯傲 on 2017/2/7.
 * e-mail 897840134@qq.com
 */

public class UpdateFileListAdapter extends RecyclerView.Adapter<UpdateFileListAdapterViewHolder> {
    private Context context;
    private FilesUpdateManager infos;

    public UpdateFileListAdapter(Context context) {
        this.context = context;
        infos = FilesUpdateManager.getInstance();
    }

    @Override
    public UpdateFileListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_update_progress, parent, false);
        return new UpdateFileListAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final UpdateFileListAdapterViewHolder holder, int position) {

        final FileUpdateInfo fileUpdateInfo = infos.get(position);
        //上传对象的暂停继续功能，在点击事件中调用，上传线程中暂未实现，考虑下一个版本实现
        //fileUpdateInfo.pauseUpdate();
        //fileUpdateInfo.startUpdate();
        String fileName = fileUpdateInfo.fileName;
        holder.fileName.setText(fileName);
        if (fileUpdateInfo.hasProgress) {
            holder.updateProgressBar.setProgress(0);
            holder.updateProgressText.setText("0%");
            fileUpdateInfo.setProgressListener(new FileUpdateInfo.ProgressListener() {
                @Override
                public void progressUpdate(long progress) {
                    holder.updateProgressBar.setProgress((int) (progress * 100 / fileUpdateInfo.totalProgress));
                    holder.updateProgressText.setText(progress * 100 / fileUpdateInfo.totalProgress + "%");
                }
            });
            if (fileUpdateInfo.updateState == FileUpdateInfo.UPDATEING) {
                holder.updateState.setText("上传中");
            } else if (fileUpdateInfo.updateState == FileUpdateInfo.UPDATEING) {
                holder.updateState.setText("等待上传");
            }
            fileUpdateInfo.setUpdateStateChangeListener(new FileUpdateInfo.UpdateStateChangeListener() {
                @Override
                public void updateStateChange(int UpdateState) {
                    if (UpdateState == FileUpdateInfo.UPDATEING) {
                        holder.updateState.setText("上传中");
                    } else if (UpdateState == FileUpdateInfo.UPDATEING) {
                        holder.updateState.setText("等待上传");
                    }
                }
            });
        } else {
            holder.updateProgressBar.setProgress(0);
            holder.updateProgressText.setText("上传中,未知进度");
        }

    }

    @Override
    public int getItemCount() {
        return infos.size();
    }
}
