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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class ClockWidgetConfigure extends Activity {
	private static final String TAG = "ClockWidgetConfigure";
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private String mWidget = null;
	private boolean ampm = false;
	private int hour = 0;
	private int minute = 0;
	private boolean set = false;
	private boolean repeat = false;

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
				mWidget = extras.getString(WIDGET);
				Log.d(TAG, "mAppWidgetId=" + String.valueOf(mAppWidgetId));
				Log.d(TAG, "mWidget=" + mWidget);
				StaticHash hash = new StaticHash(this);
				ampm = hash.get(AMPM, String.valueOf(mAppWidgetId), false);
				hour = hash.get(HOUR, String.valueOf(mAppWidgetId), 0);
				minute = hash.get(MINUTE, String.valueOf(mAppWidgetId), 0);
				minute /= 5;
				set = hash.get(SET, String.valueOf(mAppWidgetId), false);
				repeat = hash.get(REPEAT, String.valueOf(mAppWidgetId), false);
			}
			setContentView(R.layout.widget_clock_configure);

			RadioButton clock_configure_am_RadioButton = (RadioButton) findViewById(R.id.clock_configure_am_RadioButton);
			clock_configure_am_RadioButton.setChecked(!ampm);
			RadioButton clock_configure_pm_RadioButton = (RadioButton) findViewById(R.id.clock_configure_pm_RadioButton);
			clock_configure_pm_RadioButton.setChecked(ampm);

			Spinner clock_configure_hours_Spinner = (Spinner) findViewById(R.id.clock_configure_hours_Spinner);
			clock_configure_hours_Spinner.setSelection(hour);
			// Spinner のアイテムが選択された時に呼び出されるコールバックを登録
			clock_configure_hours_Spinner
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						// アイテムが選択された時の動作
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// Spinner を取得
							Spinner spinner = (Spinner) parent;
							// 選択されたアイテムのテキストを取得
							hour = spinner.getSelectedItemPosition();
							Log.d(TAG, "hour=" + String.valueOf(hour));
						}

						// 何も選択されなかった時の動作
						public void onNothingSelected(AdapterView<?> parent) {
							hour = 0;
						}
					});

			Spinner clock_configure_mins_Spinner = (Spinner) findViewById(R.id.clock_configure_mins_Spinner);
			clock_configure_mins_Spinner.setSelection(minute);
			// Spinner のアイテムが選択された時に呼び出されるコールバックを登録
			clock_configure_mins_Spinner
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						// アイテムが選択された時の動作
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// Spinner を取得
							Spinner spinner = (Spinner) parent;
							// 選択されたアイテムのテキストを取得
							minute = spinner.getSelectedItemPosition();
							Log.d(TAG, "minute=" + String.valueOf(minute));
						}

						// 何も選択されなかった時の動作
						public void onNothingSelected(AdapterView<?> parent) {
							minute = 0;
						}
					});
			ToggleButton clock_configure_alarm_ToggleButton = (ToggleButton) findViewById(R.id.clock_configure_alarm_ToggleButton);
			clock_configure_alarm_ToggleButton.setChecked(set);
			CheckBox clock_configure_repeat_CheckBox = (CheckBox) findViewById(R.id.clock_configure_repeat_CheckBox);
			if (set) {
				clock_configure_repeat_CheckBox.setVisibility(View.VISIBLE);
			} else {
				clock_configure_repeat_CheckBox.setVisibility(View.INVISIBLE);
				repeat = false;
			}
			clock_configure_repeat_CheckBox.setChecked(repeat);

			Log.i(TAG, "onCreate end");
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	// Button の onClick で実装
	public void onToggleButtonClick(View v) {
		try {
			ToggleButton clock_configure_alarm_ToggleButton = (ToggleButton) findViewById(R.id.clock_configure_alarm_ToggleButton);
			set = clock_configure_alarm_ToggleButton.isChecked();
			CheckBox clock_configure_repeat_CheckBox = (CheckBox) findViewById(R.id.clock_configure_repeat_CheckBox);
			if (set) {
				clock_configure_repeat_CheckBox.setVisibility(View.VISIBLE);
			} else {
				clock_configure_repeat_CheckBox.setVisibility(View.INVISIBLE);
				repeat = false;
				clock_configure_repeat_CheckBox.setChecked(repeat);
			}
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	// Button の onClick で実装
	public void onClockConfigureOKButtonClick(View v) {
		try {
			Log.i(TAG, "onOKButtonClick");
			RadioButton clock_configure_pm_RadioButton = (RadioButton) findViewById(R.id.clock_configure_pm_RadioButton);
			ampm = clock_configure_pm_RadioButton.isChecked();
			CheckBox clock_configure_repeat_CheckBox = (CheckBox) findViewById(R.id.clock_configure_repeat_CheckBox);
			repeat = clock_configure_repeat_CheckBox.isChecked();
			StaticHash hash = new StaticHash(this);
			hash.put(AMPM, String.valueOf(mAppWidgetId), ampm);
			hash.put(HOUR, String.valueOf(mAppWidgetId), hour);
			hash.put(MINUTE, String.valueOf(mAppWidgetId), minute * 5);
			hash.put(SET, String.valueOf(mAppWidgetId), set);
			hash.put(REPEAT, String.valueOf(mAppWidgetId), repeat);
			Intent intent = null;
			if (ANALOG.compareTo(mWidget) == 0) {
				intent = new Intent(this, KumamonClockWidgetAnalog.class);
			}
			if (DIGITAL.compareTo(mWidget) == 0) {
				intent = new Intent(this, KumamonClockWidgetDigital.class);
			}
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			intent.setAction(CONFIG_DONE);
			sendBroadcast(intent);
			setResult(RESULT_OK, intent);
			finish();
			Log.i(TAG, "onOKButtonClick End");
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}
}
