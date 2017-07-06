package com.dayang.uploadlib;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.dayang.uploadlib.service.UpLoadService;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Context appContext;
    Handler handler;

    @Test
    public void useAppContext() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
        if (handler == null) {
            handler = new Handler(appContext.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    try {
                        useAppContext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        appContext.startService(new Intent(appContext, UpLoadService.class));
        handler.sendEmptyMessageDelayed(0, 3000);
    }
}
