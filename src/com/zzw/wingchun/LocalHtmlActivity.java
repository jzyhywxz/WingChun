package com.zzw.wingchun;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class LocalHtmlActivity extends Activity {
	private WebView webView;
	private ImageButton prevBt;
	private ImageButton nextBt;
	private ImageButton homeBt;
	private ImageButton moreBt;
	
	private static final String TAG="LocalHtmlActivity";
	private String homePage;
	private UrlTrace trace=new UrlTrace();
	
	@Override protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.local_html_layout);
		homePage="file://"+CopyRawResource.SDPATH+"/wuxie/main_page.html";
		Log.d(TAG, "homePage: "+homePage);
		webView=(WebView)findViewById(R.id.web_view);
		prevBt=(ImageButton)findViewById(R.id.prev_button);
		nextBt=(ImageButton)findViewById(R.id.next_button);
		homeBt=(ImageButton)findViewById(R.id.home_button);
		moreBt=(ImageButton)findViewById(R.id.more_button);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view,String url){
				trace.addUrl(url);
				Log.d(TAG, trace.toString());
				view.loadUrl(url);
				return true;
			}
		});
		prevBt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String prev=trace.getPrevious();
				Log.d(TAG, trace.toString());
				if(prev!=null)
					webView.loadUrl(prev);
			}
		});
		nextBt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String next=trace.getNext();
				Log.d(TAG, trace.toString());
				if(next!=null)
					webView.loadUrl(next);
			}
		});
		homeBt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String url=trace.getCurrent();
				if((url==null)||(!url.equals(homePage))){
					trace.addUrl(homePage);
					Log.d(TAG, trace.toString());
					webView.loadUrl(homePage);
				}
			}
		});
		moreBt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(LocalHtmlActivity.this,MoreActivity.class);
				startActivity(intent);
			}
		});
		trace.addUrl(homePage);
		Log.d(TAG, "first: "+trace.toString());
		webView.loadUrl(homePage);
	}
	
	private class UrlTrace{
		private LinkedList<String> urlTrace;
		private int point;
		
		public UrlTrace(){
			urlTrace=new LinkedList<String>();
			point=-1;
		}
		public void addUrl(String url){
			urlTrace.addLast(url);
			point=urlTrace.size()-1;
		}
		public String getPrevious(){
			if(point>0){
				--point;
				String url=urlTrace.get(point);
				urlTrace.addLast(url);
				return url;
			}
			return null;
		}
		public String getCurrent(){
			if(urlTrace.size()>0)
				return urlTrace.getLast();
			return null;
		}
		public String getNext(){
			if(point<urlTrace.size()-1){
				++point;
				String url=urlTrace.get(point);
				urlTrace.addLast(url);
				return url;
			}
			return null;
		}
		public String toString(){
			return urlTrace.toString()+" point:"+point;
		}
	}
	
	@Override
	public void onBackPressed(){
		Log.d(TAG, "onBackPressed");
		if(trace.getCurrent().equals(homePage))
			super.onBackPressed();
		else{
			String prev=trace.getPrevious();
			Log.d(TAG, trace.toString());
			if(prev!=null)
				webView.loadUrl(prev);
		}
	}
}
