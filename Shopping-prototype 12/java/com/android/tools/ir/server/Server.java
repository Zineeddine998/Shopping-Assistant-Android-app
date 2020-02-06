package com.android.tools.ir.server;

import android.app.Activity;
import android.content.Context;
import android.net.LocalServerSocket;
import android.util.Log;
import com.android.tools.ir.runtime.ApplicationPatch;
import com.android.tools.ir.runtime.PatchesLoader;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Server {
    private static final boolean POST_ALIVE_STATUS = false;
    private static final boolean RESTART_LOCALLY = false;
    private static int wrongTokenCount;
    private final Context context;
    private LocalServerSocket serverSocket;

    private Server(String packageName, Context context) {
        String str = Logging.LOG_TAG;
        this.context = context;
        StringBuilder stringBuilder;
        try {
            this.serverSocket = new LocalServerSocket(packageName);
            if (Log.isLoggable(str, 2)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Starting server socket listening for package ");
                stringBuilder.append(packageName);
                stringBuilder.append(" on ");
                stringBuilder.append(this.serverSocket.getLocalSocketAddress());
                Log.v(str, stringBuilder.toString());
            }
            startServer();
            if (Log.isLoggable(str, 2)) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Started server for package ");
                stringBuilder2.append(packageName);
                Log.v(str, stringBuilder2.toString());
            }
        } catch (IOException e) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("IO Error creating local socket at ");
            stringBuilder.append(packageName);
            Log.e(str, stringBuilder.toString(), e);
        }
    }

    static /* synthetic */ int access$208() {
        int i = wrongTokenCount;
        wrongTokenCount = i + 1;
        return i;
    }

    public static Server create(Context context) {
        return new Server(context.getPackageName(), context);
    }

    private int handleHotSwapPatch(int updateMode, ApplicationPatch patch) {
        String str = "Couldn't apply code changes";
        String str2 = Logging.LOG_TAG;
        if (Log.isLoggable(str2, 2)) {
            Log.v(str2, "Received incremental code patch");
        }
        try {
            String dexFile = FileManager.writeTempDexFile(patch.getBytes());
            if (dexFile == null) {
                Log.e(str2, "No file to write the code to");
                return updateMode;
            }
            if (Log.isLoggable(str2, 2)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Reading live code from ");
                stringBuilder.append(dexFile);
                Log.v(str2, stringBuilder.toString());
            }
            Class<?> aClass = Class.forName("com.android.tools.ir.runtime.AppPatchesLoaderImpl", true, new DexClassLoader(dexFile, this.context.getCacheDir().getPath(), FileManager.getNativeLibraryFolder().getPath(), getClass().getClassLoader()));
            if (Log.isLoggable(str2, 2)) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Got the patcher class ");
                stringBuilder2.append(aClass);
                Log.v(str2, stringBuilder2.toString());
            }
            PatchesLoader loader = (PatchesLoader) aClass.newInstance();
            if (Log.isLoggable(str2, 2)) {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("Got the patcher instance ");
                stringBuilder3.append(loader);
                Log.v(str2, stringBuilder3.toString());
            }
            int i = 0;
            String[] getPatchedClasses = (String[]) aClass.getDeclaredMethod("getPatchedClasses", new Class[0]).invoke(loader, new Object[0]);
            if (Log.isLoggable(str2, 2)) {
                Log.v(str2, "Got the list of classes ");
                int length = getPatchedClasses.length;
                while (i < length) {
                    String getPatchedClass = getPatchedClasses[i];
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append("class ");
                    stringBuilder4.append(getPatchedClass);
                    Log.v(str2, stringBuilder4.toString());
                    i++;
                }
            }
            if (!loader.load()) {
                updateMode = 3;
            }
            return updateMode;
        } catch (Exception e) {
            Log.e(str2, str, e);
            e.printStackTrace();
            updateMode = 3;
        } catch (Throwable e2) {
            Log.e(str2, str, e2);
            updateMode = 3;
        }
    }

    private int handlePatches(List<ApplicationPatch> changes, boolean hasResources, int updateMode) {
        if (hasResources) {
            FileManager.startUpdate();
        }
        for (ApplicationPatch change : changes) {
            String path = change.getPath();
            if (path.equals("classes.dex.3")) {
                updateMode = handleHotSwapPatch(updateMode, change);
            } else if (isResourcePath(path)) {
                updateMode = handleResourcePatch(updateMode, change, path);
            }
        }
        if (hasResources) {
            FileManager.finishUpdate(true);
        }
        return updateMode;
    }

    private static int handleResourcePatch(int updateMode, ApplicationPatch patch, String path) {
        String str = Logging.LOG_TAG;
        if (Log.isLoggable(str, 2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Received resource changes (");
            stringBuilder.append(path);
            stringBuilder.append(")");
            Log.v(str, stringBuilder.toString());
        }
        FileManager.writeAaptResources(path, patch.getBytes());
        return Math.max(updateMode, 2);
    }

    private static boolean hasResources(List<ApplicationPatch> changes) {
        for (ApplicationPatch change : changes) {
            if (isResourcePath(change.getPath())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isResourcePath(String path) {
        return path.equals("resources.ap_") || path.startsWith("res/");
    }

    private void restart(int updateMode, boolean incrementalResources, boolean toast) {
        String str = Logging.LOG_TAG;
        if (Log.isLoggable(str, 2)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Finished loading changes; update mode =");
            stringBuilder.append(updateMode);
            Log.v(str, stringBuilder.toString());
        }
        if (updateMode == 0 || updateMode == 1) {
            if (Log.isLoggable(str, 2)) {
                Log.v(str, "Applying incremental code without restart");
            }
            if (toast) {
                Activity foreground = Restarter.getForegroundActivity(this.context);
                if (foreground != null) {
                    Restarter.showToast(foreground, "Applied code changes without activity restart");
                } else if (Log.isLoggable(str, 2)) {
                    Log.v(str, "Couldn't show toast: no activity found");
                }
            }
            return;
        }
        StringBuilder stringBuilder2;
        List<Activity> activities = Restarter.getActivities(this.context, false);
        if (incrementalResources && updateMode == 2) {
            File file = FileManager.getExternalResourceFile();
            if (Log.isLoggable(str, 2)) {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("About to update resource file=");
                stringBuilder3.append(file);
                stringBuilder3.append(", activities=");
                stringBuilder3.append(activities);
                Log.v(str, stringBuilder3.toString());
            }
            if (file != null) {
                MonkeyPatcher.monkeyPatchExistingResources(this.context, file.getPath(), activities);
            } else {
                Log.e(str, "No resource file found to apply");
                updateMode = 3;
            }
        }
        Activity activity = Restarter.getForegroundActivity(this.context);
        if (updateMode == 2) {
            if (activity != null) {
                if (Log.isLoggable(str, 2)) {
                    Log.v(str, "Restarting activity only!");
                }
                boolean handledRestart = false;
                try {
                    Object result = activity.getClass().getMethod("onHandleCodeChange", new Class[]{Long.TYPE}).invoke(activity, new Object[]{Long.valueOf(0)});
                    if (Log.isLoggable(str, 2)) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Activity ");
                        stringBuilder2.append(activity);
                        stringBuilder2.append(" provided manual restart method; return ");
                        stringBuilder2.append(result);
                        Log.v(str, stringBuilder2.toString());
                    }
                    if (Boolean.TRUE.equals(result)) {
                        handledRestart = true;
                        if (toast) {
                            Restarter.showToast(activity, "Applied changes");
                        }
                    }
                } catch (Throwable th) {
                }
                if (!handledRestart) {
                    if (toast) {
                        Restarter.showToast(activity, "Applied changes, restarted activity");
                    }
                    Restarter.restartActivityOnUiThread(activity);
                }
                return;
            }
            if (Log.isLoggable(str, 2)) {
                Log.v(str, "No activity found, falling through to do a full app restart");
            }
            updateMode = 3;
        }
        if (updateMode != 3) {
            if (Log.isLoggable(str, 6)) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Unexpected update mode: ");
                stringBuilder2.append(updateMode);
                Log.e(str, stringBuilder2.toString());
            }
            return;
        }
        if (Log.isLoggable(str, 2)) {
            Log.v(str, "Waiting for app to be killed and restarted by the IDE...");
        }
    }

    private void startServer() {
        try {
            new Thread(new SocketServerThread(this, null)).start();
        } catch (Throwable e) {
            String str = Logging.LOG_TAG;
            if (Log.isLoggable(str, 6)) {
                Log.e(str, "Fatal error starting Instant Run server", e);
            }
        }
    }

    public void shutdown() {
        LocalServerSocket localServerSocket = this.serverSocket;
        if (localServerSocket != null) {
            try {
                localServerSocket.close();
            } catch (IOException e) {
            }
            this.serverSocket = null;
        }
    }
}
