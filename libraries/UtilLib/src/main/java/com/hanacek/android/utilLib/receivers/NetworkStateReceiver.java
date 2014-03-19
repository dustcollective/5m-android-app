package com.hanacek.android.utilLib.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

import com.hanacek.android.utilLib.app.GenericAppContext;
import com.hanacek.android.utilLib.util.IntentUtil;
import com.hanacek.android.utilLib.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {

    private static List<Runnable> doOnReceive;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        if (doOnReceive != null) {
            for (Runnable r : doOnReceive) {
                r.run();
            }
        }

        if (isConnected()) {
            IntentUtil.broadcastConnectionReady(arg0);
        }
    }

    public static void addOnReceiveRunnable(Runnable runnable) {
        if (doOnReceive == null) {
            doOnReceive  = new ArrayList<Runnable>();
        }
        doOnReceive.add(runnable);
    }
    
    private static NetworkInfo networkInfo() {
        ConnectivityManager cm = (ConnectivityManager) GenericAppContext.context().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
    
    public static boolean isConnected() {
        NetworkInfo ni = networkInfo();
        if (ni != null && ni.isConnected()) {
            return true;
        }
        return false;
    }
    
    public static String getConnectionType() {
        NetworkInfo ni = networkInfo();
        if (ni == null) {
            return "unknown";
        }
        int type = ni.getType();
        switch (type) {
        case ConnectivityManager.TYPE_WIFI:
            return "WLAN";
        case ConnectivityManager.TYPE_ETHERNET:
            return "LAN";
        case ConnectivityManager.TYPE_MOBILE:
            {
                int subtype = ni.getSubtype();
                switch (subtype) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "1xRTT";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "CDMA";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return "eHRPD";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "EVDO_0";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "EVDO_A";
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "EVDO_B";
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "HSPA+";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "iDen";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return "unknown";
                }
            }
        }
        Log.debug("Connection: not WIFI, LAN or MOBILE");
        return "unknown";
    }
}
