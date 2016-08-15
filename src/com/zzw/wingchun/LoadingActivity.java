package com.zzw.wingchun;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadingActivity extends Activity {
	private AlertDialog.Builder builder;
	private TextView loadingText;
	private ProgressBar loadingBar;
	
	private Handler handler;
	
	public static final String TAG="LoadingActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading_layout);
		SharedPreferences pref=PreferenceManager.
				getDefaultSharedPreferences(this);
		boolean isAgree=pref.getBoolean("is_agree", false);
		boolean isAvailable=pref.getBoolean("is_available", false);
		if(!isAgree){
			View layout=getLayoutInflater().inflate(R.layout.loading_custom_dialog, null);
			builder=createDialog(layout);
			builder.show();
		}
		else{
			if(!isAvailable){
				startLoading();
			}
			else{
				Intent intent=new Intent(this,LocalHtmlActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
	
	private AlertDialog.Builder createDialog(final View layout){
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setPositiveButton("Í¬Òâ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences pref=PreferenceManager.
						getDefaultSharedPreferences(LoadingActivity.this);
				SharedPreferences.Editor editor=pref.edit();
				editor.putBoolean("is_agree", true);
				editor.commit();
				startLoading();
				//Log.d(TAG, "out thread: "+storagePath);
				//Intent intent=new Intent(LoadingActivity.this,LocalHtmlActivity.class);
				//intent.putExtra("storage_path", storagePath);
				//startActivity(intent);
				//LoadingActivity.this.finish();
			}
		});
		builder.setNegativeButton("ÍË³ö", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.setCancelable(false);
		return builder;
	}
	
	private void startLoading(){
		Log.d("LoadingActivity", "startLoading");
		RelativeLayout layout=(RelativeLayout)findViewById(R.id.loading_bar_layout);
		layout.setVisibility(View.VISIBLE);
		loadingText=(TextView)findViewById(R.id.loading_text);
		loadingBar=(ProgressBar)findViewById(R.id.loading_bar);
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				switch(msg.what){
				case LoadingActivity.UpdateProgress.ERROR_HAPPENDED:
					LoadingActivity.UpdateProgress err=
					(LoadingActivity.UpdateProgress)msg.obj;
					loadingText.setText(err.path);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					finish();
					break;
				case LoadingActivity.UpdateProgress.UPDATE_PROGRESS:
					LoadingActivity.UpdateProgress upd=
					(LoadingActivity.UpdateProgress)msg.obj;
					loadingText.setText(upd.path);
					loadingBar.setProgress(upd.progress);
					break;
				case LoadingActivity.UpdateProgress.FINISH_LOADING:
					SharedPreferences pref=PreferenceManager.
					getDefaultSharedPreferences(LoadingActivity.this);
					SharedPreferences.Editor editor=pref.edit();
					editor.putBoolean("is_available", true);
					editor.commit();
					LoadingActivity.UpdateProgress fin=
					(LoadingActivity.UpdateProgress)msg.obj;
					loadingText.setText(fin.path);
					loadingBar.setProgress(fin.progress);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Intent intent=new Intent(LoadingActivity.this,LocalHtmlActivity.class);
					startActivity(intent);
					finish();
					break;
				default:
					break;
				}
			}
		};
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				Log.d(TAG, "storagePath: "+CopyRawResource.SDPATH);
				if(!CopyRawResource.isExternalStorageWritable()){
					Log.d(TAG, "external storage is not writable!");
					Message msg=new Message();
					msg.what=LoadingActivity.UpdateProgress.ERROR_HAPPENDED;
					msg.obj=new LoadingActivity.UpdateProgress("external storage is not writable!", 0);
					handler.sendMessage(msg);
					return;
				}
				else{
					Log.d(TAG, "external storage is writable!");
				}
				
				RawResources ress=new RawResources();
				
				String wxPath=CopyRawResource.createExternalDir("wuxie");
				if(wxPath!=null){
					Log.d(TAG, wxPath+" created!");
					ress.addResource(wxPath, "main_page.html", R.raw.main_page, RawResources.FILETYPE);
				}
				else{
					Message msg=new Message();
					msg.what=LoadingActivity.UpdateProgress.ERROR_HAPPENDED;
					msg.obj=new LoadingActivity.UpdateProgress("mkdir: "+wxPath+" failed!", 0);
					handler.sendMessage(msg);
					return;
				}
				
				String wxImgPath=CopyRawResource.createExternalDir("wuxie"+File.separator+"image");
				if(wxImgPath!=null){
					Log.d(TAG, wxImgPath+" created!");
					ress.addResource(wxImgPath, "group.jpg", R.raw.group, RawResources.IMAGETYPE);
					ress.addResource(wxImgPath, "shuangjiegun.jpg", R.raw.shuangjiegun, RawResources.IMAGETYPE);
					ress.addResource(wxImgPath, "changquan.jpg", R.raw.changquan, RawResources.IMAGETYPE);
					ress.addResource(wxImgPath, "taiji.jpg", R.raw.taiji, RawResources.IMAGETYPE);
					ress.addResource(wxImgPath, "baguazhang.jpg", R.raw.baguazhang, RawResources.IMAGETYPE);
					ress.addResource(wxImgPath, "jiequandao.jpg", R.raw.jiequandao, RawResources.IMAGETYPE);
					ress.addResource(wxImgPath, "wingchun.jpg", R.raw.wingchun, RawResources.IMAGETYPE);
					ress.addResource(wxImgPath, "liuzhengfeng.jpg", R.raw.liuzhengfeng, RawResources.IMAGETYPE);
					ress.addResource(wxImgPath, "xuchuchu.jpg", R.raw.xuchuchu, RawResources.IMAGETYPE);
				}
				else{
					Message msg=new Message();
					msg.what=LoadingActivity.UpdateProgress.ERROR_HAPPENDED;
					msg.obj=new LoadingActivity.UpdateProgress("mkdir: "+wxImgPath+" failed!", 0);
					handler.sendMessage(msg);
					return;
				}
				
				String wcPath=CopyRawResource.createExternalDir("wuxie"+File.separator+"wingchun");
				if(wcPath!=null){
					Log.d(TAG, wcPath+" created!");
					ress.addResource(wcPath,"index.html", R.raw.index,RawResources.FILETYPE);
				}
				else{
					Message msg=new Message();
					msg.what=LoadingActivity.UpdateProgress.ERROR_HAPPENDED;
					msg.obj=new LoadingActivity.UpdateProgress("mkdir: "+wcPath+" failed!", 0);
					handler.sendMessage(msg);
					return;
				}
				
				String wcImgPath=CopyRawResource.createExternalDir("wuxie"+File.separator+"wingchun"+File.separator+"image");
				if(wcImgPath!=null){
					Log.d(TAG, wcImgPath+" created!");
					ress.addResource(wcImgPath,"introduct01.jpg", R.raw.introduct01,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"inherit01.jpg", R.raw.inherit01,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"inherit02.jpg", R.raw.inherit02,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"inherit0301.jpg", R.raw.inherit0301,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"inherit0302.jpg", R.raw.inherit0302,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"theory0201.gif", R.raw.theory0201,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"theory0202.gif", R.raw.theory0202,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"theory0203.gif", R.raw.theory0203,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"theory0204.jpg", R.raw.theory0204,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"theory0301.jpg", R.raw.theory0301,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"theory0302.jpg", R.raw.theory0302,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"theory0401.jpg", R.raw.theory0401,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"theory0402.jpg", R.raw.theory0402,RawResources.IMAGETYPE);
					ress.addResource(wcImgPath,"theory0403.jpg", R.raw.theory0403,RawResources.IMAGETYPE);
				}
				else{
					Message msg=new Message();
					msg.what=LoadingActivity.UpdateProgress.ERROR_HAPPENDED;
					msg.obj=new LoadingActivity.UpdateProgress("mkdir: "+wcImgPath+" failed!", 0);
					handler.sendMessage(msg);
					return;
				}

				String wcItdPath=CopyRawResource.createExternalDir("wuxie"+File.separator+"wingchun"+File.separator+"introduct");
				if(wcItdPath!=null){
					Log.d(TAG, wcItdPath+" created!");
					ress.addResource(wcItdPath,"introduct.html", R.raw.introduct,RawResources.FILETYPE);
				}
				else{
					Message msg=new Message();
					msg.what=LoadingActivity.UpdateProgress.ERROR_HAPPENDED;
					msg.obj=new LoadingActivity.UpdateProgress("mkdir: "+wcItdPath+" failed!", 0);
					handler.sendMessage(msg);
					return;
				}
				
				String wcIhrPath=CopyRawResource.createExternalDir("wuxie"+File.separator+"wingchun"+File.separator+"inherit");
				if(wcIhrPath!=null){
					Log.d(TAG, wcIhrPath+" created!");
					ress.addResource(wcIhrPath,"origin.html", R.raw.origin,RawResources.FILETYPE);
					ress.addResource(wcIhrPath,"develope.html", R.raw.develope,RawResources.FILETYPE);
					ress.addResource(wcIhrPath,"character.html", R.raw.character,RawResources.FILETYPE);
				}
				else{
					Message msg=new Message();
					msg.what=LoadingActivity.UpdateProgress.ERROR_HAPPENDED;
					msg.obj=new LoadingActivity.UpdateProgress("mkdir: "+wcIhrPath+" failed!", 0);
					handler.sendMessage(msg);
					return;
				}
				
				String wcTerPath=CopyRawResource.createExternalDir("wuxie"+File.separator+"wingchun"+File.separator+"theory");
				if(wcTerPath!=null){
					Log.d(TAG, wcTerPath+" created!");
					ress.addResource(wcTerPath,"quanpu.html", R.raw.quanpu,RawResources.FILETYPE);
					ress.addResource(wcTerPath,"taolu.html", R.raw.taolu,RawResources.FILETYPE);
					ress.addResource(wcTerPath,"wuqi.html", R.raw.wuqi,RawResources.FILETYPE);
					ress.addResource(wcTerPath,"linian.html", R.raw.linian,RawResources.FILETYPE);
				}
				else{
					Message msg=new Message();
					msg.what=LoadingActivity.UpdateProgress.ERROR_HAPPENDED;
					msg.obj=new LoadingActivity.UpdateProgress("mkdir: "+wcTerPath+" failed!", 0);
					handler.sendMessage(msg);
					return;
				}
				
				ArrayList<RawResources.Resource> resList=ress.getResourceList();
				int size=resList.size();
				int count=0;
				for(int i=0;i<size;i++){
					RawResources.Resource res=resList.get(i);
					String path=res.getPath()+File.separator+res.getFileName();
					int progress=(int)(((double)(i+1)/size)*100);
					LoadingActivity.UpdateProgress obj;
					if(CopyRawResource.copyResource(LoadingActivity.this,res)){
						Log.d(TAG, path+" copied!");
						++count;
						obj=new LoadingActivity.UpdateProgress("succeed:"+path, progress);
					}
					else{
						Log.d(TAG, path+" not copy!");
						obj=new LoadingActivity.UpdateProgress("failed:"+path, progress);
					}
					Message msg=new Message();
					msg.what=LoadingActivity.UpdateProgress.UPDATE_PROGRESS;
					msg.obj=obj;
					handler.sendMessage(msg);
				}
				LoadingActivity.UpdateProgress obj=new LoadingActivity.UpdateProgress("finish loading, "+count+" succeeded, "+(size-count)+" failed.", 100);
				Message msg=new Message();
				msg.what=LoadingActivity.UpdateProgress.FINISH_LOADING;
				msg.obj=obj;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	private static class UpdateProgress{
		static final int ERROR_HAPPENDED=0;
		static final int UPDATE_PROGRESS=1;
		static final int FINISH_LOADING=2;
		String path;
		int progress;
		UpdateProgress(String pt,int pg){
			path=pt;
			progress=pg;
		}
	}

}
