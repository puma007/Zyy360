package com.zyy360.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author daimajia
 * @modified Foxhu
 * @version 1.0
 */
public class NetworkUtils {

    public static boolean isWifi(Context mContext){
        ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }

    }

    public static boolean isMobile(Context mContext){
        ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }

    }
}