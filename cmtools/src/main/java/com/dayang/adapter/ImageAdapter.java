package com.dayang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.dayang.activity.PickImageActivity;
import com.dayang.activity.PlayAudioActivity;
import com.dayang.activity.PlayerActivity;
import com.dayang.cmtools.R;
import com.dayang.info.FileDir;
import com.dayang.util.ThumbnailUtil;
import com.dayang.util.TypeUtils;

import java.util.ArrayList;

/**
 * Created by 冯傲 on 2016/10/11.
 * e-mail 897840134@qq.com
 */
public class ImageAdapter extends RecyclerView.Adapter {
    FileDir fileDir ;
    Context context;
    int width;
    private final ArrayList<String> files;

    public ImageAdapter(FileDir fileDir,Context context) {
        this.fileDir = fileDir;
        this.context =context;
        files = ((PickImageActivity) context).getUpdataFiles();
        DisplayMetrics metric = new DisplayMetrics();
        ((PickImageActivity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(files.contains(fileDir.files.get(position))){
            ((MyHolder)holder).che.setChecked(true);
        }else {
            ((MyHolder)holder).che.setChecked(false);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ((MyHolder) holder).ima.getLayoutParams();
        layoutParams.width = width/3;
        layoutParams.height = width/3;
        ((MyHolder) holder).ima.setLayoutParams(layoutParams);
        ((MyHolder)holder).ima.setImageResource(R.drawable.white);
        final int index = position;
        int fileType = TypeUtils.getFileType(fileDir.files.get(position));
        if(fileType==TypeUtils.ADIOU){
            ((MyHolder)holder).ima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  intent = new Intent(context, PlayAudioActivity.class);
                    intent.putExtra("path", fileDir.files.get(position));
                    context.startActivity(intent);
                }
            });
            String[] split = fileDir.files.get(position).split("/");
            ((MyHolder) holder).adiouName.setText(split[split.length-1]);
            ((MyHolder) holder).adiouName.setVisibility(View.VISIBLE);
        }else {
            ((MyHolder) holder).adiouName.setVisibility(View.GONE);
        }
        if(fileType==TypeUtils.VIDIO){
            ((MyHolder)holder).ima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("path", fileDir.files.get(position));
                    context.startActivity(intent);
                }
            });
            ((MyHolder) holder).play.setVisibility(View.VISIBLE);
        }else {
            ((MyHolder) holder).play.setVisibility(View.GONE);

        }
        if(fileType==TypeUtils.IMAGE){
            ((MyHolder)holder).ima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        ThumbnailUtil.displayThumbnail(((MyHolder)holder).ima,fileDir.files.get(position));
        ((MyHolder)holder).che.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyHolder)holder).che.setChecked(((PickImageActivity) context).addFile(fileDir.files.get(index)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileDir.files.size();
    }
    class MyHolder extends RecyclerView.ViewHolder {
        public ImageView ima;
        public ImageView play;
        public TextView adiouName;

        public CheckBox che;
        public MyHolder(View itemView) {
            super(itemView);
            ima = (ImageView) itemView.findViewById(R.id.image_file);
            che = (CheckBox) itemView.findViewById(R.id.cb);
            play = (ImageView) itemView.findViewById(R.id.iv_play);
            adiouName = (TextView)itemView.findViewById(R.id.tv_audioname);
        }

    }
}
