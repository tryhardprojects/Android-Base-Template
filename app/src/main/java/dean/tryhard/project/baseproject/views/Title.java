package dean.tryhard.project.baseproject.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import dean.tryhard.project.baseproject.R;

public class Title extends ConstraintLayout {
    private Context mContext;

    /** api 19以上, 為status bar高度(因為可以到其下方); 小於 api 19, 為0 */
    private int mTopMargin;

    public Title(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public Title(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public Title(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public static int getTopMargin(Context context) {
        // 在api 19以上, 可以到status bar的下方
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            return 0;
//        }

        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private void init() {
        inflate(mContext, R.layout.view_title, this);

        mTopMargin = getTopMargin(mContext);

        // 設定padding top (statusBar)
        ConstraintLayout.LayoutParams contentLayoutParams =
                (LayoutParams) findViewById(R.id.title_actionbar).getLayoutParams();
        contentLayoutParams.topMargin = mTopMargin;
    }
}
