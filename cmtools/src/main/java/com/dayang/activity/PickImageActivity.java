package com.dayang.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dayang.adapter.ImageAdapter;
import com.dayang.cmtools.R;
import com.dayang.common.Constants;
import com.dayang.info.FileDir;
import com.dayang.util.MediaFileUtils;
import com.dayang.view.ActionSheetDialog;

import java.util.ArrayList;

public class PickImageActivity extends AppCompatActivity {

    private ActionSheetDialog dialog;
    private ArrayList<FileDir> fileDir;
    private ProgressBar choose_progress;

    public ArrayList<String> getUpdataFiles() {
        return updataFiles;
    }

    public ArrayList<String> updataFiles = new ArrayList<>();
    private TextView tv_complete;
    private RecyclerView rc;
    private TextView preview;
    private TextView tv_dirname;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog = new ActionSheetDialog(fileDir, PickImageActivity.this).builder();
            dialog.setOnClickListener(new ActionSheetDialog.OnClickListener() {
                @Override
                public void onClick(int position) {
                    rc.setAdapter(new ImageAdapter(fileDir.get(position), PickImageActivity.this));
                    tv_dirname.setText(fileDir.get(position).name);
                }
            });
            tv_dirname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });
            ImageAdapter imageAdapter = new ImageAdapter(fileDir.get(0), PickImageActivity.this);
            rc.setAdapter(imageAdapter);
            choose_progress.setVisibility(View.GONE);
            rc.setVisibility(View.VISIBLE);

            rc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pickmediafile);
        new Thread() {
            @Override
            public void run() {
                super.run();
                fileDir = new MediaFileUtils(getApplicationContext()).getFileDir();
                handler.sendEmptyMessage(0);
            }
        }.start();
        tv_complete = (TextView) findViewById(R.id.tv_complete);
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updataFiles.size() == 0) {
                    return;
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("files", updataFiles);
                    intent.putExtras(bundle);
                    setResult(Constants.FILE_SELECT_RESULT, intent);
                    finish();
                }
            }
        });
        rc = (RecyclerView) findViewById(R.id.rc);
        rc.setItemViewCacheSize(100);
        max = getIntent().getIntExtra("imgNum", 1);
        preview = (TextView) findViewById(R.id.yl);
        choose_progress = (ProgressBar) findViewById(R.id.choose_progress);
        tv_dirname = (TextView) findViewById(R.id.tv_dirname);
        choose_progress.setVisibility(View.VISIBLE);
        rc.setVisibility(View.GONE);
        rc.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }

    public void back(View v) {
        finish();
    }

    public void choose(View v) {

    }

    public void preview(View v) {
        if (updataFiles.size() == 0) {
            return;
        }
        Intent intent = new Intent(this, PagerThumbnailActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<String> files = new ArrayList<>();
        for (int i = 0; i < updataFiles.size(); i++) {
            String path = updataFiles.get(i);
            if (!path.contains("http://")) {
                path = "file://" + path;
            }
            files.add(path);
        }
        bundle.putStringArrayList("fileNamePath", files);
        bundle.putInt("index", 0);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    int max = 9;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean addFile(String filePath) {
        boolean sucess = false;
        if (updataFiles.contains(filePath)) {
            updataFiles.remove(filePath);
        } else if (updataFiles.size() < max) {
            updataFiles.add(filePath);
            sucess = true;
        } else if (updataFiles.size() == max) {
            Toast.makeText(getApplicationContext(), "已经超过最多文件了，亲", 0).show();
        }
        if (updataFiles.size() == 0) {
            preview.setText("预览");
            tv_complete.setBackground(getResources().getDrawable(R.drawable.shap_complete_n));
            preview.setTextColor(Color.parseColor("#77FFFFFF"));
            tv_complete.setText("完成");
            tv_complete.setTextColor(Color.parseColor("#77FFFFFF"));
        } else {
            preview.setText(updataFiles.size() + "/" + max + "预览");
            preview.setTextColor(Color.parseColor("#DDDDDD"));
            tv_complete.setText(updataFiles.size() + "/" + max + "完成");
            tv_complete.setBackground(getResources().getDrawable(R.drawable.shap_complete_y));
            tv_complete.setTextColor(Color.parseColor("#DDDDDD"));
        }
        return sucess;
    }
}
