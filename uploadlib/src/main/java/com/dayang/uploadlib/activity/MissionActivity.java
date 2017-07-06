package com.dayang.uploadlib.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dayang.uploadlib.R;
import com.dayang.uploadlib.UploadFileManager;
import com.dayang.uploadlib.adapter.FileListAdapter;
import com.dayang.uploadlib.model.MissionInfo;
import com.zcw.togglebutton.ToggleButton;

import java.util.List;

public class MissionActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_setting;
    private RecyclerView rv_mission_list;
    private LinearLayout ll_menu_root;
    private ImageView iv_shadow;
    private ImageView iv_back;
    private ToggleButton allow4GUpload;
    private ToggleButton startAppAutoUpload;
    private TextView tv_delete_completed;
    private TextView tv_pause_all;
    private List<MissionInfo> missionList;
    private FileListAdapter fileListAdapter;
    private LinearLayout ll_delete;
    private MissionInfo deleteMissionInfo;
    private TextView tv_delete;
    private TextView tv_cancel;
    private TextView tv_none_mission;
    private TextView tv_thread_count;
    private ImageView iv_add_thread_count;
    private ImageView iv_delete_thread_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        initView();
    }

    private void initView() {
        missionList = UploadFileManager.getInstance().getMissions();
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        ll_menu_root = (LinearLayout) findViewById(R.id.ll_menu_root);
        iv_shadow = (ImageView) findViewById(R.id.iv_shadow);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        allow4GUpload = (ToggleButton) findViewById(R.id.toggle_allow_4g_upload);
        startAppAutoUpload = (ToggleButton) findViewById(R.id.toggle_start_app_auto_upload);
        tv_delete_completed = (TextView) findViewById(R.id.tv_delete_completed);
        tv_none_mission = (TextView) findViewById(R.id.tv_none_mission);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_thread_count = (TextView) findViewById(R.id.tv_thread_count);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_pause_all = (TextView) findViewById(R.id.tv_pause_all);
        ll_delete = (LinearLayout) findViewById(R.id.ll_delete);
        ll_delete.setVisibility(View.GONE);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_add_thread_count = (ImageView) findViewById(R.id.iv_add_thread_count);
        iv_delete_thread_count = (ImageView) findViewById(R.id.iv_delete_thread_count);
        rv_mission_list = (RecyclerView) findViewById(R.id.rv_mission_list);
        fileListAdapter = new FileListAdapter(this, missionList);
        tv_thread_count.setText(UploadFileManager.getInstance().getThreadCount() + "");

        if (missionList.size() == 0) {
            rv_mission_list.setVisibility(View.GONE);
            tv_none_mission.setVisibility(View.VISIBLE);
        }
        fileListAdapter.setDeleteListener(new FileListAdapter.DeleteListener() {
            @Override
            public void delete(MissionInfo missionInfo) {
                deleteMissionInfo = missionInfo;
                ll_delete.setVisibility(View.VISIBLE);
                iv_shadow.setVisibility(View.VISIBLE);
                iv_shadow.setClickable(true);
            }
        });
        rv_mission_list.setAdapter(fileListAdapter);
        rv_mission_list.setLayoutManager(new LinearLayoutManager(this));
        ll_menu_root.setVisibility(View.GONE);
        iv_shadow.setVisibility(View.GONE);
        iv_setting.setOnClickListener(this);
        iv_shadow.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_add_thread_count.setOnClickListener(this);
        iv_delete_thread_count.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_delete_completed.setOnClickListener(this);
        if (UploadFileManager.getInstance().get4gStrategy()) {
            allow4GUpload.setToggleOff();
        } else {
            allow4GUpload.setToggleOn();
        }
        if (UploadFileManager.getInstance().getStartAppMode()) {
            startAppAutoUpload.setToggleOn();
        } else {
            startAppAutoUpload.setToggleOff();
        }
        allow4GUpload.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                UploadFileManager.getInstance().set4gStrategy(!on);
            }
        });
        startAppAutoUpload.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                UploadFileManager.getInstance().setStartAppMode(on);
            }
        });
        int flag = -1;
        for (MissionInfo missionInfo : missionList) {
            if (missionInfo.getStatus() == MissionInfo.UPLOADING
                    || missionInfo.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI
                    || missionInfo.getStatus() == MissionInfo.WAITINGUPLOAD
                    || missionInfo.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI) {
                tv_pause_all.setText("全部暂停");
                flag = 1;
                break;
            }
        }
        if (flag == -1)
            tv_pause_all.setText("全部开始");
        tv_pause_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            back();
            return;
        }
        if (id == R.id.iv_setting) {
            setting();
            return;
        }
        if (id == R.id.iv_shadow) {
            closeSetting();
            return;
        }
        if (id == R.id.tv_delete_completed) {
            deleteCompleteMission();
            closeSetting();

            return;
        }
        if (id == R.id.tv_cancel) {
            cancel();
            return;
        }
        if (id == R.id.tv_delete) {
            delete();
            return;
        }
        if (id == R.id.iv_add_thread_count) {
            addThreadCount();
            return;
        }
        if (id == R.id.iv_delete_thread_count) {
            delThreadCount();
            return;
        }
        if (id == R.id.tv_pause_all) {
            if (missionList.size() == 0) {
                Toast.makeText(this, "没有进行中的任务", Toast.LENGTH_SHORT).show();
                return;
            }
            pauseAllClick();
            closeSetting();
            return;
        }
    }

    private void delThreadCount() {
        int threadCount = UploadFileManager.getInstance().getThreadCount();
        if (threadCount > 1 && threadCount <= 5) {
            threadCount = threadCount - 1;
            UploadFileManager.getInstance().setMaxUploadMissionCount(threadCount);
        } else {
            Toast.makeText(this, "最小为1", 0).show();
        }
        tv_thread_count.setText(UploadFileManager.getInstance().getThreadCount()+"");
    }

    private void addThreadCount() {
        int threadCount = UploadFileManager.getInstance().getThreadCount();
        if (threadCount > 0 && threadCount < 5) {
            threadCount = threadCount + 1;
            UploadFileManager.getInstance().setMaxUploadMissionCount(threadCount);
        } else {
            Toast.makeText(this, "最大为5", 0).show();
        }
        tv_thread_count.setText(UploadFileManager.getInstance().getThreadCount()+"");
    }

    private void cancel() {
        deleteMissionInfo = null;
        closeSetting();
    }

    private void delete() {
        if (deleteMissionInfo != null) {
            fileListAdapter.notifyItemRemoved(missionList.indexOf(deleteMissionInfo));
            UploadFileManager.getInstance().deleteMission(deleteMissionInfo);
            deleteMissionInfo = null;
        }
        if (missionList.size() == 0) {
            rv_mission_list.setVisibility(View.GONE);
            tv_none_mission.setVisibility(View.VISIBLE);
        }
        closeSetting();
    }

    private void deleteCompleteMission() {
        UploadFileManager.getInstance().deleteCompleteMission();
        fileListAdapter.notifyDataSetChanged();
        if (missionList.size() == 0) {
            rv_mission_list.setVisibility(View.GONE);
            tv_none_mission.setVisibility(View.VISIBLE);
        }
    }

    private void pauseAllClick() {
        if (tv_pause_all.getText().equals("全部开启")) {
            startAllMission();
        } else {
            pauseAllMission();
        }
    }

    public void pauseAllMission() {
        UploadFileManager.getInstance().pauseAllMission();
        tv_pause_all.setText("全部开启");
    }

    public void startAllMission() {
        UploadFileManager.getInstance().startAllMission();
        tv_pause_all.setText("全部暂停");
    }

    private void setting() {
        if (ll_menu_root.getVisibility() == View.VISIBLE) {
            closeSetting();
        } else {
            openSetting();
        }
    }

    private void closeSetting() {
        iv_shadow.setVisibility(View.GONE);
        iv_shadow.setClickable(false);
        ll_menu_root.setVisibility(View.GONE);
        ll_delete.setVisibility(View.GONE);
    }

    private void openSetting() {
        iv_shadow.setVisibility(View.VISIBLE);
        iv_shadow.setClickable(true);
        ll_menu_root.setVisibility(View.VISIBLE);
    }

    private void back() {
        finish();
    }
}
