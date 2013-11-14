package com.eucsoft.foodex.view;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.task.DownloadImageTask;

public class FoodPicsLayout extends ViewPager {

    private OnClickListener onClickListener;
    private int imgSize;
    private FoodPair.User user;
    boolean isMap = false;
    boolean isFingerScrolling = false;

    public FoodPicsLayout(Context context) {
        this(context, null);
    }

    public FoodPicsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImgSize(int imgSize) {
        this.imgSize = imgSize;
    }

    public void setUser(FoodPair.User user) {
        this.user = user;
        setAdapter(new FoodPairAdapter(user));
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class FoodPairAdapter extends PagerAdapter {
        private final String[] values;

        public FoodPairAdapter(FoodPair.User user) {
            this.values = new String[2];
            values[0] = user.foodURL;
            values[1] = user.mapURL;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            CallableImageView imageView = new CallableImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.f);
            imageView.setOnClickListener(onClickListener);
            container.addView(imageView, 0);
            DownloadImageTask downloadImageTask = new DownloadImageTask(imageView, container.getContext());
            if (position == 0) {
                downloadImageTask.execute(user.foodURL);
            } else {
                downloadImageTask.execute(user.mapURL);
            }
            return imageView;
        }

        @Override
        public int getCount() {
            return values.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);

        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}