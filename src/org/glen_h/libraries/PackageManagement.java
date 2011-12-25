package org.glen_h.libraries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

/**
 * Contains basic package management functions.
 * @see android.content.pm.PackageManager PackageManager
 * @see android.content.pm.ApplicationInfo ApplicationInfo
 * @author Glen Husman
 */

public class PackageManagement extends Activity{
	
	/**
	 * Installed application list.
	 * @see android.content.pm.PackageManager PackageManager
	 * @see android.content.pm.ApplicationInfo ApplicationInfo
	 * @author Glen Husman
	 * @return String
	 */
	public static String packageList(Context appContext) {
		PackageManager pm = appContext.getPackageManager();
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		
		String packages_list = packages.toString();
		return packages_list;
	}

	/**
	 * Check whether a package is installed.
	 * @see android.content.pm.PackageManager PackageManager
	 * @author Glen Husman
	 * @return boolean
	 */
	public static boolean appInstalledCheck(String pkg, Context appContext)
    {
        /*
         * Checks if a package is installed, and returns a boolean.
         */
		
		PackageManager pm = appContext.getPackageManager();
		
		boolean app_installed = false;
        try
        {
               pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
               app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
               app_installed = false;
        }
        return app_installed ;
    }
	/**
	 * Install a package (APK file) from the internet. Needs INTERNET, WRITE_EXTERNAL_STORAGE, and INSTALL_PACKAGES permissions of the android.permissions package.
	 * @see android.content.pm.PackageManager PackageManager
	 * @author Glen Husman
	 */
	public static void InstallOnlinePackage(String urlofpackage, Context appContext){
		try {
	          /*
	           * Installs a package from the web.
	           * 
	           * REQUIRES PERMISSIONS:
	           * android.permission.INTERNET
	           * android.permission.WRITE_EXTERNAL_STORAGE
	           * android.permission.INSTALL_PACKAGES
	           * android.permission.DELETE_PACKAGES
	           */
	            URL url = new URL(urlofpackage);
	            HttpURLConnection c = (HttpURLConnection) url.openConnection();
	            c.setRequestMethod("GET");
	            c.setDoOutput(true);
	            c.connect();

	            String PATH = Environment.getExternalStorageDirectory() + "/download/";
	            File file = new File(PATH);
	            file.mkdirs();
	            File outputFile = new File(file, "app.apk");
	            FileOutputStream fos = new FileOutputStream(outputFile);

	            InputStream is = c.getInputStream();

	            byte[] buffer = new byte[1024];
	            int len1 = 0;
	            while ((len1 = is.read(buffer)) != -1) {
	                fos.write(buffer, 0, len1);
	            }
	            fos.close();
	            is.close();

	            Intent intent = new Intent(Intent.ACTION_VIEW);
	            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "app.apk")), "application/vnd.android.package-archive");
	            appContext.startActivity(intent);
	        } catch (IOException e) {
	            Toast.makeText(appContext, "Install error!", Toast.LENGTH_LONG).show();
	        }
	  } 
	
}
