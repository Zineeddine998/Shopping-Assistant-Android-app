package com.android.tools.ir.common;

import java.util.logging.Level;

public interface Log$Logging {
    boolean isLoggable(Level level);

    void log(Level level, String str);

    void log(Level level, String str, Throwable th);
}
