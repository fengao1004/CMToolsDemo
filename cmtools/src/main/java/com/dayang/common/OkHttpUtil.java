package com.dayang.common;

import android.util.Log;


import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by 冯傲 on 2016/8/13.
 * e-mail 897840134@qq.com
 */
public class OkHttpUtil {
    private static final String TAG =  Canstant.TAG;
    OkHttpCallBack ocb;
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");
    public static final MediaType JSON = MediaType.parse("text/plain; charset=utf-8");
    public void call(String url, String json, OkHttpCallBack ocb){
        this.ocb = ocb;
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call1 = client.newCall(request);
        call1.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkHttpUtil.this.ocb.error(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    OkHttpUtil.this.ocb.success(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }
    public void callGet(String url, OkHttpCallBack ocb){
        this.ocb = ocb;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call1 = client.newCall(request);
        call1.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkHttpUtil.this.ocb.error(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    OkHttpUtil.this.ocb.success(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void downLoadFile(String url, String filePath,OkHttpCallBack ocb){
        this.ocb = ocb;
        final File file = new File(filePath);
        RequestBody body = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_OBJECT_STREAM;
            }

            @Override
            public long contentLength() throws IOException {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    long current = 0;
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        current += readCount;
                        Log.e(TAG, "current------>" + current);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//
//            }
//        });
    }


    public interface  OkHttpCallBack{
        void success(Response response) throws Exception;
        void error(Call call, IOException e);
    }
}
