package com.android.tools.ir.server;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.SparseArray;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MonkeyPatcher {
    public static Object getActivityThread(Context context, Class<?> activityThread) {
        if (activityThread == null) {
            try {
                activityThread = Class.forName("android.app.ActivityThread");
            } catch (Throwable th) {
                return null;
            }
        }
        Method m = activityThread.getMethod("currentActivityThread", new Class[0]);
        m.setAccessible(true);
        Object currentActivityThread = m.invoke(null, new Object[0]);
        if (currentActivityThread == null && context != null) {
            Field mLoadedApk = context.getClass().getField("mLoadedApk");
            mLoadedApk.setAccessible(true);
            Object apk = mLoadedApk.get(context);
            Field mActivityThreadField = apk.getClass().getDeclaredField("mActivityThread");
            mActivityThreadField.setAccessible(true);
            currentActivityThread = mActivityThreadField.get(apk);
        }
        return currentActivityThread;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: f
        g.d.a.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:80:0x0208 in {2, 17, 20, 25, 30, 33, 34, 36, 37, 46, 49, 50, 54, 63, 66, 67, 68, 69, 71, 73, 75, 77, 79} preds:[]
        	at e.b.d.g.a.f.d(SourceFile:242)
        	at e.b.d.g.a.f.c(SourceFile:52)
        	at e.b.d.g.a.f.a(SourceFile:42)
        	at e.b.d.g.i.a(SourceFile:28)
        	at e.b.d.g.i.b(SourceFile:15)
        	at e.b.d.g.k.accept(Unknown Source:4)
        	at d.b.a.c.l.f.a(SourceFile:85)
        	at e.b.d.g.i.a(SourceFile:15)
        	at e.b.b.a(SourceFile:33)
        	at e.a.d.a(SourceFile:402)
        	at e.a.f.a(SourceFile:62)
        	at e.a.d.a(SourceFile:296)
        	at e.a.e.run(Unknown Source:10)
        */
    public static void monkeyPatchExistingResources(android.content.Context r16, java.lang.String r17, java.util.Collection<android.app.Activity> r18) {
        /*
        r1 = "mTheme";
        if (r17 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = android.content.res.AssetManager.class;	 Catch:{ Throwable -> 0x01ff }
        r2 = 0;	 Catch:{ Throwable -> 0x01ff }
        r3 = new java.lang.Class[r2];	 Catch:{ Throwable -> 0x01ff }
        r0 = r0.getConstructor(r3);	 Catch:{ Throwable -> 0x01ff }
        r3 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x01ff }
        r0 = r0.newInstance(r3);	 Catch:{ Throwable -> 0x01ff }
        r0 = (android.content.res.AssetManager) r0;	 Catch:{ Throwable -> 0x01ff }
        r3 = r0;	 Catch:{ Throwable -> 0x01ff }
        r0 = android.content.res.AssetManager.class;	 Catch:{ Throwable -> 0x01ff }
        r4 = "addAssetPath";	 Catch:{ Throwable -> 0x01ff }
        r5 = 1;	 Catch:{ Throwable -> 0x01ff }
        r6 = new java.lang.Class[r5];	 Catch:{ Throwable -> 0x01ff }
        r7 = java.lang.String.class;	 Catch:{ Throwable -> 0x01ff }
        r6[r2] = r7;	 Catch:{ Throwable -> 0x01ff }
        r0 = r0.getDeclaredMethod(r4, r6);	 Catch:{ Throwable -> 0x01ff }
        r4 = r0;	 Catch:{ Throwable -> 0x01ff }
        r4.setAccessible(r5);	 Catch:{ Throwable -> 0x01ff }
        r0 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x01ff }
        r0[r2] = r17;	 Catch:{ Throwable -> 0x01ff }
        r0 = r4.invoke(r3, r0);	 Catch:{ Throwable -> 0x01ff }
        r0 = (java.lang.Integer) r0;	 Catch:{ Throwable -> 0x01ff }
        r0 = r0.intValue();	 Catch:{ Throwable -> 0x01ff }
        if (r0 == 0) goto L_0x01f3;	 Catch:{ Throwable -> 0x01ff }
    L_0x003a:
        r0 = android.content.res.AssetManager.class;	 Catch:{ Throwable -> 0x01ff }
        r6 = "ensureStringBlocks";	 Catch:{ Throwable -> 0x01ff }
        r7 = new java.lang.Class[r2];	 Catch:{ Throwable -> 0x01ff }
        r0 = r0.getDeclaredMethod(r6, r7);	 Catch:{ Throwable -> 0x01ff }
        r6 = r0;	 Catch:{ Throwable -> 0x01ff }
        r6.setAccessible(r5);	 Catch:{ Throwable -> 0x01ff }
        r0 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x01ff }
        r6.invoke(r3, r0);	 Catch:{ Throwable -> 0x01ff }
        r7 = "mResourcesImpl";
        r8 = 0;
        r9 = "mAssets";
        if (r18 == 0) goto L_0x012b;
    L_0x0054:
        r10 = r18.iterator();	 Catch:{ Throwable -> 0x01ff }
    L_0x0058:
        r0 = r10.hasNext();	 Catch:{ Throwable -> 0x01ff }
        if (r0 == 0) goto L_0x012b;	 Catch:{ Throwable -> 0x01ff }
    L_0x005e:
        r0 = r10.next();	 Catch:{ Throwable -> 0x01ff }
        r0 = (android.app.Activity) r0;	 Catch:{ Throwable -> 0x01ff }
        r11 = r0;	 Catch:{ Throwable -> 0x01ff }
        r0 = r11.getResources();	 Catch:{ Throwable -> 0x01ff }
        r12 = r0;
        r0 = android.content.res.Resources.class;	 Catch:{ Throwable -> 0x0077 }
        r0 = r0.getDeclaredField(r9);	 Catch:{ Throwable -> 0x0077 }
        r0.setAccessible(r5);	 Catch:{ Throwable -> 0x0077 }
        r0.set(r12, r3);	 Catch:{ Throwable -> 0x0077 }
        goto L_0x0093;
    L_0x0077:
        r0 = move-exception;
        r13 = android.content.res.Resources.class;	 Catch:{ Throwable -> 0x01ff }
        r13 = r13.getDeclaredField(r7);	 Catch:{ Throwable -> 0x01ff }
        r13.setAccessible(r5);	 Catch:{ Throwable -> 0x01ff }
        r14 = r13.get(r12);	 Catch:{ Throwable -> 0x01ff }
        r15 = r14.getClass();	 Catch:{ Throwable -> 0x01ff }
        r15 = r15.getDeclaredField(r9);	 Catch:{ Throwable -> 0x01ff }
        r15.setAccessible(r5);	 Catch:{ Throwable -> 0x01ff }
        r15.set(r14, r3);	 Catch:{ Throwable -> 0x01ff }
    L_0x0093:
        r0 = r11.getTheme();	 Catch:{ Throwable -> 0x01ff }
        r13 = r0;
        r0 = android.content.res.Resources.Theme.class;	 Catch:{ NoSuchFieldException -> 0x00a7 }
        r0 = r0.getDeclaredField(r9);	 Catch:{ NoSuchFieldException -> 0x00a7 }
        r0.setAccessible(r5);	 Catch:{ NoSuchFieldException -> 0x00a7 }
        r0.set(r13, r3);	 Catch:{ NoSuchFieldException -> 0x00a7 }
        goto L_0x00c5;
    L_0x00a5:
        r0 = move-exception;
        goto L_0x010d;
    L_0x00a7:
        r0 = move-exception;
        r14 = android.content.res.Resources.Theme.class;	 Catch:{ Throwable -> 0x00a5 }
        r15 = "mThemeImpl";	 Catch:{ Throwable -> 0x00a5 }
        r14 = r14.getDeclaredField(r15);	 Catch:{ Throwable -> 0x00a5 }
        r14.setAccessible(r5);	 Catch:{ Throwable -> 0x00a5 }
        r15 = r14.get(r13);	 Catch:{ Throwable -> 0x00a5 }
        r2 = r15.getClass();	 Catch:{ Throwable -> 0x00a5 }
        r2 = r2.getDeclaredField(r9);	 Catch:{ Throwable -> 0x00a5 }
        r2.setAccessible(r5);	 Catch:{ Throwable -> 0x00a5 }
        r2.set(r15, r3);	 Catch:{ Throwable -> 0x00a5 }
    L_0x00c5:
        r0 = android.view.ContextThemeWrapper.class;	 Catch:{ Throwable -> 0x00a5 }
        r0 = r0.getDeclaredField(r1);	 Catch:{ Throwable -> 0x00a5 }
        r0.setAccessible(r5);	 Catch:{ Throwable -> 0x00a5 }
        r0.set(r11, r8);	 Catch:{ Throwable -> 0x00a5 }
        r2 = android.view.ContextThemeWrapper.class;	 Catch:{ Throwable -> 0x00a5 }
        r14 = "initializeTheme";	 Catch:{ Throwable -> 0x00a5 }
        r15 = 0;	 Catch:{ Throwable -> 0x00a5 }
        r8 = new java.lang.Class[r15];	 Catch:{ Throwable -> 0x00a5 }
        r2 = r2.getDeclaredMethod(r14, r8);	 Catch:{ Throwable -> 0x00a5 }
        r2.setAccessible(r5);	 Catch:{ Throwable -> 0x00a5 }
        r8 = new java.lang.Object[r15];	 Catch:{ Throwable -> 0x00a5 }
        r2.invoke(r11, r8);	 Catch:{ Throwable -> 0x00a5 }
        r8 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x00a5 }
        r14 = 24;	 Catch:{ Throwable -> 0x00a5 }
        if (r8 >= r14) goto L_0x010c;	 Catch:{ Throwable -> 0x00a5 }
    L_0x00ea:
        r8 = android.content.res.AssetManager.class;	 Catch:{ Throwable -> 0x00a5 }
        r14 = "createTheme";	 Catch:{ Throwable -> 0x00a5 }
        r15 = 0;	 Catch:{ Throwable -> 0x00a5 }
        r5 = new java.lang.Class[r15];	 Catch:{ Throwable -> 0x00a5 }
        r5 = r8.getDeclaredMethod(r14, r5);	 Catch:{ Throwable -> 0x00a5 }
        r8 = 1;	 Catch:{ Throwable -> 0x00a5 }
        r5.setAccessible(r8);	 Catch:{ Throwable -> 0x00a5 }
        r8 = new java.lang.Object[r15];	 Catch:{ Throwable -> 0x00a5 }
        r8 = r5.invoke(r3, r8);	 Catch:{ Throwable -> 0x00a5 }
        r14 = android.content.res.Resources.Theme.class;	 Catch:{ Throwable -> 0x00a5 }
        r14 = r14.getDeclaredField(r1);	 Catch:{ Throwable -> 0x00a5 }
        r15 = 1;	 Catch:{ Throwable -> 0x00a5 }
        r14.setAccessible(r15);	 Catch:{ Throwable -> 0x00a5 }
        r14.set(r13, r8);	 Catch:{ Throwable -> 0x00a5 }
    L_0x010c:
        goto L_0x0123;
    L_0x010d:
        r2 = "InstantRun";	 Catch:{ Throwable -> 0x01ff }
        r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x01ff }
        r5.<init>();	 Catch:{ Throwable -> 0x01ff }
        r8 = "Failed to update existing theme for activity ";	 Catch:{ Throwable -> 0x01ff }
        r5.append(r8);	 Catch:{ Throwable -> 0x01ff }
        r5.append(r11);	 Catch:{ Throwable -> 0x01ff }
        r5 = r5.toString();	 Catch:{ Throwable -> 0x01ff }
        android.util.Log.e(r2, r5, r0);	 Catch:{ Throwable -> 0x01ff }
    L_0x0123:
        pruneResourceCaches(r12);	 Catch:{ Throwable -> 0x01ff }
        r2 = 0;	 Catch:{ Throwable -> 0x01ff }
        r5 = 1;	 Catch:{ Throwable -> 0x01ff }
        r8 = 0;	 Catch:{ Throwable -> 0x01ff }
        goto L_0x0058;	 Catch:{ Throwable -> 0x01ff }
    L_0x012b:
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x01ff }
        r1 = 19;
        r2 = "mActiveResources";
        if (r0 < r1) goto L_0x017b;
    L_0x0133:
        r0 = "android.app.ResourcesManager";	 Catch:{ Throwable -> 0x01ff }
        r0 = java.lang.Class.forName(r0);	 Catch:{ Throwable -> 0x01ff }
        r1 = r0;	 Catch:{ Throwable -> 0x01ff }
        r0 = "getInstance";	 Catch:{ Throwable -> 0x01ff }
        r5 = 0;	 Catch:{ Throwable -> 0x01ff }
        r8 = new java.lang.Class[r5];	 Catch:{ Throwable -> 0x01ff }
        r0 = r1.getDeclaredMethod(r0, r8);	 Catch:{ Throwable -> 0x01ff }
        r8 = r0;	 Catch:{ Throwable -> 0x01ff }
        r10 = 1;	 Catch:{ Throwable -> 0x01ff }
        r8.setAccessible(r10);	 Catch:{ Throwable -> 0x01ff }
        r0 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x01ff }
        r5 = 0;	 Catch:{ Throwable -> 0x01ff }
        r0 = r8.invoke(r5, r0);	 Catch:{ Throwable -> 0x01ff }
        r5 = r0;
        r0 = r1.getDeclaredField(r2);	 Catch:{ NoSuchFieldException -> 0x0165 }
        r2 = 1;	 Catch:{ NoSuchFieldException -> 0x0165 }
        r0.setAccessible(r2);	 Catch:{ NoSuchFieldException -> 0x0165 }
        r2 = r0.get(r5);	 Catch:{ NoSuchFieldException -> 0x0165 }
        r2 = (android.util.ArrayMap) r2;	 Catch:{ NoSuchFieldException -> 0x0165 }
        r10 = r2.values();	 Catch:{ NoSuchFieldException -> 0x0165 }
        r0 = r10;
        goto L_0x0177;
    L_0x0165:
        r0 = move-exception;
        r2 = "mResourceReferences";	 Catch:{ Throwable -> 0x01ff }
        r2 = r1.getDeclaredField(r2);	 Catch:{ Throwable -> 0x01ff }
        r10 = 1;	 Catch:{ Throwable -> 0x01ff }
        r2.setAccessible(r10);	 Catch:{ Throwable -> 0x01ff }
        r10 = r2.get(r5);	 Catch:{ Throwable -> 0x01ff }
        r10 = (java.util.Collection) r10;	 Catch:{ Throwable -> 0x01ff }
        r0 = r10;	 Catch:{ Throwable -> 0x01ff }
    L_0x0177:
        r2 = r16;	 Catch:{ Throwable -> 0x01ff }
        r1 = r0;	 Catch:{ Throwable -> 0x01ff }
        goto L_0x019c;	 Catch:{ Throwable -> 0x01ff }
    L_0x017b:
        r0 = "android.app.ActivityThread";	 Catch:{ Throwable -> 0x01ff }
        r0 = java.lang.Class.forName(r0);	 Catch:{ Throwable -> 0x01ff }
        r1 = r0.getDeclaredField(r2);	 Catch:{ Throwable -> 0x01ff }
        r2 = 1;	 Catch:{ Throwable -> 0x01ff }
        r1.setAccessible(r2);	 Catch:{ Throwable -> 0x01ff }
        r2 = r16;
        r5 = getActivityThread(r2, r0);	 Catch:{ Throwable -> 0x01fd }
        r8 = r1.get(r5);	 Catch:{ Throwable -> 0x01fd }
        r8 = (java.util.HashMap) r8;	 Catch:{ Throwable -> 0x01fd }
        r10 = r8.values();	 Catch:{ Throwable -> 0x01fd }
        r0 = r10;	 Catch:{ Throwable -> 0x01fd }
        r1 = r0;	 Catch:{ Throwable -> 0x01fd }
    L_0x019c:
        r5 = r1.iterator();	 Catch:{ Throwable -> 0x01fd }
    L_0x01a0:
        r0 = r5.hasNext();	 Catch:{ Throwable -> 0x01fd }
        if (r0 == 0) goto L_0x01f1;	 Catch:{ Throwable -> 0x01fd }
    L_0x01a6:
        r0 = r5.next();	 Catch:{ Throwable -> 0x01fd }
        r0 = (java.lang.ref.WeakReference) r0;	 Catch:{ Throwable -> 0x01fd }
        r8 = r0;	 Catch:{ Throwable -> 0x01fd }
        r0 = r8.get();	 Catch:{ Throwable -> 0x01fd }
        r0 = (android.content.res.Resources) r0;	 Catch:{ Throwable -> 0x01fd }
        r10 = r0;
        if (r10 == 0) goto L_0x01ef;
    L_0x01b6:
        r0 = android.content.res.Resources.class;	 Catch:{ Throwable -> 0x01c5 }
        r0 = r0.getDeclaredField(r9);	 Catch:{ Throwable -> 0x01c5 }
        r11 = 1;	 Catch:{ Throwable -> 0x01c5 }
        r0.setAccessible(r11);	 Catch:{ Throwable -> 0x01c5 }
        r0.set(r10, r3);	 Catch:{ Throwable -> 0x01c5 }
        r14 = 1;
        goto L_0x01e3;
    L_0x01c5:
        r0 = move-exception;
        r11 = android.content.res.Resources.class;	 Catch:{ Throwable -> 0x01fd }
        r11 = r11.getDeclaredField(r7);	 Catch:{ Throwable -> 0x01fd }
        r12 = 1;	 Catch:{ Throwable -> 0x01fd }
        r11.setAccessible(r12);	 Catch:{ Throwable -> 0x01fd }
        r12 = r11.get(r10);	 Catch:{ Throwable -> 0x01fd }
        r13 = r12.getClass();	 Catch:{ Throwable -> 0x01fd }
        r13 = r13.getDeclaredField(r9);	 Catch:{ Throwable -> 0x01fd }
        r14 = 1;	 Catch:{ Throwable -> 0x01fd }
        r13.setAccessible(r14);	 Catch:{ Throwable -> 0x01fd }
        r13.set(r12, r3);	 Catch:{ Throwable -> 0x01fd }
    L_0x01e3:
        r0 = r10.getConfiguration();	 Catch:{ Throwable -> 0x01fd }
        r11 = r10.getDisplayMetrics();	 Catch:{ Throwable -> 0x01fd }
        r10.updateConfiguration(r0, r11);	 Catch:{ Throwable -> 0x01fd }
        goto L_0x01f0;	 Catch:{ Throwable -> 0x01fd }
    L_0x01ef:
        r14 = 1;	 Catch:{ Throwable -> 0x01fd }
    L_0x01f0:
        goto L_0x01a0;	 Catch:{ Throwable -> 0x01fd }
        return;	 Catch:{ Throwable -> 0x01fd }
    L_0x01f3:
        r2 = r16;	 Catch:{ Throwable -> 0x01fd }
        r0 = new java.lang.IllegalStateException;	 Catch:{ Throwable -> 0x01fd }
        r1 = "Could not create new AssetManager";	 Catch:{ Throwable -> 0x01fd }
        r0.<init>(r1);	 Catch:{ Throwable -> 0x01fd }
        throw r0;	 Catch:{ Throwable -> 0x01fd }
    L_0x01fd:
        r0 = move-exception;
        goto L_0x0202;
    L_0x01ff:
        r0 = move-exception;
        r2 = r16;
    L_0x0202:
        r1 = new java.lang.IllegalStateException;
        r1.<init>(r0);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tools.ir.server.MonkeyPatcher.monkeyPatchExistingResources(android.content.Context, java.lang.String, java.util.Collection):void");
    }

    private static boolean pruneResourceCache(Object resources, String fieldName) {
        Field cacheField;
        try {
            cacheField = resources.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            cacheField = Resources.class.getDeclaredField(fieldName);
        } catch (Throwable th) {
        }
        cacheField.setAccessible(true);
        Object cache = cacheField.get(resources);
        Class<?> type = cacheField.getType();
        Method configChangeMethod;
        if (VERSION.SDK_INT < 16) {
            if (cache instanceof SparseArray) {
                ((SparseArray) cache).clear();
                return true;
            } else if (VERSION.SDK_INT >= 14 && (cache instanceof LongSparseArray)) {
                ((LongSparseArray) cache).clear();
                return true;
            }
        } else if (VERSION.SDK_INT >= 23) {
            while (type != null) {
                try {
                    configChangeMethod = type.getDeclaredMethod("onConfigurationChange", new Class[]{Integer.TYPE});
                    configChangeMethod.setAccessible(true);
                    configChangeMethod.invoke(cache, new Object[]{Integer.valueOf(-1)});
                    return true;
                } catch (Throwable th2) {
                    type = type.getSuperclass();
                }
            }
        } else if (!"mColorStateListCache".equals(fieldName)) {
            String str = "clearDrawableCachesLocked";
            if (type.isAssignableFrom(ArrayMap.class)) {
                configChangeMethod = Resources.class.getDeclaredMethod(str, new Class[]{ArrayMap.class, Integer.TYPE});
                configChangeMethod.setAccessible(true);
                configChangeMethod.invoke(resources, new Object[]{cache, Integer.valueOf(-1)});
                return true;
            } else if (type.isAssignableFrom(LongSparseArray.class)) {
                try {
                    configChangeMethod = Resources.class.getDeclaredMethod(str, new Class[]{LongSparseArray.class, Integer.TYPE});
                    configChangeMethod.setAccessible(true);
                    configChangeMethod.invoke(resources, new Object[]{cache, Integer.valueOf(-1)});
                    return true;
                } catch (NoSuchMethodException e2) {
                    if (cache instanceof LongSparseArray) {
                        ((LongSparseArray) cache).clear();
                        return true;
                    }
                }
            } else if (type.isArray() && type.getComponentType().isAssignableFrom(LongSparseArray.class)) {
                for (LongSparseArray array : (LongSparseArray[]) cache) {
                    if (array != null) {
                        array.clear();
                    }
                }
                return true;
            }
        } else if (cache instanceof LongSparseArray) {
            ((LongSparseArray) cache).clear();
        }
        return false;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: f
        g.d.a.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:42:0x00b5 in {6, 7, 8, 13, 14, 19, 20, 22, 25, 26, 28, 33, 36, 38, 41} preds:[]
        	at e.b.d.g.a.f.d(SourceFile:242)
        	at e.b.d.g.a.f.c(SourceFile:52)
        	at e.b.d.g.a.f.a(SourceFile:42)
        	at e.b.d.g.i.a(SourceFile:28)
        	at e.b.d.g.i.b(SourceFile:15)
        	at e.b.d.g.k.accept(Unknown Source:4)
        	at d.b.a.c.l.f.a(SourceFile:85)
        	at e.b.d.g.i.a(SourceFile:15)
        	at e.b.b.a(SourceFile:33)
        	at e.a.d.a(SourceFile:402)
        	at e.a.f.a(SourceFile:62)
        	at e.a.d.a(SourceFile:296)
        	at e.a.e.run(Unknown Source:10)
        */
    private static void pruneResourceCaches(java.lang.Object r7) {
        /*
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 1;
        r2 = 21;
        if (r0 < r2) goto L_0x0032;
    L_0x0007:
        r0 = android.content.res.Resources.class;	 Catch:{ Throwable -> 0x0031 }
        r2 = "mTypedArrayPool";	 Catch:{ Throwable -> 0x0031 }
        r0 = r0.getDeclaredField(r2);	 Catch:{ Throwable -> 0x0031 }
        r0.setAccessible(r1);	 Catch:{ Throwable -> 0x0031 }
        r2 = r0.get(r7);	 Catch:{ Throwable -> 0x0031 }
        r3 = r2.getClass();	 Catch:{ Throwable -> 0x0031 }
        r4 = "acquire";	 Catch:{ Throwable -> 0x0031 }
        r5 = 0;	 Catch:{ Throwable -> 0x0031 }
        r6 = new java.lang.Class[r5];	 Catch:{ Throwable -> 0x0031 }
        r4 = r3.getDeclaredMethod(r4, r6);	 Catch:{ Throwable -> 0x0031 }
        r4.setAccessible(r1);	 Catch:{ Throwable -> 0x0031 }
    L_0x0026:
        r6 = new java.lang.Object[r5];	 Catch:{ Throwable -> 0x0031 }
        r6 = r4.invoke(r2, r6);	 Catch:{ Throwable -> 0x0031 }
        if (r6 != 0) goto L_0x0030;
        goto L_0x0032;
    L_0x0030:
        goto L_0x0026;
    L_0x0031:
        r0 = move-exception;
    L_0x0032:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 23;
        if (r0 < r2) goto L_0x004a;
    L_0x0038:
        r0 = android.content.res.Resources.class;	 Catch:{ Throwable -> 0x0049 }
        r3 = "mResourcesImpl";	 Catch:{ Throwable -> 0x0049 }
        r0 = r0.getDeclaredField(r3);	 Catch:{ Throwable -> 0x0049 }
        r0.setAccessible(r1);	 Catch:{ Throwable -> 0x0049 }
        r3 = r0.get(r7);	 Catch:{ Throwable -> 0x0049 }
        r7 = r3;
        goto L_0x004a;
    L_0x0049:
        r0 = move-exception;
    L_0x004a:
        r0 = 0;
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 18;
        if (r3 < r4) goto L_0x0066;
    L_0x0051:
        r3 = r7.getClass();	 Catch:{ Throwable -> 0x0064 }
        r4 = "mAccessLock";	 Catch:{ Throwable -> 0x0064 }
        r3 = r3.getDeclaredField(r4);	 Catch:{ Throwable -> 0x0064 }
        r3.setAccessible(r1);	 Catch:{ Throwable -> 0x0064 }
        r1 = r3.get(r7);	 Catch:{ Throwable -> 0x0064 }
        r0 = r1;
    L_0x0063:
        goto L_0x0078;
    L_0x0064:
        r1 = move-exception;
        goto L_0x0063;
    L_0x0066:
        r3 = android.content.res.Resources.class;	 Catch:{ Throwable -> 0x0077 }
        r4 = "mTmpValue";	 Catch:{ Throwable -> 0x0077 }
        r3 = r3.getDeclaredField(r4);	 Catch:{ Throwable -> 0x0077 }
        r3.setAccessible(r1);	 Catch:{ Throwable -> 0x0077 }
        r1 = r3.get(r7);	 Catch:{ Throwable -> 0x0077 }
        r0 = r1;
        goto L_0x0078;
    L_0x0077:
        r1 = move-exception;
    L_0x0078:
        if (r0 != 0) goto L_0x007c;
    L_0x007a:
        r0 = com.android.tools.ir.server.MonkeyPatcher.class;
    L_0x007c:
        monitor-enter(r0);
        r1 = "mDrawableCache";	 Catch:{ all -> 0x00b2 }
        pruneResourceCache(r7, r1);	 Catch:{ all -> 0x00b2 }
        r1 = "mColorDrawableCache";	 Catch:{ all -> 0x00b2 }
        pruneResourceCache(r7, r1);	 Catch:{ all -> 0x00b2 }
        r1 = "mColorStateListCache";	 Catch:{ all -> 0x00b2 }
        pruneResourceCache(r7, r1);	 Catch:{ all -> 0x00b2 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x00b2 }
        if (r1 < r2) goto L_0x009b;	 Catch:{ all -> 0x00b2 }
    L_0x0090:
        r1 = "mAnimatorCache";	 Catch:{ all -> 0x00b2 }
        pruneResourceCache(r7, r1);	 Catch:{ all -> 0x00b2 }
        r1 = "mStateListAnimatorCache";	 Catch:{ all -> 0x00b2 }
        pruneResourceCache(r7, r1);	 Catch:{ all -> 0x00b2 }
        goto L_0x00b0;	 Catch:{ all -> 0x00b2 }
    L_0x009b:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x00b2 }
        r2 = 19;	 Catch:{ all -> 0x00b2 }
        if (r1 != r2) goto L_0x00b0;	 Catch:{ all -> 0x00b2 }
    L_0x00a1:
        r1 = "sPreloadedDrawables";	 Catch:{ all -> 0x00b2 }
        pruneResourceCache(r7, r1);	 Catch:{ all -> 0x00b2 }
        r1 = "sPreloadedColorDrawables";	 Catch:{ all -> 0x00b2 }
        pruneResourceCache(r7, r1);	 Catch:{ all -> 0x00b2 }
        r1 = "sPreloadedColorStateLists";	 Catch:{ all -> 0x00b2 }
        pruneResourceCache(r7, r1);	 Catch:{ all -> 0x00b2 }
    L_0x00b0:
        monitor-exit(r0);	 Catch:{ all -> 0x00b2 }
        return;	 Catch:{ all -> 0x00b2 }
    L_0x00b2:
        r1 = move-exception;	 Catch:{ all -> 0x00b2 }
        monitor-exit(r0);	 Catch:{ all -> 0x00b2 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.tools.ir.server.MonkeyPatcher.pruneResourceCaches(java.lang.Object):void");
    }
}
