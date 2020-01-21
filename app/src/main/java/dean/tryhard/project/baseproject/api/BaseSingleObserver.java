package dean.tryhard.project.baseproject.api;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class BaseSingleObserver<T> implements SingleObserver<T> {
    private static final String TAG = "BaseSingleObserver";
    public Callback<T> callback;
    private int reconnectTime = 0;

    public interface Callback<T> {
        void onResult(T t);

        void onOtherError(Throwable e);
    }

    public BaseSingleObserver(Callback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onSuccess(T t) {
//        if (t instanceof Response) {
//            Log.d(TAG, "code = " + ((Response) t).code() + ", message = " + ((Response) t).message());
//            callback.onResult(t);
////            if(((Response)t).body() != null){
////              callback.onResult(t);
////            }
//        }
    }

    @Override
    public void onError(Throwable e) {

    }
}
