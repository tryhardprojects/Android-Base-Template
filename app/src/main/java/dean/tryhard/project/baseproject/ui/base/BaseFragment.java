package dean.tryhard.project.baseproject.ui.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dean.tryhard.project.baseproject.utils.BindEventBus;
import dean.tryhard.project.baseproject.utils.EventBusUtils;
import dean.tryhard.project.baseproject.views.LoadingView;

abstract public class BaseFragment extends Fragment implements BaseView {
    private LoadingView mProgress;

    /**
     * 空白建構子, 傳任何參數請用setArgument, 建構子不能動!
     */
    public BaseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgress = new LoadingView(getContext());

        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBusUtils.register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBusUtils.unregister(this);
        }
    }

    @Override
    public void showProgress(String text, boolean show, boolean cancelable) {
        try {
            if (show) {
                mProgress.setCancelable(cancelable);
                mProgress.show();
                mProgress.setText(text);
            } else {
                mProgress.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showProgress(boolean show, boolean cancelable) {
        try {
            if (show) {
                mProgress.setCancelable(cancelable);
                mProgress.show();
            } else {
                mProgress.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBusUtils.unregister(this);
        }
    }

    //BaseEvents
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(Integer eventId) {
        if (!this.getClass().isAnnotationPresent(BindEventBus.class)) {
            return;
        }
    }

    @Override
    public void showMessage(String title, String message, boolean cancelable, @Nullable View.OnClickListener onPositiveListener) {

    }

    @Override
    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
