package dean.tryhard.project.baseproject.ui.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import dean.tryhard.project.baseproject.R;
import dean.tryhard.project.baseproject.utils.BindEventBus;
import dean.tryhard.project.baseproject.utils.EventBusUtils;
import dean.tryhard.project.baseproject.utils.PermissionManager;
import dean.tryhard.project.baseproject.views.LoadingView;
import dean.tryhard.project.baseproject.views.Title;

abstract public class BaseActivity extends AppCompatActivity implements BaseView {
    private LoadingView mProgress;
    protected Title actionbar;
    protected ViewGroup vg;

    /**
     * 請用這個來呼叫權限, 用Activity當參數的那隻方法.
     */
    public PermissionManager permissionManager = new PermissionManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        transparentStatusBar();
        actionbar = findViewById(R.id.base_actionbar);
//        設置statusbar的距離
//        actionbar.setPadding(actionbar.getPaddingLeft(), Utils.getStausBarHeight(), actionbar.getPaddingRight(), actionbar.getPaddingBottom());
        vg = findViewById(R.id.base_content);
//        vg.setPadding(vg.getPaddingLeft(), vg.getPaddingTop(), vg.getPaddingRight(), Utils.getNavBarHeight());

        mProgress = new LoadingView(this);

        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBusUtils.register(this);
        }

        vg.setKeepScreenOn(true);

//        AndroidBug5497Workaround.assistActivity(this);


        // 設定contentdata的top margin (負的, 為了和fitsSystemWindows造成的margin抵銷)
        // 之所以要用fitsSystemWindows 是為了讓鍵盤在全螢幕之下可以正常使用resize等.
        int topMargin = (-1) * (Title.getTopMargin(this));
        RelativeLayout.LayoutParams contentLayoutParams =
                (RelativeLayout.LayoutParams) vg.getLayoutParams();
        contentLayoutParams.topMargin = topMargin;
        vg.setLayoutParams(contentLayoutParams);
    }

    /**
     * 5.0以上的手機可以透明化狀態列, 6.0以上的手機可以讓status bar icon變成深色.
     */
    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            // Followed by google doc.
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));

            // http://corrupt003-android.blogspot.tw/2016/03/status-bar.html
            // 只有在21以上的手機才有辦法做到完全的透明
            // For not opaque(transparent) color.
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

            // https://stackoverflow.com/questions/30075827/android-statusbar-icons-color
            // 只有在23以上的手機才能讓status bar icon變成深色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                visibility = visibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }

            w.getDecorView().setSystemUiVisibility(visibility);

//            w.getDecorView().setBackground(ContextCompat.getDrawable(this, R.drawable.white));
        }
    }


    @Override
    public void showProgress(String text, boolean show, boolean cancelable) {
        try {
            if (show) {
                mProgress.setCancelable(cancelable);
                mProgress.setText(text);
                mProgress.show();
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

    /**
     * 子類別請在super.onCreate()呼叫完才呼叫.
     * 提供了Title, 及layout的設定. (android:fitsSystemWindows="true")
     *
     * @param layoutRes 子類別的畫面
     */
    protected void setContentViewInParent(@LayoutRes int layoutRes) {
        ViewGroup.inflate(this, layoutRes, vg);
    }

    /**
     * 一般而言, 子類別盡量用 setContentViewInParent, 但是真的不行的話, 也可以繼續呼叫這個函式.
     */
    @Override
    public void setContentView(@LayoutRes int layoutRes) {
        ViewGroup.inflate(this, layoutRes, vg);
        actionbar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    /**
     * @param permissions  想取得的權限
     * @param grantResults 取得權限的結果(成功/失敗)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionManager.PERMISSION_REQUEST_CODE) {
            final List<String> grantedPermissions = new ArrayList<>();
            final List<String> deniedPermissions = new ArrayList<>();
            final List<String> deniedAndNeverShowPermissions = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    //非第一次詢問 下次開始需要增加說明與不再問我的check box
                    deniedPermissions.add(permissions[i]);
                } else {
                    //取得權限成功
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grantedPermissions.add(permissions[i]);
                    } else {
                        deniedAndNeverShowPermissions.add(permissions[i]);
//                        dialog = new CommDialog(this)
//                                .setCancelable(false)
//                                .setTitle("權限被拒絕")
//                                .setMessage(permissionManager.explain)
//                                .setPositiveText("設定")
//                                .setOnPositiveListener(new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                        intent.setData(uri);
//                                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//                                    }
//                                }).setNegativeText("取消")
//                                .setOnNegativeListener(new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        onUserDoNotWantGoToSettings();
//                                    }
//                                });
                    }
                }

            }

            //若有接受或是拒絕的權限才需回呼
            // 在MobileSDK 中似乎有座fragment的更換 但是onRequestPermissionsResult中如果做fragment的更換會有 java.lang.IllegalStateException 的問題 Google有人回報但似乎官方尚未解決
            // https://stackoverflow.com/questions/33264031/calling-dialogfragments-show-from-within-onrequestpermissionsresult-causes
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // do your fragment transaction here

                    if (deniedAndNeverShowPermissions.size() > 0) {
                        // 被永久拒絕顯示 只能從設定去開啟
                        permissionManager.requestPermissionCallback.deniedAndNeverShowed(deniedAndNeverShowPermissions);
                    }
                    if (grantedPermissions.size() > 0 && deniedPermissions.size() == 0) {
                        // 沒有dialog要顯示, 也沒有拒絕的, 而且承認的大於0.
                        permissionManager.requestPermissionCallback.grantedPermissions(grantedPermissions);
                    } else if (deniedPermissions.size() > 0) {
                        // 沒有dialog要顯示, 拒絕的大於0.
                        permissionManager.requestPermissionCallback.deniedPermissions(deniedPermissions);
                    }
                }
            }, 200);
        }
    }

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }
}
