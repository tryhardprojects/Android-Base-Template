package dean.tryhard.project.baseproject.api;

import java.io.IOException;

import dean.tryhard.project.baseproject.ui.base.BaseView;
import retrofit2.Response;

public class BeadApiHandler<T> {
    private BaseView baseView;
    private SuccessCallback<T> successCallback;
    private FailedCallback<T> failedCallback;

    public interface SuccessCallback<T> {
        void success(T t);
    }

    public interface FailedCallback<T> {
        void failed(String type, String key);
    }

    public BeadApiHandler() {

    }

    public void attach(BaseView baseView) {
        this.baseView = baseView;
    }

    public void detach() {
        baseView = null;
        successCallback = null;
        failedCallback = null;
    }

    public void process(Response<T> response) {
        // 先看是不是200 200 = 成功
        if (response.code() == 200) {
            if (successCallback != null) {
                successCallback.success(response.body());
            }
        } else {
            // 不是200 走失敗 先剔除非400的 也就是我們這邊統一做掉的 只有再code是400 會到外面去給Handler客製
            defaultError(response, response.code(), response.message());
        }
    }

    public void processOtherError(Throwable e) {
        // 收到我們沒有準備好的Error 也就是不屬於需要Retry的
        e.printStackTrace();
    }

    public void defaultError(Response<T> response, int code, String message) {
        if (baseView == null) return;

        switch (code) {
            default:
                try {
                    baseView.showMessage(code + "", message + "/" + response.errorBody().string(), true, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void showErrorMessage(Response<T> response, int code, String message) {

    }

    public void setSuccessCallback(SuccessCallback<T> successCallback) {
        this.successCallback = successCallback;
    }

    public void setFailedCallback(FailedCallback<T> failedCallback) {
        this.failedCallback = failedCallback;
    }

    public void goSuccess(T t) {
        if (successCallback != null) {
            successCallback.success(t);
        }
    }
}
