package com.bupt.indoorPosition.fragment;

import com.bupt.indoorPosition.callback.SettingUpdateCallback;
import com.bupt.indoorPosition.location.LocationProvider;
import com.bupt.indoorPosition.model.ModelService;
import com.bupt.indoorPosition.uti.Constants;
import com.bupt.indoorpostion.HomeActivity;
import com.bupt.indoorpostion.MainActivity;
import com.bupt.indoorpostion.R;

//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentSetting extends Fragment implements SettingUpdateCallback {
	private HomeActivity parent = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.setting, container, false);
	}

	@Override
	public void handleUpdateMessage(Message msg) {
		if (msg.what == Constants.MSG.UPDATE) {
			Bundle b = msg.getData();
			boolean status = b.getBoolean("status");
			if (status) {
				Toast.makeText(parent, "更新成功", 3000).show();
			} else {
				Toast.makeText(parent, "更新失败", 3000).show();
			}
		}

	}

}
