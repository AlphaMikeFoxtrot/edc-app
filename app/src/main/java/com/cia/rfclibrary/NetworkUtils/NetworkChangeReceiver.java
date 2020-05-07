package com.cia.rfclibrary.NetworkUtils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by ANONYMOUS on 14-Dec-17.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "CheckNetworkStatus";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.v(LOG_TAG, "Receieved notification about network status");

        check(context);

    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            // Toast.makeText(context, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void check(final Context context){
        if(!isNetworkAvailable(context)){

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Unable to connect to Internet")
                    .setCancelable(false)
                    .setPositiveButton("Reload", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            check(context);
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }

    }

}
