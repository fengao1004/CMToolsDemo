package com.dayang.view;

import android.content.Context;
import android.text.TextPaint;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dayang.cmtools.R;

/**
 * Created by 冯傲 on 2016/8/14.
 * e-mail 897840134@qq.com
 */
public class DialogChooseFile extends android.app.Dialog {
    public DialogChooseFile(Context context) {
        super(context);
    }

    public DialogChooseFile(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        public onUpClickListener l = null;



        public Builder(Context context) {
            this.context = context;
        }

        String title;
        String contant;




        public DialogChooseFile create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DialogChooseFile dialog = new DialogChooseFile(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialogchoosefile, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layout.findViewById(R.id.ll_choose_mu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onAudioClick();
                }
            });
            layout.findViewById(R.id.ll_choose_pi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onMusicClick();
                }
            });
            layout.findViewById(R.id.ll_choose_vi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onVedioClick();
                }
            });
            dialog.setContentView(layout);
            return dialog;
        }

        public void setOnClick(onUpClickListener l) {
            this.l = l;
        }


    }

    public interface onUpClickListener {
        void onVedioClick();

        void onMusicClick();

        void onAudioClick();

    }
}
