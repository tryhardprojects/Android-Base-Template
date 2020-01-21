package dean.tryhard.project.baseproject.utils;

import android.util.Log;

import dean.tryhard.project.baseproject.SysConfig;

public class LOG {
    public static void d(String text) {
        Log.d(SysConfig.DEBUG_LOG, text);
    }
}
