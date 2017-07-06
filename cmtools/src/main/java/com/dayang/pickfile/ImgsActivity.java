package com.dayang.pickfile;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dayang.cmtools.R;
import com.dayang.common.Constants;
import com.dayang.common.ExitAppUtils;
import com.dayang.pickfile.ImgsAdapter.OnItemClickClass;

public class ImgsActivity extends Activity {

    Bundle bundle;
    FileTraversal fileTraversal;
    GridView imgGridView;
    ImgsAdapter imgsAdapter;
    LinearLayout select_layout;
    Util util;
    RelativeLayout relativeLayout2;
    HashMap<Integer, ImageView> hashImage;
    Button choise_button;
    ArrayList<String> filelist;
    private long exitTime = 0;
    int imgNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photogrally);
        ExitAppUtils.getInstance().addActivity(this);
        imgGridView = (GridView) findViewById(R.id.gridView1);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        Constants.IMAGEWIDTH = screenWidth / 3;
        Constants.IMAGEHEIGHT = Constants.IMAGEWIDTH;
        bundle = getIntent().getExtras();
        fileTraversal = bundle.getParcelable("data");
        imgNum = bundle.getInt("imgNum");
        imgsAdapter = new ImgsAdapter(this, fileTraversal.filecontent, onItemClickClass);
        imgGridView.setAdapter(imgsAdapter);
        select_layout = (LinearLayout) findViewById(R.id.selected_image_layout);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        choise_button = (Button) findViewById(R.id.button3);
        hashImage = new HashMap<Integer, ImageView>();
        filelist = new ArrayList<String>();
//		imgGridView.setOnItemClickListener(this);
        util = new Util(this);
    }

    class BottomImgIcon implements OnItemClickListener {

        int index;

        public BottomImgIcon(int index) {
            this.index = index;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

        }
    }

    @SuppressLint("NewApi")
    public ImageView iconImage(String filepath, int index, CheckBox checkBox) throws FileNotFoundException {
        LinearLayout.LayoutParams params = new LayoutParams(relativeLayout2.getMeasuredHeight() - 10, relativeLayout2.getMeasuredHeight() - 10);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(params);
        imageView.setBackgroundResource(R.drawable.imgbg);
        float alpha = 100;
        imageView.setAlpha(alpha);
        util.imgExcute(imageView, imgCallBack, filepath);
        imageView.setOnClickListener(new ImgOnclick(filepath, checkBox));
        return imageView;
    }

    ImgCallBack imgCallBack = new ImgCallBack() {
        @Override
        public void resultImgCall(ImageView imageView, Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    class ImgOnclick implements OnClickListener {
        String filepath;
        CheckBox checkBox;

        public ImgOnclick(String filepath, CheckBox checkBox) {
            this.filepath = filepath;
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View arg0) {
            checkBox.setChecked(false);
            select_layout.removeView(arg0);
            choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
            filelist.remove(filepath);
        }
    }

    ImgsAdapter.OnItemClickClass onItemClickClass = new OnItemClickClass() {
        @Override
        public void OnItemClick(View v, int Position, CheckBox checkBox) {
            String filapath = fileTraversal.filecontent.get(Position);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
                select_layout.removeView(hashImage.get(Position));
                filelist.remove(filapath);
                choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
            } else {
                try {
                    if (select_layout.getChildCount() >= imgNum) {
                        Toast.makeText(ImgsActivity.this, "最多上传" + imgNum + "个文件", Toast.LENGTH_LONG).show();
                        return;
                    }
                    checkBox.setChecked(true);
                    Log.i("img", "img choise position->" + Position);
                    ImageView imageView = iconImage(filapath, Position, checkBox);
                    if (imageView != null) {
                        hashImage.put(Position, imageView);
                        filelist.add(filapath);
                        select_layout.addView(imageView);
                        choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void tobreak(View view) {
        finish();
    }

    public void sendfiles(View view) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("files", filelist);
        intent.putExtras(bundle);
        setResult(Constants.FILE_SELECTDETAIL_RESULT, intent);
        this.finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
