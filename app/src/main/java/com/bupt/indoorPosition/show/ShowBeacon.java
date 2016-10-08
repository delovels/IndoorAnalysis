package com.bupt.indoorPosition.show;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bupt.indoorPosition.bean.Beacon;
import com.bupt.indoorPosition.bean.IndoorRecord;
import com.bupt.indoorPosition.bean.Neighbor;
import com.bupt.indoorPosition.dao.DBHelper;
import com.bupt.indoorPosition.dao.DBManager;
import com.bupt.indoorPosition.model.ModelService;
import com.bupt.indooranalysis.R;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

	
	public class ShowBeacon extends Activity {  
	    private String[] mListTitle ;  
	    private String[] mListMac ;  
	    private MyReceiver receiver=null;
	    public List<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();  
	    public List<String> list=new ArrayList<String>();
	    public  List<String> currentBeacon=new ArrayList<String>();
		
	    @Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			setContentView(R.layout.show_main);
	        //
			DBManager dBManager=new DBManager(ShowBeacon.this);		
			list=dBManager.readBeacon();
			receiver=new MyReceiver();
			currentBeacon=receiver.returnBeacon();
			  IntentFilter filter=new IntentFilter();
			  filter.addAction("com.bupt.indoorPosition.show.MyReceiver");
			  registerReceiver(receiver,filter);
			 
			  mListTitle=new String[list.size()];
				mListMac =new String[list.size()];
				int length=list.size();
				for(int i=0;i<length;i++){
					int j=i+1;
					mListTitle[i]=j+"";
				}
				Log.d("广播接收长度",""+ currentBeacon.size()/2);
				for(int i=0;i<currentBeacon.size()/2;i++){
					for(int j=0;j<list.size();j++){
					if(currentBeacon.get(2*i)==list.get(j)){
					    list.remove(j);	
					}
				  }
					mListMac[i]=currentBeacon.get(i*2)+"       "+""+currentBeacon.get(2*i+1);
				}
				for(int i=0;i<list.size();i++){
					mListMac[i+currentBeacon.size()/2]=list.get(i);
				}
				 for(int i =0; i < list.size(); i++) {  
					  Log.d("list长度",""+list.size());
				        Map<String,Object> item = new HashMap<String,Object>();  
				        item.put("title", mListTitle[i]);  
				        item.put("info", mListMac[i]);  
				        mData.add(item);   
				    }  
				 Log.d("ShowBeacon111111111", "done"+currentBeacon.size());
				  
				 
				    SimpleAdapter adapter = new SimpleAdapter(ShowBeacon.this,mData,R.layout.simple_item,  
					        new String[]{"title","info"},new int[]{R.id.title,R.id.info});  
				            ListView llist=(ListView) findViewById(R.id.listView1);
				            llist.setAdapter(adapter);
		    
			  
			  
	    }
	    
	    protected void onDestroy() {
	    	super.onDestroy();
	    	this.unregisterReceiver(receiver);
	    }
	}
	    
//	public class MyReceiver extends BroadcastReceiver {
//	     @Override
//	     public void onReceive(Context context, Intent intent) {
////	      Bundle bundle=intent.getStringArrayListExtra("beaconlist");
//	     
//	      currentBeacon=intent.getStringArrayListExtra("beaconlist");
//	      
//			Log.d("ShowBeacon", "done"+currentBeacon.size());
//		    
//		
//
// 
//	     // mac.add(bundle.getString("count"));
//	      Log.d("广播接收",""+ currentBeacon.size());
//	      //
//			
//			
//
//	    
//	            }
//	    }
//	}
