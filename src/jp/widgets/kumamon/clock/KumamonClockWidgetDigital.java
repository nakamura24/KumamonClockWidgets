﻿/*
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

import java.util.*;

import android.os.Bundle;
import android.os.IBinder;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

public class KumamonClockWidgetDigital extends WidgetBase {
	private static final String TAG = "KumamonClockWidgetDigital";

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.i(TAG, "onEnabled");
		try {
			Intent intent = new Intent(context, WidgetService.class);
			context.startService(intent);
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		try {
			StaticHash hash = new StaticHash(context);
			for (int i = 0; i < appWidgetIds.length; i++) {
				Log.d(TAG, "onUpdate - " + String.valueOf(appWidgetIds[i]));
				hash.put(WIDGET, String.valueOf(appWidgetIds[i]), DIGITAL);
				UpdateAppWidget(context, appWidgetIds[i]);
			}
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.i(TAG, "onReceive" + intent.getAction());
		try {
			if (CONFIG_DONE.equals(intent.getAction())) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					int appWidgetId = extras.getInt(
							AppWidgetManager.EXTRA_APPWIDGET_ID,
							AppWidgetManager.INVALID_APPWIDGET_ID);
					Log.d(TAG, "appWidgetId=" + String.valueOf(appWidgetId));
					if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
						StaticHash hash = new StaticHash(context);
						boolean ampm = hash.get(AMPM,
								String.valueOf(appWidgetId), false);
						int hour = hash.get(HOUR, String.valueOf(appWidgetId),
								0);
						int minute = hash.get(MINUTE,
								String.valueOf(appWidgetId), 0);
						boolean set = hash.get(SET,
								String.valueOf(appWidgetId), false);
						Log.d(TAG,
								"ampm=" + String.valueOf(ampm) + " hour="
										+ String.valueOf(hour) + " minute="
										+ String.valueOf(minute));
						if (hash.get(ALARM, String.valueOf(appWidgetId), false)) {
							Intent cancelIntent = new Intent(context,
									ClockWidgetAlarm.class);
							cancelIntent.putExtra(
									AppWidgetManager.EXTRA_APPWIDGET_ID,
									appWidgetId);
							cancelIntent.setAction(APPWIDGET_ALARM);
							PendingIntent operation = PendingIntent
									.getActivity(context, appWidgetId,
											cancelIntent, 0);
							AlarmManager alarmManager = (AlarmManager) context
									.getSystemService(Context.ALARM_SERVICE);
							alarmManager.cancel(operation);
							hash.remove(ALARM, String.valueOf(appWidgetId));
						}
						if (set) {
							setAlarm(context, appWidgetId);
						}
					}
				}
			}
			if (ALARM_DONE.equals(intent.getAction())) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					int appWidgetId = extras.getInt(
							AppWidgetManager.EXTRA_APPWIDGET_ID,
							AppWidgetManager.INVALID_APPWIDGET_ID);
					Log.d(TAG, "appWidgetId=" + String.valueOf(appWidgetId));
					if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
						StaticHash hash = new StaticHash(context);
						if (hash.get(SET, String.valueOf(appWidgetId), false)
								&& hash.get(REPEAT,
										String.valueOf(appWidgetId), false)) {
							hash.remove(ALARM, String.valueOf(appWidgetId));
							setAlarm(context, appWidgetId);
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.i(TAG, "onDeleted");
		super.onDeleted(context, appWidgetIds);
		try {
			StaticHash hash = new StaticHash(context);
			for (int appWidgetId : appWidgetIds) {
				hash.remove(WIDGET, String.valueOf(appWidgetId));
				hash.remove(AMPM, String.valueOf(appWidgetId));
				hash.remove(HOUR, String.valueOf(appWidgetId));
				hash.remove(MINUTE, String.valueOf(appWidgetId));
				hash.remove(SET, String.valueOf(appWidgetId));
				hash.remove(REPEAT, String.valueOf(appWidgetId));
				hash.remove(ALARM, String.valueOf(appWidgetId));
			}
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	@Override
	public void onDisabled(Context context) {
		Log.i(TAG, "onDisabled");
		super.onDisabled(context);
	}

	private static void UpdateAppWidget(Context context, int appWidgetId) {
		Log.i(TAG, "UpdateAppWidget - " + String.valueOf(appWidgetId));
		try {
			String week[] = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
			Calendar Cal = Calendar.getInstance();
			int year = Cal.get(Calendar.YEAR);
			int month = Cal.get(Calendar.MONTH);
			int day_of_month = Cal.get(Calendar.DAY_OF_MONTH);
			int day_of_week = Cal.get(Calendar.DAY_OF_WEEK);
			int hour = Cal.get(Calendar.HOUR_OF_DAY);
			int minute = Cal.get(Calendar.MINUTE);
			// ボタンが押された時に発行されるインテントを準備する
			Intent buttonIntent = new Intent(context,
					ClockWidgetConfigure.class);
			buttonIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetId);
			buttonIntent.putExtra(WIDGET, DIGITAL);
			buttonIntent.setAction(APPWIDGET_CONFIGURE);
			PendingIntent pendingIntent = PendingIntent.getActivity(context,
					appWidgetId, buttonIntent, 0);
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_clock_digital);
			remoteViews.setOnClickPendingIntent(R.id.clock_back_ImageView,
					pendingIntent);
			remoteViews.setTextViewText(R.id.clock_date_TextView, String
					.format("%4d/%02d/%02d(%s)", year, month + 1, day_of_month,
							week[day_of_week - 1]));
			remoteViews.setTextViewText(R.id.clock_time_TextView,
					String.format("%02d:%02d", hour, minute));
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	private void setAlarm(Context context, int appWidgetId) {
		Log.i(TAG, "setAlarm - " + String.valueOf(appWidgetId));
		try {
			Calendar alarmDate = Calendar.getInstance();
			StaticHash hash = new StaticHash(context);
			if (hash.get(AMPM, String.valueOf(appWidgetId), false)) {
				alarmDate.set(Calendar.HOUR_OF_DAY,
						12 + hash.get(HOUR, String.valueOf(appWidgetId), 0));
				alarmDate.set(Calendar.MINUTE,
						hash.get(MINUTE, String.valueOf(appWidgetId), 0));
				alarmDate.set(Calendar.SECOND, 0);
				alarmDate.set(Calendar.MILLISECOND, 0);
			} else {
				alarmDate.set(Calendar.HOUR_OF_DAY,
						hash.get(HOUR, String.valueOf(appWidgetId), 0));
				alarmDate.set(Calendar.MINUTE,
						hash.get(MINUTE, String.valueOf(appWidgetId), 0));
				alarmDate.set(Calendar.SECOND, 0);
				alarmDate.set(Calendar.MILLISECOND, 0);
			}
			Calendar now = Calendar.getInstance();
			if (now.getTimeInMillis() > alarmDate.getTimeInMillis())
				alarmDate.add(Calendar.DATE, 1);
			Log.d(TAG, "setAlarm - " + alarmDate.toString());

			Intent intent = new Intent(context, ClockWidgetAlarm.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			intent.setAction(APPWIDGET_ALARM);
			PendingIntent operation = PendingIntent.getActivity(context,
					appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					alarmDate.getTimeInMillis(), operation);
			hash.put(ALARM, String.valueOf(appWidgetId), true);
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	public static class WidgetService extends Service {
		@Override
		public void onCreate() {
			super.onCreate();
			Log.i(TAG, "onCreate");
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_USER_PRESENT);
			registerReceiver(timetichReceiver, filter);
		}

		@Override
		public void onDestroy() {
			Log.i(TAG, "onDestroy");
			unregisterReceiver(timetichReceiver);
			super.onDestroy();
		}

		@Override
		public IBinder onBind(Intent in) {
			return null;
		}
	}

	private static final BroadcastReceiver timetichReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "onReceive - " + intent.getAction());
			try {
				context = context.getApplicationContext();
				String action = intent.getAction();
				StaticHash hash = new StaticHash(context);
				ArrayList<String> keys = hash.keys(WIDGET);
				if (action.equals(Intent.ACTION_TIME_TICK)) {
					for (int i = 0; i < keys.size(); i++) {
						Log.d(TAG,
								"appWidgetId - " + String.valueOf(keys.get(i)));
						UpdateAppWidget(context, Integer.parseInt(keys.get(i)));
					}
				}
				if (Intent.ACTION_USER_PRESENT.equals(action)) {
					Log.d(TAG, "ACTION_USER_PRESENT - " + intent.getAction());
					for (int i = 0; i < keys.size(); i++) {
						Log.d(TAG,
								"appWidgetId - " + String.valueOf(keys.get(i)));
						UpdateAppWidget(context, Integer.parseInt(keys.get(i)));
					}
				}
			} catch (Exception e) {
				ExceptionLog.Log(TAG, e);
			}
		}
	};
}
