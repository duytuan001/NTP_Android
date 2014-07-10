package com.duytuan.ntptimesync.ultils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {
	
	private Context context;
	
	public Network(Context context){
		this.context = context;
	}
	
    /**
     * check network status
     * @return true | false
     */
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    
    public void showNetworkStatusDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Network Info")
        	.setMessage("Internet not available, Cross check your internet connectivity and try again")
        	.setIcon(android.R.drawable.ic_dialog_info)
        	.setCancelable(false)
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			});
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
