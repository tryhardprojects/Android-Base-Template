package dean.tryhard.project.baseproject.ui.base;

import android.view.View;

import androidx.annotation.Nullable;

public interface BaseView {
    void showMessage(String title, String message, boolean cancelable, @Nullable View.OnClickListener onPositiveListener);

    void showProgress(boolean show, boolean cancelable);
    void showProgress(String text, boolean show, boolean cancelable);

    BaseActivity getBaseActivity();
}
