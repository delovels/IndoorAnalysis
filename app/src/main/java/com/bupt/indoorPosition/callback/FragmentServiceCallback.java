package com.bupt.indoorPosition.callback;

import android.content.Intent;

public interface FragmentServiceCallback {
	public void startOrStopActivityService(Intent intent, boolean isStart);
}
