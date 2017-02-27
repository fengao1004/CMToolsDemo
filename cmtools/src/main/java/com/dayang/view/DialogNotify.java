package com.dayang.view;

import android.app.Dialog;
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
public class DialogNotify extends Dialog {
    public DialogNotify(Context context) {
        super(context);
    }

    public DialogNotify(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        public  onUpClickListener l=null ;
        private TextView dialog_content;


        public Builder(Context context) {
            this.context = context;
        }


        public void setText(String text){
            dialog_content.setText(text);
        }

        public DialogNotify create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DialogNotify dialog = new DialogNotify(context,R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialognotify, null);
            dialog_content = (TextView) layout.findViewById(R.id.dialog_content);
            dialog_content.setMovementMethod(ScrollingMovementMethod.getInstance());
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            layout.findViewById(R.id.et_up).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onClick();
                    dialog.dismiss();
                }
            });
            dialog.setContentView(layout);
            TextView tv =  (TextView) layout.findViewById(R.id.tv_dialog_title);
            TextPaint tp = tv.getPaint();
            tp.setFakeBoldText(true);
            return dialog;
        }
        public void setOnClick(onUpClickListener l){
            this.l = l;
        }


    }

    public interface onUpClickListener{
        void onClick();
    }
}
