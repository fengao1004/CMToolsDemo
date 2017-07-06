package com.dayang.upload;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.UnsupportedEncodingException;

/**
 * Created by 冯傲 on 2017/5/15.
 * e-mail 897840134@qq.com
 */

public class RequestErrorTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String requestParam = params[1];
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        StringEntity reqEntity = null;
        try {
            reqEntity = new StringEntity(requestParam, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        reqEntity.setContentEncoding("UTF-8");
        reqEntity.setContentType("application/String");
        httppost.setEntity(reqEntity);
        for (int i = 0; i < 10; i++) {
            Log.i("fengao", "doInBackground: 执行重试第" + i + "次 线程" + Thread.currentThread().getName());
            Log.i("fengao", "doInBackground:参数 " + requestParam);
            try {
                HttpResponse response = httpclient.execute(httppost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    Log.i("fengao", "doInBackground: 成功通知 执行重试第" + i + "次 线程" + Thread.currentThread().getName());
                    break;
                }
            } catch (Exception e) {

            }
            SystemClock.sleep(1000 * 60);
        }
        return null;
    }
}
