package jul.lab.library.log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 전역 및 레벨별로 enable/disable flag가 존재하는 log class.
 * 모든 로그를 호출 시점의 class 명과 thread number를 추가하여 출력한다.
 *
 * Created by JuL on 2014-07-12.
 */
public class Log {
    public static boolean LOG_VERBOSE = true;
    public static boolean LOG_INFO = true;
    public static boolean LOG_DEBUG = true;
    public static boolean LOG_WARNING = true;
    public static boolean LOG_ERROR = true;

    protected static String TAG = "JuL"; // should be replaced with your tag
    protected static boolean mEnabled = true;

    public static boolean isDebugMode() {
        return mEnabled;
    }

    public static void setDebugMode(boolean enable) {
        mEnabled = enable;
    }

    public static void setLogTag(String tag) {
        TAG = tag;
    }

    public static void v(String tag, String log) {
        if (mEnabled && LOG_VERBOSE) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.v(tag, realLog);
        }
    }

    public static void i(String tag, String log) {
        if (mEnabled && LOG_INFO) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.i(tag, realLog);
        }
    }

    public static void d(String tag, String log) {
        if (mEnabled && LOG_DEBUG) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.d(tag, realLog);
        }
    }

    public static void w(String tag, String log) {
        if (mEnabled && LOG_WARNING) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.w(tag, realLog);
        }
    }

    public static void e(String tag, String log) {
        if (mEnabled && LOG_ERROR) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.e(tag, realLog);
        }
    }

    public static void v(String log) {
        if (mEnabled && LOG_VERBOSE) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.v(TAG, realLog);
        }
    }

    public static void i(String log) {
        if (mEnabled && LOG_INFO) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.i(TAG, realLog);
        }
    }

    public static void d(String log) {
        if (mEnabled && LOG_DEBUG) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.d(TAG, realLog);
        }
    }

    public static void w(String log) {
        if (mEnabled && LOG_WARNING) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.w(TAG, realLog);
        }
    }

    public static void e(String log) {
        if (mEnabled && LOG_ERROR) {
            String realLog = getClassNameAndLineNumber(4) + buildVariableArguments(log);
            android.util.Log.e(TAG, realLog);
        }
    }

    public static void v(Object... args) {
        if (mEnabled && LOG_VERBOSE) {
            String realLog = getClassNameAndLineNumber(4)
                    + buildVariableArguments(args);
            android.util.Log.v(TAG, realLog);
        }
    }

    public static void i(Object... args) {
        if (mEnabled && LOG_INFO) {
            String realLog = getClassNameAndLineNumber(4)
                    + buildVariableArguments(args);
            android.util.Log.i(TAG, realLog);
        }
    }

    public static void d(Object... args) {
        if (mEnabled && LOG_DEBUG) {
            String realLog = getClassNameAndLineNumber(4)
                    + buildVariableArguments(args);
            android.util.Log.d(TAG, realLog);
        }
    }

    public static void w(Object... args) {
        if (mEnabled && LOG_WARNING) {
            String realLog = getClassNameAndLineNumber(4)
                    + buildVariableArguments(args);
            android.util.Log.w(TAG, realLog);
        }
    }

    public static void e(Object... args) {
        if (mEnabled && LOG_ERROR) {
            String realLog = getClassNameAndLineNumber(4)
                    + buildVariableArguments(args);
            android.util.Log.e(TAG, realLog);
        }
    }

    public static void e(Throwable e) {
        if (mEnabled && LOG_ERROR) {
            e(getStringFromThrowable(e));
        }
    }

    public static String getStringFromThrowable(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String buildVariableArguments(Object... agrs) {
        StringBuilder sb = new StringBuilder();
        sb.append(" [").append(Thread.currentThread().getId()).append("] ");
        for (Object item : agrs) {
            sb.append(item);
        }
        return sb.toString();
    }

    public static String buildVariableArguments(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(" [").append(Thread.currentThread().getId()).append("] ");
        sb.append(str);
        return sb.toString();
    }

    public static String getClassNameAndLineNumber(int depth) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        StringBuffer tempBuf = new StringBuffer();

        String[] temp = ste[depth].getClassName().split("\\.");
        tempBuf.append("[" + temp[temp.length - 1]);
        tempBuf.append(":" + ste[depth].getLineNumber() + "] ");

        return tempBuf.toString();
    }
}
