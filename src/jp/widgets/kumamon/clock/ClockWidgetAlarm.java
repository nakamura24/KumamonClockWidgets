/*
 * Copyright (C) 2012 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */
package jp.widgets.kumamon.clock;

import jp.widgets.kumamon.lib.*;
import static jp.widgets.kumamon.clock.ClockWidgetConstant.*;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

public class ClockWidgetAlarm extends Activity {
	private static final String TAG = "ClockWidgetAlarm";
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private Ringtone mRingtone;
	private boolean mRepeat = false;

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		public void run() {
			if (mRepeat) {
				if (!mRingtone.isPlaying())
					mRingtone.play();
				mHandler.postDelayed(mRunnable, 1000);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		try {
			// setResult(RESULT_CANCELED);
			// AppWidgetID の取得
			Intent intent = getIntent();
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
				Log.d(TAG, "mAppWidgetId=" + String.valueOf(mAppWidgetId));
			}
			setContentView(R.layout.widget_clock_alarm);
			getWindow().setLayout(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			// 現在設定されている着信音を選択する
			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			mRingtone = RingtoneManager.getRingtone(getApplicationContext(),
					uri);
			if (mRingtone != null) {
				mRepeat = true;
				mRingtone.play();
				mHandler.postDelayed(mRunnable, 1000);
			}
			Log.i(TAG, "onCreate end");
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	// Button の onClick で実装
	public void onClockAlarmStopButtonClick(View v) {
		try {
			Log.i(TAG, "onOKButtonClick");
			mRepeat = false;
			if (mRingtone != null)
				mRingtone.stop();
			if (mHandler != null)
				mHandler.removeCallbacks(mRunnable);

			Intent intent = null;
			intent = new Intent(this, KumamonClockWidgetAnalog.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			intent.setAction(ALARM_DONE);
			sendBroadcast(intent);
			setResult(RESULT_OK, intent);
			finish();
			Log.i(TAG, "onOKButtonClick End");
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}
}
