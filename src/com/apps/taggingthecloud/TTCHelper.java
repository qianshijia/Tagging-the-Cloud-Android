package com.apps.taggingthecloud;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class TTCHelper 
{
	public static boolean check3GNetwork(Context context)
	{
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
                return false;
        } else {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if(info == null){
                        return false;
                }else{
                        if(info.isAvailable()){
                                return true;
                        }
                      
                }
        }
        return false;
	}
	
	public static boolean checkWiFi(Context inContext) {
		WifiManager mWifiManager = (WifiManager) inContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
		if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
		    return true;
		} 
		else 
		{
		     return false;   
		}
	}
	


}
