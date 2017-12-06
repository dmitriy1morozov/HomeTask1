package com.example.systemservice_broadcastreceiver_example;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

		@butterknife.BindView(R.id.text_main_timeout) EditText mTimeoutText;
		@butterknife.BindView(R.id.btn_main_fire) Button mFireButton;

		MyServiceBroadcastReceiver mBroadcastReceiver;

		@Override protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				butterknife.ButterKnife.bind(this);
		}

		@Override protected void onResume() {
				super.onResume();
		}

		@Override protected void onPause() {
				super.onPause();
		}

		//----------------------------------------------------------------------------------------------
		@OnClick(R.id.btn_main_fire)void fireEvent(){
				int timeout;
				if(mTimeoutText.getText().toString() != ""){
						timeout = Integer.parseInt(mTimeoutText.getText().toString());
				} else{
						timeout = 1;
				}

				Intent intent = new Intent(this, MyServiceBroadcastReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 123123, intent, 0);
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

				//Android > 5
				//ELAPSED_REALTIME_WAKEUP - если телефон потушен - разбудит
				//ELAPSED_REALTIME_WAKEUP - если телефон потушен -  НЕ разбудит
				alarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeout*1000, 10000, pendingIntent);
				//alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + timeout*1000, 10000, pendingIntent);
				////Android > 6
				//alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,SystemClock.elapsedRealtime() + timeout*1000, pendingIntent);
				//alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + timeout*1000, pendingIntent);
				//Toast.makeText(this, "Alarm is set in " + timeout + "seconds.", Toast.LENGTH_SHORT).show();
		}
}
