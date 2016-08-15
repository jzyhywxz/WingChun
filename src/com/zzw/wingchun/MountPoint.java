package com.zzw.wingchun;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

public class MountPoint {
	private MountPoint(){}
	
	public static String[] getAllPoints(Context context){
		StorageManager manager=(StorageManager)
				context.getSystemService(Activity.STORAGE_SERVICE);
		String[] points=null;
		try{
			Method method=manager.getClass().getMethod("getVolumePaths");
			points=(String[])method.invoke(manager);
		}catch(Exception e){
			e.printStackTrace();
		}
		return points;
	}
	public static boolean isMounted(Context context,String point){
		if(point==null)
			return false;
		StorageManager manager=(StorageManager)
				context.getSystemService(Activity.STORAGE_SERVICE);
		try{
			Method method=manager.getClass().getMethod("getVolumeState", String.class);
			String state=(String)method.invoke(manager, point);
			return Environment.MEDIA_MOUNTED.equals(state);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	public static ArrayList<String> getMountedPoints(Context context){
		StorageManager manager=(StorageManager)
				context.getSystemService(Activity.STORAGE_SERVICE);
		ArrayList<String> mountedPoints=new ArrayList<String>();
		try{
			Method getVolumePaths=manager.getClass().getMethod("getVolumePaths");
			String[] points=(String[])getVolumePaths.invoke(manager);
			if(points!=null&&points.length>0){
				Method getVolumeState=manager.getClass().getMethod("getVolumeState", String.class);
				for(int i=0;i<points.length;i++){
					String state=(String)getVolumeState.invoke(manager, points[i]);
					if(Environment.MEDIA_MOUNTED.equals(state))
						mountedPoints.add(points[i]);
				}
				return mountedPoints;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
