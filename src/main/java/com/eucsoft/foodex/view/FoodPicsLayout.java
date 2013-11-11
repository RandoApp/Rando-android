package com.eucsoft.foodex.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FoodPicsLayout extends LinearLayout {

    private Context myContext;
    private ArrayList<String> itemList = new ArrayList<String>();
    private OnClickListener onClickListener;

    private static BitmapFactory.Options decodeOptions;

    static {
        decodeOptions = new BitmapFactory.Options();
        decodeOptions.inDither = false;
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        decodeOptions.inSampleSize = 3;
        decodeOptions.inPurgeable = true;
    }

    public FoodPicsLayout(Context context) {
        super(context);
        myContext = context;
    }

    public FoodPicsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
    }

    public void add(String path, int imgSize) {
        int newIdx = itemList.size();
        itemList.add(path);
        addView(getImageView(newIdx, imgSize));
    }

    private ImageView getImageView(final int i, final int imgSize) {
        Bitmap bm = null;
        if (i < itemList.size()) {
            bm = decodeSampledBitmapFromUri(itemList.get(i), imgSize, imgSize);
        }

        ImageView imageView = new ImageView(myContext);
        imageView.setLayoutParams(new LayoutParams(imgSize, imgSize));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bm);

        imageView.setOnClickListener(onClickListener);

        return imageView;
    }

    private Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        Bitmap bm = null;
        // Calculate inSampleSize
        decodeOptions.inSampleSize = calculateInSampleSize(decodeOptions, reqWidth, reqHeight);
        bm = BitmapFactory.decodeFile(path, decodeOptions);

        return bm;
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}