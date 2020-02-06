package com.android.tools.ir.server;

import com.android.tools.ir.common.Log;
import com.android.tools.ir.common.Log$Logging;
import java.util.logging.Level;

public class Logging {
    public static final String LOG_TAG = "InstantRun";

    static {
        Log.logging = new Log$Logging() {
            public boolean isLoggable(Level level) {
                Level level2 = Level.SEVERE;
                String str = Logging.LOG_TAG;
                if (level == level2) {
                    return android.util.Log.isLoggable(str, 6);
                }
                if (level == Level.FINE) {
                    return android.util.Log.isLoggable(str, 2);
                }
                return android.util.Log.isLoggable(str, 4);
            }

            public void log(Level level, String string) {
                log(level, string, null);
            }

            public void log(Level level, String string, Throwable throwable) {
                Level level2 = Level.SEVERE;
                String str = Logging.LOG_TAG;
                if (level == level2) {
                    if (throwable == null) {
                        android.util.Log.e(str, string);
                    } else {
                        android.util.Log.e(str, string, throwable);
                    }
                } else if (level == Level.FINE) {
                    if (!android.util.Log.isLoggable(str, 2)) {
                        return;
                    }
                    if (throwable == null) {
                        android.util.Log.v(str, string);
                    } else {
                        android.util.Log.v(str, string, throwable);
                    }
                } else if (!android.util.Log.isLoggable(str, 4)) {
                } else {
                    if (throwable == null) {
                        android.util.Log.i(str, string);
                    } else {
                        android.util.Log.i(str, string, throwable);
                    }
                }
            }
        };
    }
}
