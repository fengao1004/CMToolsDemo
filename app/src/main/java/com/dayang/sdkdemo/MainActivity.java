package com.dayang.sdkdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dayang.common.CMToolsManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CMToolsManager.getInstance().init(this, CMToolsManager.MODE_WITHOUT_CONFERENCE).setMediaFileRootPath(Environment.getExternalStorageState());
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //http://clue.dayang.com/ClueCloudMobile?index=xs&FormType=DAY       token是678370dbd00e9a85c70996076956dfb
                CMToolsManager.getInstance().openWebApp(MainActivity.this, "http://yun.csztv.com/baoliao/index.html?FormType=DAY&teamToken=2af500630616c68e8d7b36a4eafc3be&appId=7d85849a6fdc7e585f67058c30ee0c09");
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CMToolsManager.getInstance().joinLiveConnection(getApplicationContext(), "DAY/Meeting");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CMToolsManager.getInstance().destroy();
    }
}
