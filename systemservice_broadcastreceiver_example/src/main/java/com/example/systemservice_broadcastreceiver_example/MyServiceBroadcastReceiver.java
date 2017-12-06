package com.example.systemservice_broadcastreceiver_example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class MyServiceBroadcastReceiver extends BroadcastReceiver {
		@Override public void onReceive(Context context, Intent intent) {
				Toast.makeText(context, "Your time is up!", Toast.LENGTH_SHORT).show();
				Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(2000);
		}
}
