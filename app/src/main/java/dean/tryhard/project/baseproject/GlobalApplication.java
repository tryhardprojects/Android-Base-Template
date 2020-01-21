package dean.tryhard.project.baseproject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GlobalApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "GlobalApplication";
    private static Application application;
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        context = getApplicationContext();

        registerActivityLifecycleCallbacks(this);
    }

    public static Context getContext() {
        return context;
    }

    public static Application getApplication() {
        return application;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
