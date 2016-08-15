package com.zzw.wingchun;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public final class CopyRawResource {
	public static final String SDPATH=Environment.getExternalStorageDirectory().getAbsolutePath();
	private static String TAG="CopyRawResource";
	
	private CopyRawResource() {}
	
	public static boolean isExternalStorageWritable(){
		String state=Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}
	
	public static String createExternalDir(String dir){
		String path=SDPATH+File.separator+dir;
		try{
			File f=new File(path);
			if(f.exists())
				return path;
			else{
				if(f.mkdirs())
					return path;
				else{
					Log.e(TAG, "createDir failed: "+path);
					return null;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static boolean copyFile(Context context,String dir,String fileName,int resource){
		String path=dir+File.separator+fileName;
		BufferedReader in=null;
		BufferedWriter out=null;
		try{
			File f=new File(path);
			if(f.exists())
				return true;
			else{
				in=new BufferedReader(
						new InputStreamReader(
								context.getResources().openRawResource(resource)));
				out=new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(f)));
				String line;
				while((line=in.readLine())!=null)
					out.write(line);
				out.flush();
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try{
				if(in!=null)
					in.close();
				if(out!=null)
					out.close();
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
	}
	public static boolean copyImage(Context context,String dir,String imageName,int resource){
		String path=dir+File.separator+imageName;
		BufferedInputStream in=null;
		BufferedOutputStream out=null;
		try{
			File f=new File(path);
			if(f.exists())
				return true;
			else{
				in=new BufferedInputStream(
						context.getResources().openRawResource(resource));
				out=new BufferedOutputStream(
						new FileOutputStream(f));
				int b;
				while((b=in.read())!=-1)
					out.write(b);
				out.flush();
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try{
				if(in!=null)
					in.close();
				if(out!=null)
					out.close();
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
	}
	public static boolean copyResource(Context context,RawResources.Resource r){
		String path=r.getPath();
		String file=r.getFileName();
		int id=r.getResourceId();
		int type=r.getType();
		if(type==RawResources.FILETYPE)
			return copyFile(context,path, file, id);
		else if(type==RawResources.IMAGETYPE)
			return copyImage(context,path, file, id);
		return false;
	}
}
