package com.dayang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.HBuilder.integrate.SDK_WebApp;
import com.dayang.common.Canstant;
import com.dayang.view.DialogNotify;

import io.dcloud.common.DHInterface.IWebview;

public class MessageActivity extends Activity {


    private static final String TAG =  Canstant.TAG;
    private FrameLayout fl;
    private IWebview webview;
    public static SDK_WebApp sdk_activity;

    static  public void  setSdk_activity(SDK_WebApp sdk_activit) {
        sdk_activity = sdk_activit;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "odasdadnCreate: ");
        Intent intent = getIntent();
        String content;
        try {
            content = intent.getStringExtra("content");
        } catch (Exception e) {
            content = "";
        }
        Log.i(TAG, "onCreate: "+sdk_activity);
        finish();
        if(sdk_activity==null){
            Intent adkIntent = new Intent(this,SDK_WebApp.class);
            adkIntent.putExtra("content",content);
            startActivity(adkIntent);
        }else {
            sdk_activity.enterUrl(content);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}
