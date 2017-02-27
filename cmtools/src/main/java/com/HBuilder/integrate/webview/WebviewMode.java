package com.HBuilder.integrate.webview;

import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.ICore.ICoreStatusListener;
import io.dcloud.common.DHInterface.IFeature;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.feature.internal.sdk.SDK;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

class WebviewMode implements ICoreStatusListener{
	private String url = null;
	
	LinearLayout btns = null;
	Activity activity = null;
	ViewGroup mRootView = null;
	public WebviewMode(Activity activity,ViewGroup rootView,String url){
		this.activity = activity;
		mRootView = rootView;
		this.url = url;
		btns = new LinearLayout(activity);
		mRootView.setBackgroundColor(0xffffffff);
		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout(){
				webview.onRootViewGlobalLayout(mRootView);
	        }
			
		});
	}
	
	@Override
	public void onCoreInitEnd(ICore coreHandler) {
		SDK.initSDK(coreHandler);
		//注册扩展的Feature  
		//1，featureName	为特征名称
		//2, className	为处理扩展Feature的接收类全名称
		//3, content  为扩展Feature而创建的js代码，代码中必须使用 plus.bridge.execSync(featureName,actionName,[arguments])或plus.bridge.exec(featureName,actionName,[arguments])与native层进行数据交互
	//	SDK.registerJsApi(featureName, className, content);
		//创建默认webapp，赋值appid
		//String url = "http://100.0.3.31:8080/baoliao";
		//String url = "http://100.0.1.53:8080/TestWeb/plugin.jsp";
		//String url = "http://100.0.1.53:8080/TestWeb/pluginrecord.jsp";
		//String url = "http://100.0.1.53:8080/DYMMedits4/login.jsp";
		//String urlstr12 = "http://100.0.1.53:8080/TestWeb/webviewtest.jsp";
		showWebview("", this.url, new AbsoluteLayout.LayoutParams(-1,800,0,200));
	}
	IWebview webview = null;
	
	ProgressDialog pd = null;
	private void showWebview(String appid, String url,LayoutParams lp){
		String launchPage = url;
		
		webview = SDK.createWebview(activity,launchPage, appid,new IWebviewStateListener() {
			@Override
			public Object onCallBack(int pType, Object pArgs) {
				switch (pType) {
				case IWebviewStateListener.ON_WEBVIEW_READY:
					//准备完毕之后添加webview到显示父View中，设置排版不显示状态，避免显示webview时，html内容排版错乱问题
					((IWebview)pArgs).obtainFrameView().obtainMainView().setVisibility(View.INVISIBLE);
					SDK.attach(mRootView, ((IWebview)pArgs));
					break;
				/*case IWebviewStateListener.ON_PAGE_STARTED:
					pd = ProgressDialog.show(activity, "加载中", "0/100");
					break;
				case IWebviewStateListener.ON_PROGRESS_CHANGED:
					if(pd != null){
						pd.setMessage(pArgs + "/100");
					}
					break;*/
				case IWebviewStateListener.ON_PAGE_FINISHED:
					if(pd != null){
						pd.dismiss();
						pd = null;
					}
					//页面加载完毕，设置显示webview
					webview.obtainFrameView().obtainMainView().setVisibility(View.VISIBLE);
					break;
				}
				return null;
			}
		});
	}
	
	@Override
	public void onCoreReady(ICore coreHandler) {
		try {
			SDK.initSDK(coreHandler);
			SDK.requestFeature(IFeature.F_UI, null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String featureName = "T";
	String className = "com.HBuilder.integrate.webview.WebViewMode_FeatureImpl";
	String content = "(function(plus){function test(){return plus.bridge.execSync('T','test',[arguments]);}plus.T = {test:test};})(window.plus);";
	@Override
	public boolean onCoreStop() {
		// TODO Auto-generated method stub
		return false;
	}
}