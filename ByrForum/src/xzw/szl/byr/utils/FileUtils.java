package xzw.szl.byr.utils;

import java.io.File;

import android.net.Uri;
import android.os.Environment;

public class FileUtils {
	
	public static void createDir() {
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory(),ByrBase.FILE_PATH);
			
			if (!file.exists()) {
				file.mkdir();
			}
		}
	}
	
	public static boolean isFileExist(String filepath) {
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory(),filepath);
			
			if (file.exists()) {
				return true;
			}
		}
		return false;
	}

	public static Uri createCameraURI(String path) {
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			
			File file = new File(Environment.getExternalStorageDirectory(),ByrBase.CAMERA_DIR);
			
			if (!file.exists()) file.mkdirs();
			
			file = new File(Environment.getExternalStorageDirectory() + ByrBase.CAMERA_DIR, path);
			
			if (file != null) {
				return Uri.fromFile(file);
			}
		} 
		
		return null;
	}
	
	public static String getCameraFullPath(String path) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			
			return Environment.getExternalStorageDirectory() + ByrBase.CAMERA_DIR + path;
		} 
		
		return path;
	}
}
