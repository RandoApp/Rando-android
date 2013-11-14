package com.eucsoft.foodex.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.model.FoodPair;

public class FoodPicsLayout extends TwoWayGridView {

    private Context context;
    private OnClickListener onClickListener;
    private int imgSize;
    private FoodPair.User user;

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
        this(context, null);
        this.context = context;
    }

    public FoodPicsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FoodPicsLayout(Context context, AttributeSet attrs, int defStyle, Context context1) {
        super(context, attrs, defStyle);
        context = context1;
    }

    public void setImgSize(int imgSize) {
        this.imgSize = imgSize;
        this.setColumnWidth(imgSize);
    }

    public void setUser(FoodPair.User user) {
        this.user = user;
        setAdapter(new FoodPairAdapter(user));
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class FoodOnScrollListener implements TwoWayAbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(TwoWayAbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:

            }

        }

        @Override
        public void onScroll(TwoWayAbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }


    class FoodPairAdapter extends BaseAdapter {
        private final String[] values;

        public FoodPairAdapter(FoodPair.User user) {
            this.values = new String[2];
            values[0] = user.foodURL;
            values[1] = user.mapURL;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView imageView = (ImageView) inflater.inflate(R.layout.imageview, parent, false);

            Bitmap bm = BitmapFactory.decodeFile(values[position], decodeOptions);
            imageView.setLayoutParams(new LayoutParams(imgSize, imgSize));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setImageBitmap(bm);
            imageView.setImageResource(R.drawable.f);

            imageView.setOnClickListener(onClickListener);
        return imageView;
        }

        @Override
        public int getCount() {
            return values.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}