package com.HBuilder.integrate.webview;

import com.dayang.activity.MediaPlugin;

import io.dcloud.EntryProxy;
import io.dcloud.common.DHInterface.ISysEventListener.SysEventType;
import io.dcloud.feature.internal.sdk.SDK;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;


/**
 * 本demo为以webview控件方式集成pandora sdk，包含如何扩展Feature,如{@link WebViewMode_FeatureImpl}
 * @author yanglei
 *
 */
public class SDK_WebView extends Activity{
	   public WebviewMode wm;
	   private WebView webView;
		boolean doHardAcc = true;
		EntryProxy mEntryProxy = null;
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Bundle bundle = this.getIntent().getExtras();
			String url = bundle.getString("url");
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	        if(mEntryProxy == null){
	        	FrameLayout rootView = new FrameLayout(this);
	        	 wm = new WebviewMode(this,rootView,url);
		    	 mEntryProxy = EntryProxy.init(this,wm);
			     mEntryProxy.onCreate(savedInstanceState,SDK.IntegratedMode.WEBVIEW,null);
			     setContentView(rootView);
	        }
	    }
	    
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        return mEntryProxy.onActivityExecute(this, SysEventType.onCreateOptionMenu, menu);
	    }
		
		@Override
		public void onPause() {
			super.onPause();
			mEntryProxy.onPause(this);
		}
		@Override
		public void onResume() {
			super.onResume();
			mEntryProxy.onResume(this);
		}
		
		public void onNewIntent(Intent intent) {
			super.onNewIntent(intent);
			if(intent.getFlags() != 0x10600000){//非点击icon调用activity时才调用newintent事件
				mEntryProxy.onNewIntent(this,intent);
			}
		}
		
		@Override
		protected void onDestroy() {
			super.onDestroy();
			mEntryProxy.onStop(this);
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			boolean _ret = mEntryProxy.onActivityExecute(this,SysEventType.onKeyDown, new Object[]{keyCode,event});
			return _ret ? _ret : super.onKeyDown(keyCode, event);
		}
		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {
			boolean _ret = mEntryProxy.onActivityExecute(this,SysEventType.onKeyUp, new Object[]{keyCode,event});
			return _ret ? _ret : super.onKeyUp(keyCode, event);
		}
		@Override
		public boolean onKeyLongPress(int keyCode, KeyEvent event) {
			boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyLongPress, new Object[]{keyCode,event});
			return _ret ? _ret : super.onKeyLongPress(keyCode, event);
		}
		
		public void onConfigurationChanged(Configuration newConfig) {
			try {
				int temp = this.getResources().getConfiguration().orientation;
				if (mEntryProxy != null) {
					mEntryProxy.onConfigurationChanged(this, temp);
				}
				super.onConfigurationChanged(newConfig);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			MediaPlugin.sysEventExtWebView.onActivityResultCall(requestCode, resultCode,data);
			//mEntryProxy.onActivityExecute(this,SysEventType.onActivityResult, new Object[]{requestCode,resultCode,data});
		}
}


