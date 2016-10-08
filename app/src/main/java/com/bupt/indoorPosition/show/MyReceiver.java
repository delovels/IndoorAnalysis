package com.bupt.indoorPosition.show;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
	public  List<String> MycurrentBeacon=new ArrayList<String>();

    @Override
    public void onReceive(Context context, Intent intent) {
//     Bundle bundle=intent.getStringArrayListExtra("beaconlist");
    
     MycurrentBeacon=intent.getStringArrayListExtra("beaconlist");
     
    }
    public List<String> returnBeacon(){
    	return MycurrentBeacon;
    }
}
