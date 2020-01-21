package dean.tryhard.project.baseproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import dean.tryhard.project.baseproject.GlobalApplication;
import dean.tryhard.project.baseproject.SysConfig;

public class LocalSP {

    private static final Gson gson = new Gson();
    private static SharedPreferences getSP() {
        return GlobalApplication.getContext().getSharedPreferences(SysConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
}
