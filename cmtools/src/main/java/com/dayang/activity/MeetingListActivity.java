package com.dayang.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dayang.cmtools.R;

public class MeetingListActivity extends AppCompatActivity {
//    final int MEETIONGLISTGETSUCCESS = 123;
//    private RecyclerView meeting_list;
//    String meetingListRequestUrl = "";
//    boolean joinMeeting = false;
//    private MeetingListResponseInfo meetingListResponseInfo;
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            int what = msg.what;
//            switch (what) {
//                case MEETIONGLISTGETSUCCESS:
//                    swipe_refresh_widget.setRefreshing(false);
//                    ArrayList<MeetingListInfo> list = new ArrayList<>();
//                    List<MeetingListResponseInfo.RecordsEntity> records = meetingListResponseInfo.getRecords();
//                    for (int i = 0; i < records.size(); i++) {
//                        list.add(new MeetingListInfo(records.get(i).getName(), records.get(i).getAccount(), records.get(i).getPcode()));
//                    }
//                    meeting_list.setAdapter(new MeetingListAdapter(MeetingListActivity.this, list));
//                    break;
//            }
//        }
//    };
//    private SwipeRefreshLayout swipe_refresh_widget;
//    private String workNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metting_list);
//        meeting_list = (RecyclerView) findViewById(R.id.meeting_list);
//        swipe_refresh_widget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
//        findViewById(R.id.back_from_list).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        meeting_list.setLayoutManager(new LinearLayoutManager(this));
//        swipe_refresh_widget.setColorSchemeResources(R.color.cmtools_blue,
//                R.color.audio_background, R.color.bule_overlay
//        );
//        workNo = getIntent().getStringExtra("workNo");
//        swipe_refresh_widget.setOnRefreshListener(this);
//        swipe_refresh_widget.setRefreshing(true);
//        getMeetingListRequestUrl();
    }
//
//    private void getMeetingListRequestUrl() {
//        if (meetingListRequestUrl.equals("")) {
//            OkHttpUtil okHttpUtil = new OkHttpUtil();
//            okHttpUtil.call("https://apphttps.dayang.com:9443/portal/api/projService", "{\"domainName\":\"houjian.com\",\"productCode\":\"cmtools\"}", new OkHttpUtil.OkHttpCallBack() {
//                @Override
//                public void success(Response response) throws Exception {
//                    Gson gson = new Gson();
//                    try {
//                        PortalResponseInfo portalResponseInfo = gson.fromJson(response.body().string(), PortalResponseInfo.class);
//                        meetingListRequestUrl = portalResponseInfo.getData().getCasservice();
//                        response.body().close();
//                        meetingListRequestUrl = meetingListRequestUrl + "?workNo=" + workNo;
//                        getMeetingList();
//                    } catch (Exception e) {
//                        Log.i("fengao", "请求错误");
//                    }
//                }
//
//                @Override
//                public void error(Request request, IOException e) {
//                    Log.i("fengao", "error: " + e.toString());
//                }
//            });
//        } else {
//            getMeetingList();
//        }
//
//    }
//
//    public void setJoinMeeting(boolean joinMeeting) {
//        this.joinMeeting = joinMeeting;
//    }
//
//    public void getMeetingList() {
//
//        OkHttpUtil okHttpUtil = new OkHttpUtil();
//
//        Log.i("fengao", "getMeetingList: " + meetingListRequestUrl);
//        okHttpUtil.callGet(meetingListRequestUrl, new OkHttpUtil.OkHttpCallBack() {
//            @Override
//            public void success(Response response) throws Exception {
//                String json = response.body().string();
//                Log.i("fengao", "success: " + json);
//                Gson gson = new Gson();
//                meetingListResponseInfo = gson.fromJson(json, MeetingListResponseInfo.class);
//                Message message = new Message();
//                message.what = MEETIONGLISTGETSUCCESS;
//                handler.sendMessage(message);
//            }
//
//            @Override
//            public void error(Request request, IOException e) {
//                Log.i("fengao", "error: " + e.toString());
//            }
//        });
//    }
//
//    @Override
//    public void onRefresh() {
//        Log.i("fengao", "onRefresh: 刷新了");
//        getMeetingListRequestUrl();
//    }
}
