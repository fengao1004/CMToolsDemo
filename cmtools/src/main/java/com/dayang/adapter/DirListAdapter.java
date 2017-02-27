package com.dayang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dayang.cmtools.R;
import com.dayang.info.FileDir;
import com.dayang.util.ThumbnailUtil;
import com.dayang.util.TypeUtils;
import com.dayang.view.ActionSheetDialog;

import java.util.ArrayList;

/**
 * Created by 冯傲 on 2016/10/12.
 * e-mail 897840134@qq.com
 */
public class DirListAdapter extends BaseAdapter{
    ArrayList<FileDir> list ;
    Context context;
    ActionSheetDialog dialog;
    ActionSheetDialog.OnClickListener l ;
    public DirListAdapter(ActionSheetDialog.OnClickListener l ,ArrayList<FileDir> list, Context context,ActionSheetDialog dialog) {
        this.list = list;
        this.context = context;
        this.l = l;
        this.dialog =dialog;
    }

    public void setL(ActionSheetDialog.OnClickListener l) {
        this.l = l;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dirdialog, parent, false);
        }
        ((RelativeLayout)convertView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(l!=null){
                    l.onClick(position);
                }
                dialog.dismiss();
            }
        });
        TextView name = (TextView) convertView.findViewById(R.id.tv_dirname);
        TextView tv_number = (TextView) convertView.findViewById(R.id.tv_number);
        tv_number.setText("一共"+list.get(position).files.size()+"张");
        name.setText(list.get(position).name);
        ImageView lv_dir_list = (ImageView) convertView.findViewById(R.id.iv_dir);
        lv_dir_list.setImageResource(R.drawable.picture);
        int fileType = TypeUtils.getFileType(list.get(position).files.get(0));
        if(fileType==TypeUtils.VIDIO){
            lv_dir_list.setImageResource(R.drawable.vedio);
        }else if(fileType==TypeUtils.ADIOU){
            lv_dir_list.setImageResource(R.drawable.musci);
        }else {
            ThumbnailUtil.displayThumbnail(lv_dir_list,list.get(position).files.get(0));

        }
        return convertView;
    }
}
