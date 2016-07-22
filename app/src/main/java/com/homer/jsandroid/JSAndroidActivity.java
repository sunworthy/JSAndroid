package com.homer.jsandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class JSAndroidActivity extends Activity {
	private Activity mContext = null;
	private WebView mWebView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		showWebView();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void showWebView(){		// webView与js交互代码
		try {
			mWebView = new WebView(this);
			setContentView(mWebView);
			
			mWebView.requestFocus();
			
			mWebView.setWebChromeClient(new WebChromeClient(){
				@Override
				public void onProgressChanged(WebView view, int progress){
					JSAndroidActivity.this.setTitle("Loading...");
					JSAndroidActivity.this.setProgress(progress);
					
					if(progress >= 80) {
						JSAndroidActivity.this.setTitle("JsAndroid Test");
					}
				}
			});
			
			mWebView.setOnKeyListener(new View.OnKeyListener() {		// webview can go back
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
						mWebView.goBack();
						return true;
					}
					return false;
				}
			});
			
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDefaultTextEncodingName("utf-8");

			JSInterface jsInterface=new JSInterface();
			mWebView.addJavascriptInterface(jsInterface, "Js_Android");
			mWebView.loadUrl("http://sunworthy.net/js/index.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class JSInterface{
		@JavascriptInterface
		public void JsCallJavaMethod(){
			Toast.makeText(mContext, "JsCallJavaMethod", Toast.LENGTH_SHORT).show();
		}
		@JavascriptInterface
		public String showHtmlFromJava(final String param){
			return "Html call Java : " + param;
		}
		@JavascriptInterface
		public void JavaCallJS(){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mWebView.loadUrl("javascript: callJsMethodFromJava()");
					Toast.makeText(mContext, "JavaCallJS", Toast.LENGTH_SHORT).show();
				}
			});
		}
		@JavascriptInterface
		public void JavaCallJS2(){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mWebView.loadUrl("javascript: callJsMethodFromJavaWithParam('callJsMethodFromJavaWithParam')");
					Toast.makeText(mContext, "JavaCallJS2", Toast.LENGTH_SHORT).show();
				}
			});
		}
		@JavascriptInterface
		public void JavaCallJS3(){//匿名调用
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mWebView.loadUrl("javascript:(function(){document.getElementById(\"id_input3\").value = \"Java call JS\";})();");
					Toast.makeText(mContext, "JavaCallJS3", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
