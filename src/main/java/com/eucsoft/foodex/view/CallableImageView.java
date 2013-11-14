package com.eucsoft.foodex.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.BaseTask;

import java.util.HashMap;

public class CallableImageView extends ImageView implements TaskResultListener {

    public CallableImageView(Context context) {
        super(context);
    }

    private static BitmapFactory.Options decodeOptions;

    static {
        decodeOptions = new BitmapFactory.Options();
        decodeOptions.inDither = false;
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        decodeOptions.inSampleSize = 3;
        decodeOptions.inPurgeable = true;
    }

    public CallableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CallableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {

        if (resultCode == BaseTask.RESULT_OK) {
            Bitmap bm = BitmapFactory.decodeFile((String) data.get(Constants.FILEPATH), decodeOptions);
            setScaleType(ImageView.ScaleType.CENTER_CROP);
            setImageBitmap(bm);
        }
    }
}
