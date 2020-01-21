package dean.tryhard.project.baseproject.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    public static final int PERMISSION_REQUEST_CODE = 348;

    public IRequestPermissionCallback requestPermissionCallback;
    public String explain;

    public interface IRequestPermissionCallback {
        void grantedPermissions(List<String> permissions);
        void deniedPermissions(List<String> permissions);
        void deniedAndNeverShowed(List<String> permissions);
    }

    public boolean checkAndRequestPermission(Activity targetActivity, String permission, @Nullable String explain, IRequestPermissionCallback requestPermissionCallback){
        return checkAndRequestPermission(targetActivity, new String[]{permission}, explain, requestPermissionCallback);
    }

    public boolean checkAndRequestPermission(Activity targetActivity, DangerPermission dangerPermission, @Nullable String explain, IRequestPermissionCallback requestPermissionCallback) {
        String permissionString = getDangerPermissionString(dangerPermission);
        return checkAndRequestPermission(targetActivity, new String[]{permissionString}, explain, requestPermissionCallback);
    }

    public boolean checkAndRequestPermission(Activity targetActivity, DangerPermission[] dangerPermissions, @Nullable String explain, IRequestPermissionCallback requestPermissionCallback) {
        List<String> permissionStrings = new ArrayList<>();
        for(int i = 0 ; i < dangerPermissions.length;i++){
            permissionStrings.add(getDangerPermissionString(dangerPermissions[i]));
        }
        return checkAndRequestPermission(targetActivity, permissionStrings.toArray(new String[permissionStrings.size()]), explain, requestPermissionCallback);
    }

    /**
     * [2017/06/18] jason 新增給Fragment用的方法, 參數是Activity的直接抄過來, 目的為在Fragment使用.
     * @param fragment 呼叫的Activity 原本使用者接受或拒絕權限給予後回呼 該Fragment的 onRequestPermissionsResult 這裡全部在BaseFragment 接走後傳給 requestPermissionCallback
     * @param dangerPermissions 想取得的權限
     * @param explain 第2次以後詢問 方法可調整為Alert 現在為Toast Message
     * @param requestPermissionCallback 成功或失敗的callBack
     * @return 是否早就已經取得了權限
     */
    public boolean checkAndRequestPermission(final Fragment fragment, DangerPermission[] dangerPermissions, @Nullable String explain, IRequestPermissionCallback requestPermissionCallback) {
        List<String> permissionStrings = new ArrayList<>();
        for(int i = 0 ; i < dangerPermissions.length;i++){
            permissionStrings.add(getDangerPermissionString(dangerPermissions[i]));
        }
        return checkAndRequestPermission(fragment, permissionStrings.toArray(new String[permissionStrings.size()]), explain, requestPermissionCallback);
    }

    /**
     * 從參數是Activity的直接抄過來, 目的為在Fragment使用.
     * @param fragment  呼叫的Activity 原本使用者接受或拒絕權限給予後回呼 該Fragment的 onRequestPermissionsResult 這裡全部在BaseFragment 接走後傳給 requestPermissionCallback
     * @param permissions 想取得的權限 使用方法 new String[ ]{權限1, 權限2, ...}
     * @param explain 第2次以後詢問 方法可調整為Alert 現在為Toast Message
     *                [2017/06/20] jason 暫時關掉toast, 因為會造成圖層重疊
     * @param requestPermissionCallback 成功或失敗的callBack
     * @return 是否早就已經取得了權限
     */
    private boolean checkAndRequestPermission(final Fragment fragment, final String[] permissions, @Nullable String explain, IRequestPermissionCallback requestPermissionCallback) {
        this.requestPermissionCallback = requestPermissionCallback;
        this.explain = explain;
        List<String> notGrantedPermissions = new ArrayList<>();
        for(int i = 0 ; i < permissions.length;i++){
            int permissionCheck = ActivityCompat.checkSelfPermission(fragment.getActivity(), permissions[i]);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permissions[i]);
            }
        }

        if(notGrantedPermissions.size() == 0){
            //全部想要的權限都已經取得
            return true;
        }
        //確認SDK版本是否為6.0(API 23)以上 (不確認其實也沒事)
        if(Build.VERSION.SDK_INT < 23)
            return true;

        //確認是否為下次需要說明與新增不再詢問的check box
        boolean needExplain = false;
        for(int i = 0 ; i < notGrantedPermissions.size();i++){
            //多種要求只要有一種需要說明理由 就將explain 給顯示使用者看
            needExplain = (needExplain || ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), notGrantedPermissions.get(i)));
        }
        if(needExplain){
            //上次已被拒絕 請於此顯示說明 ( Google : 請一定要誠實說明用途 )
            if(explain != null){
                // [2017/06/20] jason 實測發現如果在toast還沒消掉的情況下再去開dialog, 系統會跳圖層重疊.
//                Toast.makeText(fragment.getBaseActivity(), explain, Toast.LENGTH_LONG).show();
            }
            fragment.requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }else{
            //第一次詢問
            fragment.requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
        return false;
    }

    /**
     *
     * @param targetActivity  呼叫的Activity 原本使用者接受或拒絕權限給予後回呼 該activity的 onRequestPermissionsResult 這裡全部在title 接走後傳給 requestPermissionCallback
     * @param permissions 想取得的權限 使用方法 new String[ ]{權限1, 權限2, ...}
     * @param explain 第2次以後詢問 方法可調整為Alert 現在為Toast Message,
     *                [2017/06/20] jason 暫時關掉toast, 因為會造成圖層重疊
     * @param requestPermissionCallback 成功或失敗的callBack
     * @return
     */
    private boolean checkAndRequestPermission(final Activity targetActivity, final String[] permissions, @Nullable String explain, IRequestPermissionCallback requestPermissionCallback) {
        this.requestPermissionCallback = requestPermissionCallback;
        this.explain = explain;
        List<String> notGrantedPermissions = new ArrayList<>();
        for(int i = 0 ; i < permissions.length;i++){
            int permissionCheck = ActivityCompat.checkSelfPermission(targetActivity, permissions[i]);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permissions[i]);
            }
        }

        if(notGrantedPermissions.size() == 0){
            //全部想要的權限都已經取得
            return true;
        }
        //確認SDK版本是否為6.0(API 23)以上 (不確認其實也沒事)
        if(Build.VERSION.SDK_INT < 23)
            return true;

        //確認是否為下次需要說明與新增不再詢問的check box
        boolean needExplain = false;
        for(int i = 0 ; i < notGrantedPermissions.size();i++){
            //多種要求只要有一種需要說明理由 就將explain 給顯示使用者看
            needExplain = (needExplain || ActivityCompat.shouldShowRequestPermissionRationale(targetActivity, notGrantedPermissions.get(i)));
        }
        if(needExplain){
            //上次已被拒絕 請於此顯示說明 ( Google : 請一定要誠實說明用途 )
            if(explain != null){
                // [2017/06/20] jason 實測發現如果在toast還沒消掉的情況下再去開dialog, 系統會跳圖層重疊.
//                Toast.makeText(targetActivity, explain, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(targetActivity, permissions, PERMISSION_REQUEST_CODE);
        }else{
            //第一次詢問
            ActivityCompat.requestPermissions(targetActivity, permissions, PERMISSION_REQUEST_CODE);
        }
        return false;
    }

    //需要特別向使用者詢問的權限
    private String getDangerPermissionString(DangerPermission permission) {
        switch (permission) {
            case READ_CALENDAR:
                return Manifest.permission.READ_CALENDAR;
            case CAMERA:
                return Manifest.permission.CAMERA;
            case READ_CONTACTS:
                return Manifest.permission.READ_CONTACTS;
            case ACCESS_FINE_LOCATION:
                return Manifest.permission.ACCESS_FINE_LOCATION;
            case ACCESS_COARSE_LOCATION:
                return Manifest.permission.ACCESS_COARSE_LOCATION;
            case RECORD_AUDIO:
                return Manifest.permission.RECORD_AUDIO;
            case READ_PHONE_STATE:
                return Manifest.permission.READ_PHONE_STATE;
            case BODY_SENSORS:
                return Manifest.permission.BODY_SENSORS;
            case SEND_SMS:
                return Manifest.permission.SEND_SMS;
            case WRITE_EXTERNAL_STORAGE:
                return Manifest.permission.WRITE_EXTERNAL_STORAGE;
            case GET_ACCOUNTS:
                return Manifest.permission.GET_ACCOUNTS;
            default:
                return null;
        }
    }

    //需要特別向使用者詢問的權限
    public enum DangerPermission {
        READ_CALENDAR,
        CAMERA,
        READ_CONTACTS,
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION,
        RECORD_AUDIO,
        READ_PHONE_STATE,
        BODY_SENSORS,
        SEND_SMS,
        WRITE_EXTERNAL_STORAGE,
        GET_ACCOUNTS
    }
}

