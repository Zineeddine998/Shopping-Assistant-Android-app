package com.android.tools.ir.server;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.ProcessErrorStateInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build.VERSION;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Restarter {
    public static List<Activity> getActivities(Context context, boolean foregroundOnly) {
        List<Activity> list = new ArrayList();
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = MonkeyPatcher.getActivityThread(context, activityThreadClass);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            if (hasAppCrashed(context, activityThreadClass, activityThread)) {
                return new ArrayList();
            }
            Map collection = activitiesField.get(activityThread);
            Collection c;
            if (collection instanceof HashMap) {
                c = ((HashMap) collection).values();
            } else if (VERSION.SDK_INT < 19 || !(collection instanceof ArrayMap)) {
                return list;
            } else {
                c = ((ArrayMap) collection).values();
            }
            for (Object activityClientRecord : c) {
                Field pausedField;
                Class activityClientRecordClass = activityClientRecord.getClass();
                if (foregroundOnly) {
                    pausedField = activityClientRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (pausedField.getBoolean(activityClientRecord)) {
                    }
                }
                pausedField = activityClientRecordClass.getDeclaredField("activity");
                pausedField.setAccessible(true);
                Activity activity = (Activity) pausedField.get(activityClientRecord);
                if (activity != null) {
                    list.add(activity);
                }
            }
            return list;
        } catch (Throwable e) {
            String str = Logging.LOG_TAG;
            if (Log.isLoggable(str, 5)) {
                Log.w(str, "Error retrieving activities", e);
            }
        }
    }

    public static Activity getForegroundActivity(Context context) {
        List<Activity> list = getActivities(context, true);
        return list.isEmpty() ? null : (Activity) list.get(0);
    }

    private static String getPackageName(Class activityThreadClass, Object activityThread) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (String) activityThreadClass.getDeclaredMethod("currentPackageName", new Class[0]).invoke(activityThread, new Object[0]);
    }

    private static boolean hasAppCrashed(Context context, Class activityThreadClass, Object activityThread) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (context == null || activityThread == null) {
            return false;
        }
        String currentPackageName = getPackageName(activityThreadClass, activityThread);
        List<ProcessErrorStateInfo> processesInErrorState = ((ActivityManager) context.getSystemService("activity")).getProcessesInErrorState();
        if (processesInErrorState != null) {
            for (ProcessErrorStateInfo info : processesInErrorState) {
                if (info.processName.equals(currentPackageName) && info.condition != 0) {
                    String str = Logging.LOG_TAG;
                    if (Log.isLoggable(str, 2)) {
                        Log.v(str, "App Thread has crashed, return empty activity list.");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static void restartActivity(Activity activity) {
        StringBuilder stringBuilder;
        String str = Logging.LOG_TAG;
        if (Log.isLoggable(str, 2)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("About to restart ");
            stringBuilder.append(activity.getClass().getSimpleName());
            Log.v(str, stringBuilder.toString());
        }
        while (activity.getParent() != null) {
            if (Log.isLoggable(str, 2)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(activity.getClass().getSimpleName());
                stringBuilder.append(" is not a top level activity; restarting ");
                stringBuilder.append(activity.getParent().getClass().getSimpleName());
                stringBuilder.append(" instead");
                Log.v(str, stringBuilder.toString());
            }
            activity = activity.getParent();
        }
        activity.recreate();
    }

    public static void restartActivityOnUiThread(Activity activity) {
        activity.runOnUiThread(new 1(activity));
    }

    public static void restartApp(Context appContext, Collection<Activity> knownActivities, boolean toast) {
        if (!knownActivities.isEmpty()) {
            Context foreground = getForegroundActivity(appContext);
            String str = Logging.LOG_TAG;
            if (foreground != null) {
                if (toast) {
                    showToast(foreground, "Restarting app to apply incompatible changes");
                }
                if (Log.isLoggable(str, 2)) {
                    Log.v(str, "RESTARTING APP");
                }
                Context context = foreground;
                ((AlarmManager) context.getSystemService("alarm")).set(1, System.currentTimeMillis() + 100, PendingIntent.getActivity(context, 0, new Intent(context, foreground.getClass()), 268435456));
                if (Log.isLoggable(str, 2)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Scheduling activity ");
                    stringBuilder.append(foreground);
                    stringBuilder.append(" to start after exiting process");
                    Log.v(str, stringBuilder.toString());
                }
            } else {
                showToast((Activity) knownActivities.iterator().next(), "Unable to restart app");
                if (Log.isLoggable(str, 2)) {
                    Log.v(str, "Couldn't find any foreground activities to restart for resource refresh");
                }
            }
            System.exit(0);
        }
    }

    static void showToast(final Activity activity, final String text) {
        String str = Logging.LOG_TAG;
        if (Log.isLoggable(str, 2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("About to show toast for activity ");
            stringBuilder.append(activity);
            stringBuilder.append(": ");
            stringBuilder.append(text);
            Log.v(str, stringBuilder.toString());
        }
        activity.runOnUiThread(new Runnable() {
            public void run() {
                String str = Logging.LOG_TAG;
                try {
                    Context context = activity.getApplicationContext();
                    if ((context instanceof ContextWrapper) && ((ContextWrapper) context).getBaseContext() == null) {
                        if (Log.isLoggable(str, 5)) {
                            Log.w(str, "Couldn't show toast: no base context");
                        }
                        return;
                    }
                    int duration = 0;
                    if (text.length() >= 60 || text.indexOf(10) != -1) {
                        duration = 1;
                    }
                    Toast.makeText(activity, text, duration).show();
                } catch (Throwable e) {
                    if (Log.isLoggable(str, 5)) {
                        Log.w(str, "Couldn't show toast", e);
                    }
                }
            }
        });
    }

    private static void updateActivity(Activity activity) {
        restartActivity(activity);
    }
}
