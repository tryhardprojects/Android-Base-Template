package dean.tryhard.project.baseproject.api;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class BaseFlowableSubscriber<T> implements Subscriber<T> {
    public interface Callback<T>{
        void onNext(T t);
    }
    private Callback<T> callback;

    public BaseFlowableSubscriber(Callback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T t) {
        callback.onNext(t);
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
