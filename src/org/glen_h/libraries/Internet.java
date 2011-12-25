package org.glen_h.libraries;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.glen_h.libraries.InvalidNetworkStateException;

/**
 * Contains basic internet-related functions, such as check connectivity state or make URI/URL.
 * Please see methods in this class for detailed documentation.
 * @see java.net.URL URL
 * @see java.net.URI URI
 * @see android.net.NetworkInfo NetworkInfo
 * @see android.net.ConnectivityManager ConnectivityManager
 * @author Glen Husman
 */

public class Internet extends Activity{
	
	/**
	 * Checks whether the network connectivity state is CONNECTED, CONNECTING, DISCONNECTED, DISCONNECTING, SUSPENDED, or UNKNOWN.
	 * All of those except connected and connecting will return false. If it does not receive any one of those, it will return false.
	 * @see android.net.NetworkInfo NetworkInfo
	 * @see android.net.ConnectivityManager ConnectivityManager
	 * @param Context
	 * @author Glen Husman
	 * @return boolean
	 */
	public static boolean isConnected(Context appContext) {
	// A method that checks whether the device is online, NO EXCEPTIONS THROWABLE!
		ConnectivityManager connec = (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isOnline = false;
		
	    if (connec != null && (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) ||(connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){ 
	        //You are connected to the internet.
	    	isOnline =  true;
	    }
	    else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ) {             
		     // Connecting. Set boolean false.
		    	isOnline = true;
		    }
	    else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {             
	     //Not connected. Set boolean false.
	    	isOnline = false;
	    }
	    else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTING ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTING ) {             
		     //Not connected. Set boolean false.
		    	isOnline = false;
		    }
	    else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.SUSPENDED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.SUSPENDED ) {             
		     // Suspended. Set boolean false.
		    	isOnline = false;
		    }
	    else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.UNKNOWN ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.UNKNOWN ) {             
		     // Unknown. Set boolean false.
		    	isOnline = false;
		    }
	    
	    return isOnline;
	}
	
	/**
	 * Checks whether the network connectivity state is CONNECTED or DISCONNECTED.
	 * If it is not either one, it will throw an "InvalidNetworkStateException."
	 * @see android.net.NetworkInfo NetworkInfo
	 * @see android.net.ConnectivityManager ConnectivityManager
	 * @param Context
	 * @deprecated Use {@link #isConnected} instead.
	 * @author Glen Husman
	 * @return boolean
	 */
	public static boolean isOnline(Context appContext) throws InvalidNetworkStateException {
	
	/*
	 * Checks whether device is online.
	 * 
	 * REQUIRED PERMISSIONS:
	 * android.permission.ACCESS_NETWORK_STATE
	 * 
	 * RECOMMENDED PERMISSIONS:
	 * android.permission.INTERNET
	 * 
	 * So you can use this value AND connect to the internet
	 */	

	ConnectivityManager connec = (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	boolean isOnline;
	InvalidNetworkStateException invalid_network_state = new InvalidNetworkStateException();
    if (connec != null && (connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) ||(connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){ 
        //You are connected to the internet.
    	isOnline =  true;
    }
    else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED ) {             
     //Not connected. Set boolean false.
    	isOnline = false;
    }
    else {             
        throw invalid_network_state;
        }
		return isOnline;
	}

	/**
	 * Makes a URL from a string without the need for a try/catch.
	 * @see java.net.URL URL
	 * @author Glen Husman
	 * @return URL
	 */
	public static URL MakeURL (String webaddress) {
		
		/*
		 * Makes a URL from a string
		 */
		
		URL website;
		try {
			website = new URL(webaddress);
		} catch (MalformedURLException e) {
			website = null;
			Log.e("Malformed URL Exception was thrown on string to URL conversion", null);
		}
	return website;
	}
	
/**
 * Makes a URI from a string without the need for a try/catch.
 * The difference between a URL and URI is that a URI can have any protocol, whereas a URL can only have a limited number of protocols.
 * @see java.net.URI URI
 * @author Glen Husman
 * @return URI
 */
public static URI MakeURI (String webaddress) {
		
		/*
		 * Makes a URI from a string
		 */
		
		URI website;
		try {
			website = new URI(webaddress);
		} catch (URISyntaxException e) {
			website = null;
		}
	return website;
	}
}
