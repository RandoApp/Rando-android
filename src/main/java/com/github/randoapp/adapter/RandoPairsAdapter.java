package com.github.randoapp.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ViewSwitcher;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.animation.AnimationFactory;
import com.github.randoapp.animation.AnimationListenerAdapter;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.log.Log;
import com.github.randoapp.network.VolleySingleton;
import com.github.randoapp.util.BitmapUtil;
import com.github.randoapp.util.RandoPairUtil;
import com.makeramen.RoundedImageView;

import java.util.List;

import static com.android.volley.Request.Priority;

public class RandoPairsAdapter extends BaseAdapter {

    private boolean isStranger;

    private List<RandoPair> randoPairs;
    private int imageSize;

    private int size;

    private Context context;

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return randoPairs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public RandoPairsAdapter(Context context, boolean isStranger) {
        // get the window width according to device screen size
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int displayWidth = displaymetrics.widthPixels;
        int orientation = context.getResources().getConfiguration().orientation;
        this.context = context;
        imageSize = getRandoImageSize(orientation, displayWidth);
        this.isStranger = isStranger;
        initData();
    }

    private void initData() {
        randoPairs = RandoDAO.getAllRandos(!isStranger);
        size = randoPairs.size();
    }

    @Override
    public void notifyDataSetChanged() {
        initData();
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final RandoPair randoPair = randoPairs.get(position);
        final ViewHolder holder;

        Log.i(RandoPairsAdapter.class, "isStranger", String.valueOf(isStranger), "Size:", String.valueOf(size), "Position", String.valueOf(position));

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rando_item, container, false);
            holder = createHolder(convertView);
            addListenersToHolder(holder);
        }

        recycle(holder);
        loadImages(holder, randoPair);
        setAnimations(holder);
        return convertView;
    }

    private ViewHolder createHolder(View convertView) {
        ViewHolder holder = new ViewHolder();

        holder.viewSwitcher = (ViewSwitcher) convertView.findViewWithTag("viewSwitcher");

        holder.image = (RoundedImageView) convertView.findViewWithTag("image");
        holder.map = (RoundedImageView) convertView.findViewWithTag("map");
        ViewSwitcher.LayoutParams randoImagesLayout = new ViewSwitcher.LayoutParams(imageSize, imageSize);
        holder.image.setLayoutParams(randoImagesLayout);
        holder.map.setLayoutParams(randoImagesLayout);

        convertView.setTag(holder);
        return holder;
    }

    private void addListenersToHolder(final ViewHolder holder) {
        View.OnClickListener randoOnClickListener = createRandoOnClickListener(holder);
        holder.image.setOnClickListener(randoOnClickListener);
        holder.map.setOnClickListener(randoOnClickListener);
    }

    private View.OnClickListener createRandoOnClickListener(final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.animationInProgress) return;

                holder.viewSwitcher.showNext();
            }
        };
    }

    private void recycle(ViewHolder holder) {
        holder.animationInProgress = false;

        cancelRequests(holder);

        recycleViewSwitcher(holder.viewSwitcher);

        holder.image.setImageBitmap(null);
        holder.map.setImageBitmap(null);
    }

    private void cancelRequests(ViewHolder holder) {
        if (holder.randoContainer != null) {
            holder.randoContainer.cancelRequest();
            holder.randoContainer = null;
        }
        if (holder.mapContainer != null) {
            holder.mapContainer.cancelRequest();
            holder.mapContainer = null;
        }
    }

    private void recycleViewSwitcher(ViewSwitcher viewSwitcher) {
        //disable animation for immediately and undetectable switching to zero child:
        viewSwitcher.setInAnimation(null);
        viewSwitcher.setOutAnimation(null);
        viewSwitcher.setDisplayedChild(0);
    }

    private int getRandoImageSize(int orientation, int displayWidth) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return displayWidth / 2 - (context.getResources().getDimensionPixelSize(R.dimen.rando_padding_landscape_column_left) + context.getResources().getDimensionPixelSize(R.dimen.rando_padding_landscape_column_right));
        } else {
            return displayWidth - Constants.RANDO_MARGIN_PORTRAIT;
        }
    }

    private void setAnimations(final ViewHolder holder) {
        final Animation[] leftToRightAnimation = AnimationFactory.flipAnimation(imageSize, AnimationFactory.FlipDirection.LEFT_RIGHT, 350, null);
        final Animation[] rightToLeftAnimation = AnimationFactory.flipAnimation(imageSize, AnimationFactory.FlipDirection.RIGHT_LEFT, 350, null);

        holder.viewSwitcher.setOutAnimation(leftToRightAnimation[0]);
        holder.viewSwitcher.setInAnimation(leftToRightAnimation[1]);

        Animation.AnimationListener outAnimationListener = new AnimationListenerAdapter() {
            @Override
            public void onAnimationStart(Animation animation) {
                holder.animationInProgress = true;
            }
        };
        leftToRightAnimation[0].setAnimationListener(outAnimationListener);
        leftToRightAnimation[1].setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                holder.viewSwitcher.setOutAnimation(rightToLeftAnimation[0]);
                holder.viewSwitcher.setInAnimation(rightToLeftAnimation[1]);
                holder.animationInProgress = false;
            }
        });

        rightToLeftAnimation[0].setAnimationListener(outAnimationListener);
        rightToLeftAnimation[1].setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                holder.viewSwitcher.setOutAnimation(leftToRightAnimation[0]);
                holder.viewSwitcher.setInAnimation(leftToRightAnimation[1]);
                holder.animationInProgress = false;
            }
        });
    }

    private void loadImages(final ViewHolder holder, final RandoPair randoPair) {
        if (randoPair.user.imageURLSize.small!=null && !URLUtil.isNetworkUrl(randoPair.user.imageURLSize.small) && !randoPair.user.imageURLSize.small.isEmpty()) {
            loadFile(holder, randoPair.user.imageURL);
            return;
        }

        loadImage(holder, RandoPairUtil.getUrlByImageSize(imageSize, isStranger ? randoPair.stranger.imageURLSize : randoPair.user.imageURLSize), Priority.HIGH);
        loadMapImage(holder, RandoPairUtil.getUrlByImageSize(imageSize, isStranger ? randoPair.stranger.mapURLSize : randoPair.user.mapURLSize), Priority.LOW);
    }

    private void loadFile(final ViewHolder holder, final String filePath) {
        if (holder.image != null) {
            holder.image.setImageBitmap(BitmapUtil.decodeSampledBitmap(filePath, imageSize, imageSize));
        }
        if (holder.map != null) {
            holder.map.setImageResource(R.drawable.rando_pairing);
        } else {
            holder.needSetPairing = true;
        }
    }

    private void loadImage(final ViewHolder viewHolder, final String url, Priority priority) {
        if (TextUtils.isEmpty(url)) {
            if (viewHolder.image != null) {
                viewHolder.image.setImageResource(R.drawable.rando_pairing);
            } else {
                viewHolder.needSetPairing = true;
            }
            return;
        }

        if (URLUtil.isValidUrl(url)) {
            Log.d(RandoPairsAdapter.class, "image url: ", url);
            viewHolder.randoContainer = VolleySingleton.getInstance().getImageLoader().get(url, priority, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (viewHolder.image != null && response.getBitmap() != null) {
                        viewHolder.image.setImageBitmap(response.getBitmap());
                    } else if (viewHolder.image != null && response.getBitmap() == null) {
                        viewHolder.image.setImageResource(R.drawable.rando_loading);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(RandoPairsAdapter.class, "VolleyError when load rando image: ", url, "with imageSize = ", String.valueOf(imageSize), " , because ", error.getMessage());
                    if (viewHolder.image != null) {
                        viewHolder.image.setImageResource(R.drawable.rando_error);
                    } else {
                        viewHolder.needSetImageError = true;
                    }
                }
            });
        } else {
            Log.e(RandoPairsAdapter.class, "Ignore rando image because url: ", url, " incorrect");
            if (viewHolder.image != null) {
                viewHolder.image.setImageResource(R.drawable.rando_error);
            } else {
                viewHolder.needSetImageError = true;
            }
        }
    }

    private void loadMapImage(final ViewHolder viewHolder, final String url, Priority priority) {
        if (URLUtil.isValidUrl(url)) {
            Log.d(RandoPairsAdapter.class, "map url: ", url);
            viewHolder.mapContainer = VolleySingleton.getInstance().getImageLoader().get(url, priority, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (viewHolder.map != null && response.getBitmap() != null) {
                        viewHolder.map.setImageBitmap(response.getBitmap());
                    } else if (viewHolder.map != null && response.getBitmap() == null) {
                        viewHolder.map.setImageResource(R.drawable.rando_loading);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(RandoPairsAdapter.class, "VolleyError when load map image: ", url, "with imageSize = ", String.valueOf(imageSize), " , because ", error.getMessage());
                    if (viewHolder.map != null) {
                        viewHolder.map.setImageResource(R.drawable.rando_error);
                    } else {
                        viewHolder.needSetMapError = true;
                    }
                }
            });
        } else {
            Log.e(RandoPairsAdapter.class, "Ignore map image because url: ", url, " incorrect");
            if (viewHolder.map != null) {
                viewHolder.map.setImageResource(R.drawable.rando_error);
            } else {
                viewHolder.needSetMapError = true;
            }
        }
    }

    public static class ViewHolder {
        public boolean animationInProgress = false;

        public ViewSwitcher viewSwitcher;

        public RoundedImageView image;
        public RoundedImageView map;

        public ImageLoader.ImageContainer randoContainer;
        public ImageLoader.ImageContainer mapContainer;

        public boolean needSetImageError = false;
        public boolean needSetMapError = false;

        public boolean needSetPairing = false;
    }
}
