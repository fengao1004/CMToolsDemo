//package com.dayang.view;
//
//import android.content.Context;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.Display;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//
//import com.dayang.adapter.UpdateFileListAdapter;
//import com.dayang.cmtools.R;
//import com.dayang.info.FilesUpdateManager;
//
///**
// * Created by 冯傲 on 2017/1/23.
// * e-mail 897840134@qq.com
// */
//
//public class UpdateProgressDialog {
//    String TAG = "fengao";
//    Context context;
//    android.app.Dialog dialog;
//    Display display;
//    private RecyclerView rv_updateFile_list;
//    private UpdateFileListAdapter updateFileListAdapter;
//    private FilesUpdateManager infos;
//
//    public UpdateProgressDialog(Context context) {
//        this.context = context;
//        WindowManager windowManager = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
//        display = windowManager.getDefaultDisplay();
//    }
//
//    public UpdateProgressDialog build() {
//        infos = FilesUpdateManager.getInstance();
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update_progress, null);
//        Log.i(TAG, "build: " + display.getWidth());
//        view.setMinimumWidth(display.getWidth());
//        if (dialog == null) {
//            dialog = new android.app.Dialog(context, R.style.ActionSheetDialogStyle);
//        }
//        rv_updateFile_list = (RecyclerView) view.findViewById(R.id.rv_updateFile_list);
//        rv_updateFile_list.setMinimumWidth(display.getWidth());
//        updateFileListAdapter = new UpdateFileListAdapter(context);
//        rv_updateFile_list.setAdapter(updateFileListAdapter);
//        rv_updateFile_list.setLayoutManager(new LinearLayoutManager(context));
//        rv_updateFile_list.setItemAnimator(new DefaultItemAnimator());
//        infos.setRemoveFileUpdateInfoListener(new FilesUpdateManager.RemoveFileUpdateInfoListener() {
//            @Override
//            public void removeFileUpdateInfo(int index) {
//                //本来是打算用updateFileListAdapter.notifyItemInserted()和updateFileListAdapter.notifyItemRemove() 来执行条目的增删
//                //但有时会报数组角标越界
//                //遂直接改用updateFileListAdapter.notifyDataSetChanged();
//                //好在item较少对效率影响可以忽略不计
//                updateFileListAdapter.notifyDataSetChanged();
//            }
//        });
//        infos.setAddFileUpdateInfoListener(new FilesUpdateManager.AddFileUpdateInfoListener() {
//            @Override
//            public void addFileUpdateInfo(int index) {
//                //本来是打算用updateFileListAdapter.notifyItemInserted()和updateFileListAdapter.notifyItemRemove() 来执行条目的增删
//                //但有时会报数组角标越界
//                //遂直接改用updateFileListAdapter.notifyDataSetChanged();
//                //好在item较少对效率影响可以忽略不计
//                updateFileListAdapter.notifyDataSetChanged();
//            }
//        });
//        infos.setFileUpdateFinishedListener(new FilesUpdateManager.FileUpdateFinishedListener() {
//            @Override
//            public void FileUpdateFinished() {
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        dialog.setContentView(view);
//        Window dialogWindow = dialog.getWindow();
//        dialogWindow.setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.x = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.y = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.width = display.getWidth();
//        dialogWindow.setAttributes(lp);
//        return this;
//    }
//
//    public void show() {
//        if (dialog != null) {
//            dialog.show();
//        }
//    }
//}
