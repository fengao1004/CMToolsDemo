package com.dayang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dayang.cmtools.R;
import com.dayang.info.MeetingListInfo;
import com.quanshi.sdk.BaseResp;
import com.quanshi.sdk.ConferenceReq;
import com.quanshi.sdk.TangCallback;
import com.quanshi.sdk.TangInterface;

import java.util.ArrayList;

/**
 * Created by 冯傲 on 2017/3/6.
 * e-mail 897840134@qq.com
 */

public class MeetingListAdapter extends RecyclerView.Adapter<MeetingListHolder> {
    ArrayList<MeetingListInfo> list;
    Context context;

    public MeetingListAdapter(Context context, ArrayList<MeetingListInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MeetingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meeting_list, parent, false);
        return new MeetingListHolder(view);
    }

    @Override
    public void onBindViewHolder(MeetingListHolder holder, final int position) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = list.get(position).password;
                String account = list.get(position).account;
                Log.i("fengao", "onClick: password" + password);
                Log.i("fengao", "onClick: account" + account);
                ConferenceReq req = new ConferenceReq("", account, password);
                req.setCallSelf(false);
                TangInterface.joinConference(context, req, new TangCallback() {
                    @Override
                    public void onCallback(BaseResp baseResp) {

                    }
                });
            }
        });
        MeetingListInfo meetingListInfo = list.get(position);
        holder.text.setText(meetingListInfo.name);
//        if (meetingListInfo.imgType == meetingListInfo.IMGTYPE_BITMAP) {
//            holder.iamge.setImageBitmap(meetingListInfo.bitmap);
//        } else if (meetingListInfo.imgType == meetingListInfo.IMGTYPE_RESOURCE) {
        holder.iamge.setImageResource(R.drawable.conference);
//        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
