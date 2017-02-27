package com.dayang.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dayang.adapter.DirListAdapter;
import com.dayang.cmtools.R;
import com.dayang.info.FileDir;

import java.util.ArrayList;

public class ActionSheetDialog {
    private Context context;
    private Dialog dialog;
    ArrayList<FileDir> list;
    private DirListAdapter dirListAdapter;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        dirListAdapter.setL(onClickListener);
    }

    OnClickListener onClickListener;

    public ActionSheetDialog(ArrayList<FileDir> list, Context context) {
        this.list = list;
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();

    }

    private TextView txt_title;
    private TextView txt_cancel;
    private LinearLayout lLayout_content;
    private ScrollView sLayout_content;
    private boolean showTitle = false;
    private Display display;
    private ListView lv_dir_list;

    public ActionSheetDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public ActionSheetDialog() {

    }

    public ActionSheetDialog builder() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.dirlist, null);
        view.setMinimumWidth(display.getWidth());
        lv_dir_list = (ListView) view.findViewById(R.id.lv_dir_list);
        dirListAdapter = new DirListAdapter(onClickListener, list, context, this);
        lv_dir_list.setAdapter(dirListAdapter);
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        return this;
    }


    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public interface OnClickListener {
        void onClick(int position);
    }


}
