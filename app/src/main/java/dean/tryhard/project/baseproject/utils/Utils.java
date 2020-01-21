package dean.tryhard.project.baseproject.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dean.tryhard.project.baseproject.GlobalApplication;
import dean.tryhard.project.baseproject.ui.base.BaseView;

public class Utils {

    public static void setOnlyEnglishNumber(EditText editText) {
        InputFilter[] arr = editText.getFilters();
        InputFilter[] newArr = new InputFilter[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                Pattern p = Pattern.compile("[a-zA-Z|\u4e00-\u9fa5]+");
                Pattern p = Pattern.compile("[a-zA-Z0-9]+");
                Matcher m = p.matcher(source.toString());
                if (!m.matches()) return "";
                return null;
            }
        };

        editText.setFilters(newArr);
    }


    public static void setOnlyNumber(EditText editText) {
        InputFilter[] arr = editText.getFilters();
        InputFilter[] newArr = new InputFilter[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                Pattern p = Pattern.compile("[a-zA-Z|\u4e00-\u9fa5]+");
                Pattern p = Pattern.compile("[0-9]+");
                Matcher m = p.matcher(source.toString());
                if (!m.matches()) return "";
                return null;
            }
        };

        editText.setFilters(newArr);
    }

    public static void setLengthLimitation(EditText editText, int length) {
        InputFilter[] arr = editText.getFilters();
        InputFilter[] newArr = new InputFilter[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = new InputFilter.LengthFilter(length);

        editText.setFilters(newArr);
    }

    public static int convertDpToPixel(int dp) {
        Context context = GlobalApplication.getContext();
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static void expand(final View v, long duration) {
        v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 0;
        v.setVisibility(View.GONE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime > 0.02) {
                    v.setVisibility(View.VISIBLE);
                }
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(duration);
        v.startAnimation(a);
    }

    public static void collapse(final View v, long duration, AnimationFinishedCallback animationFinishedCallback) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animationFinishedCallback != null) {
                    animationFinishedCallback.done();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // 1dp/ms
        a.setDuration(duration);
        v.startAnimation(a);
    }

    public static int getScreenWidthPixel() {
        Context context = GlobalApplication.getContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int pxlHeight = displayMetrics.heightPixels;
        int pxlWidth = displayMetrics.widthPixels;
        return pxlWidth;
    }

    public static int getScreenHeightPixel() {
        Context context = GlobalApplication.getContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int pxlHeight = displayMetrics.heightPixels;
        int pxlWidth = displayMetrics.widthPixels;
        return pxlHeight - getStausBarHeight();
    }

    public static int getStausBarHeight() {
        int height = 0;
        int resourceId = GlobalApplication.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = GlobalApplication.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static int getNavBarHeight() {
        Resources resources = GlobalApplication.getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static boolean hasMicrophone() {
        return GlobalApplication.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public interface AnimationFinishedCallback {
        void done();
    }

    public static void viewFadeOut(final View view, long duration, AnimationFinishedCallback animationFinishedCallback) {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(duration);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
                if (animationFinishedCallback != null) {
                    animationFinishedCallback.done();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(anim);
    }

    public static void viewFadeIn(View view, long duration, AnimationFinishedCallback animationFinishedCallback) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(duration);
        view.setVisibility(View.VISIBLE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animationFinishedCallback != null) {
                    animationFinishedCallback.done();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(anim);
    }

    public static double bytesToMB(long bytes) {
        return (double) bytes / 1024f / 1024f;
    }

    /**
     * 解壓縮檔案至指定目錄
     *
     * @param zipFile         需要解壓縮的zip檔案
     * @param targetDirectory 解壓縮至目錄的path
     * @throws IOException 可能拋出的錯誤
     */
    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
    }

    public static boolean checkEmailValid(String email) {
        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
//
//    public static String birthFormatToDash(String birthdayWithSlash) {
//        SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
//        SimpleDateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        Date date = new Date();
//        try {
//            date = formatFrom.parse(birthdayWithSlash);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return formatTo.format(date);
//    }
//
//    public static String birthFormatToSlash(String birthdayWithDash) {
//        SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        SimpleDateFormat formatTo = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
//        Date date = new Date();
//        try {
//            date = formatFrom.parse(birthdayWithDash);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return formatTo.format(date);
//    }

    public static Date getDateFromyyyyMM(String yyyyMM) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(yyyyMM);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateStringFromDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getTodayDateString() {
        return getDateStringFromDate(Calendar.getInstance().getTime());
    }

    public static void initShareIntent(BaseView baseView, Context context, Intent shareIntent, String type, String shareMessage) {
        boolean found = false;

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = GlobalApplication.getContext().getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type)) {
                    shareIntent.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found) {
//                baseView.showMessage(context.getString(R.string.error_no_app_title), context.getString(R.string.error_no_app_context), true, null);
                return;
            }

            context.startActivity(Intent.createChooser(shareIntent, shareMessage));
        }
    }

    public static Intent composeChooserTextIntent(String text) {
        Intent shareIntent;

        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");

        return shareIntent;
    }

    public static Intent composeChooserImagesIntent(ArrayList<Uri> imageUris) {
        Intent shareIntent;

        shareIntent = new Intent();


        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        shareIntent.setType("image/*");
        return shareIntent;
    }

    public static String getStringFromAssets(String fileName) {
        String tContents = "";
        try {
            InputStream stream = GlobalApplication.getContext().getAssets().open(fileName);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }

        return tContents;
    }

}
