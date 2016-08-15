package com.zzw.wingchun;

import java.util.ArrayList;

public class RawResources {
	public static final int FILETYPE=0;
	public static final int IMAGETYPE=1;
	private ArrayList<Resource> resList=new ArrayList<Resource>();

	public static class Resource{
		private String path;
		private String fileName;
		private int resourceId;
		private int type;
		public Resource(String p,String file,int res,int t){
			path=p;
			fileName=file;
			resourceId=res;
			type=t;
		}
		public String getPath(){
			return path;
		}
		public String getFileName(){
			return fileName;
		}
		public int getResourceId(){
			return resourceId;
		}
		public int getType(){
			return type;
		}
	}
	
	public void addResource(
			String path,String fileName,int resourceId,int type){
		resList.add(new Resource(path,fileName,resourceId,type));
	}
	public ArrayList<Resource> getResourceList(){
		return resList;
	}
}
