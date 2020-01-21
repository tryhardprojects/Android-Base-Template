package dean.tryhard.project.baseproject.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import dean.tryhard.project.baseproject.R;

public class LoadingView extends Dialog {
    private TextView mLoadingText;
    public LoadingView(@NonNull Context context) {
        super(context, R.style.CSProgressDialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_loading);

        ImageView img = findViewById(R.id.loadingImageView);
//        img.setBackgroundResource(R.drawable.progress_loading);


        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
    }

    public void setText(String text){
        if(mLoadingText != null){
            mLoadingText.setText(text);
        }
    }
}
