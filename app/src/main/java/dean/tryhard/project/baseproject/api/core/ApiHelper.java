package dean.tryhard.project.baseproject.api.core;


import dean.tryhard.project.baseproject.BuildConfig;

public class ApiHelper {
    private static ApiHelper mInstance;
    private static final boolean showLog = BuildConfig.DEBUG; // 打包方式
    private static final boolean isDevelopement = BuildConfig.DEVELOPEMENT; // api環境


    private Api mApi;

    private ApiHelper() {
        mApi = new Api();
    }

    public Api getApi() {
        return mApi;
    }

    public static ApiHelper getInstance() {
        if (mInstance == null) {
            mInstance = new ApiHelper();
        }
        return mInstance;
    }
}
