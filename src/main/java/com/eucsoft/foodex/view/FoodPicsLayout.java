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
            bm = BitmapFactory.decodeFile(itemList.get(i), decodeOptions);
        }

        ImageView imageView = new ImageView(myContext);
        imageView.setLayoutParams(new LayoutParams(imgSize, imgSize));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bm);

        imageView.setOnClickListener(onClickListener);

        return imageView;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}