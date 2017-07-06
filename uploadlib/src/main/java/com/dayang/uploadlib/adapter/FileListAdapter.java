package com.dayang.uploadlib.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dayang.uploadlib.R;
import com.dayang.uploadlib.UploadFileManager;
import com.dayang.uploadlib.model.MissionInfo;
import com.dayang.uploadlib.util.ThumbnailUtil;

import java.io.File;
import java.util.List;

import static com.dayang.uploadlib.task.FtpUploadTask.TAG;

/**
 * Created by 冯傲 on 2017/6/15.
 * e-mail 897840134@qq.com
 */

public class FileListAdapter extends RecyclerView.Adapter<FileViewHolder> {
    Context context;
    List<MissionInfo> list;
    DeleteListener deleteListener;

    public FileListAdapter(Context context, List<MissionInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final FileViewHolder holder, int position) {
        final MissionInfo missionInfo = list.get(position);
        holder.fileName.setText(new File(missionInfo.getFilePath()).getName());
        holder.progressBar.setMax(missionInfo.getLength());
        holder.progressBar.setProgress(missionInfo.getProgress());
        int status = missionInfo.getStatus();
        switch (status) {
            case MissionInfo.UPLOADERROR:
                holder.missionStatus.setText("上传失败");
                holder.handleImage.setImageResource(R.drawable.messages_queue_state_failed);
                if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                    holder.speed.setText("进度未知");
                } else {
                    int progress = missionInfo.getProgress();
                    int length = missionInfo.getLength();
                    progress = length == 0 ? 0 : progress * 100 / length;
                    holder.speed.setText(progress + "%");
                }
                break;
            case MissionInfo.UPLOADCOMPLETED:
                holder.missionStatus.setText("上传成功");
                holder.handleImage.setImageResource(R.drawable.music_shop_pitch_on_highlighted);
                holder.progressBar.setMax(100);
                holder.progressBar.setProgress(100);
                holder.speed.setText("100%");
                break;
            case MissionInfo.UPLOADING:
                holder.missionStatus.setText("上传中");
                holder.handleImage.setImageResource(R.drawable.icon_player_pause);
                holder.speed.setText(missionInfo.getSpeed() + " k/s");
                if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                    holder.speed.setText("进度未知");
                } else {
                    int progress = missionInfo.getProgress();
                    int length = missionInfo.getLength();
                    progress = length == 0 ? 0 : progress * 100 / length;
                    holder.speed.setText(progress + "%");
                }
                break;
            case MissionInfo.WAITINGNETWORDK:
                holder.missionStatus.setText("等待网络");
                holder.handleImage.setImageResource(R.drawable.compose_emotion_table_recent);
                if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                    holder.speed.setText("进度未知");
                } else {
                    int progress = missionInfo.getProgress();
                    int length = missionInfo.getLength();
                    progress = length == 0 ? 0 : progress * 100 / length;
                    holder.speed.setText(progress + "%");
                }
                break;
            case MissionInfo.WAITINGNETWORDK_WIFI:
                holder.missionStatus.setText("等待WIFI");
                holder.handleImage.setImageResource(R.drawable.compose_emotion_table_recent);
                if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                    holder.speed.setText("进度未知");
                } else {
                    int progress = missionInfo.getProgress();
                    int length = missionInfo.getLength();
                    progress = length == 0 ? 0 : progress * 100 / length;
                    holder.speed.setText(progress + "%");
                }
                break;
            case MissionInfo.WAITINGUPLOAD:
                holder.missionStatus.setText("等待上传");
                holder.handleImage.setImageResource(R.drawable.compose_emotion_table_recent);
                if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                    holder.speed.setText("进度未知");
                } else {
                    int progress = missionInfo.getProgress();
                    int length = missionInfo.getLength();
                    progress = length == 0 ? 0 : progress * 100 / length;
                    holder.speed.setText(progress + "%");
                }
                break;
            case MissionInfo.PAUSEING:
                holder.missionStatus.setText("暂停上传");
                holder.handleImage.setImageResource(R.drawable.icon_player_play);
                if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                    holder.speed.setText("进度未知");
                } else {
                    int progress = missionInfo.getProgress();
                    int length = missionInfo.getLength();
                    progress = length == 0 ? 0 : progress * 100 / length;
                    holder.speed.setText(progress + "%");
                }
                break;
        }
        missionInfo.setUploadStatueListener(new MissionInfo.UploadStatusListener() {
            @Override
            public void uploadStatus(int status, int progress, String message) {
                holder.progressBar.setProgress(progress);
                switch (status) {
                    case MissionInfo.UPLOADERROR:
                        holder.missionStatus.setText("上传失败");
                        holder.handleImage.setImageResource(R.drawable.messages_queue_state_failed);
                        holder.speed.setText("");
                        break;
                    case MissionInfo.UPLOADCOMPLETED:
                        holder.missionStatus.setText("上传成功");
                        holder.progressBar.setProgress(missionInfo.getLength());
                        holder.handleImage.setImageResource(R.drawable.music_shop_pitch_on_highlighted);
                        holder.speed.setText("100%");
                        break;
                    case MissionInfo.UPLOADING:
                        holder.missionStatus.setText("上传中");
                        if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                            holder.handleImage.setImageResource(R.drawable.icon_player_pause);
                            holder.speed.setText("进度未知");
                        } else {
                            holder.handleImage.setImageResource(R.drawable.icon_player_pause);
                            if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                                holder.speed.setText("进度未知");
                            } else {
                                int progress1 = missionInfo.getProgress();
                                int length = missionInfo.getLength();
                                progress1 = length == 0 ? 0 : progress1 * 100 / length;
                                holder.speed.setText(progress1 + "%");
                            }
                        }
                        break;
                    case MissionInfo.WAITINGNETWORDK:
                        holder.missionStatus.setText("等待网络");
                        holder.handleImage.setImageResource(R.drawable.compose_emotion_table_recent);
                        if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                            holder.speed.setText("进度未知");
                        } else {
                            int progress1 = missionInfo.getProgress();
                            int length = missionInfo.getLength();
                            progress1 = length == 0 ? 0 : progress1 * 100 / length;
                            holder.speed.setText(progress1 + "%");
                        }
                        break;
                    case MissionInfo.WAITINGNETWORDK_WIFI:
                        holder.missionStatus.setText("等待WIFI");
                        holder.handleImage.setImageResource(R.drawable.compose_emotion_table_recent);
                        if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                            holder.speed.setText("进度未知");
                        } else {
                            int progress1 = missionInfo.getProgress();
                            int length = missionInfo.getLength();
                            progress1 = length == 0 ? 0 : progress1 * 100 / length;
                            holder.speed.setText(progress1 + "%");
                        }
                        break;
                    case MissionInfo.WAITINGUPLOAD:
                        holder.missionStatus.setText("等待上传");
                        holder.handleImage.setImageResource(R.drawable.compose_emotion_table_recent);
                        if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                            holder.speed.setText("进度未知");
                        } else {
                            int progress1 = missionInfo.getProgress();
                            int length = missionInfo.getLength();
                            progress1 = length == 0 ? 0 : progress1 * 100 / length;
                            holder.speed.setText(progress1 + "%");
                        }
                        break;
                    case MissionInfo.PAUSEING:
                        holder.missionStatus.setText("暂停上传");
                        holder.handleImage.setImageResource(R.drawable.icon_player_play);
                        if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                            holder.speed.setText("进度未知");
                        } else {
                            int progress1 = missionInfo.getProgress();
                            int length = missionInfo.getLength();
                            progress1 = length == 0 ? 0 : progress1 * 100 / length;
                            holder.speed.setText(progress1 + "%");
                        }
                        break;
                }
            }
        });
        holder.handleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = missionInfo.getStatus();
                Log.i(TAG, "onClick: " + status);
                switch (status) {
                    case MissionInfo.UPLOADERROR:
                        UploadFileManager.getInstance().startMission(missionInfo);
                        break;
                    case MissionInfo.UPLOADCOMPLETED:
                        break;
                    case MissionInfo.UPLOADING:
                        Log.i(TAG, "onClick: pause");
                        if (missionInfo.getUpLoadType() == MissionInfo.UPLOADTYPE_HTTP) {
                            Toast.makeText(context, "此任务无法暂停", 0).show();
                        } else {
                            missionInfo.pauseMission();
                        }
                        break;
                    case MissionInfo.WAITINGNETWORDK:
                        missionInfo.pauseMission();
                        break;
                    case MissionInfo.WAITINGNETWORDK_WIFI:
                        missionInfo.pauseMission();
                        break;
                    case MissionInfo.WAITINGUPLOAD:
                        missionInfo.pauseMission();
                        break;
                    case MissionInfo.PAUSEING:
                        UploadFileManager.getInstance().startMission(missionInfo);
                        break;
                }
            }
        });
        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (deleteListener != null) {
                    deleteListener.delete(missionInfo);
                }
                return false;
            }
        });
        ThumbnailUtil.displayThumbnail(holder.fileImage, missionInfo.getFilePath());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public interface DeleteListener {
        void delete(MissionInfo missionInfo);
    }
}
