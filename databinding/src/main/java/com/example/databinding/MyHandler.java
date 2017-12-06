package com.example.databinding;

import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MyHandler {
		private static final String TAG = "MyLogs MyHandler";

		public void onClickFriend(View view){
				((Button)view).setText("Test!");
				Log.d(TAG, "onClickFriend: ");
		}
}
