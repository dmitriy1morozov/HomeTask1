package com.example.databinding;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.databinding.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
		private static final String TAG = "MyLogs MainActivity";

		private User mUser = new User("Test", "User");

		@Override protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
				binding.setUser(mUser);

				Button mLogData = (Button)findViewById(R.id.btn_main_logdata);
				mLogData.setOnClickListener(new View.OnClickListener() {
						@Override public void onClick(View v) {
								Log.d(TAG, "mUser = " + mUser.toString());
						}
				});
		}
}
