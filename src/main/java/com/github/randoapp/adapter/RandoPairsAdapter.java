package com.github.randoapp.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.animation.AnimationFactory;
import com.github.randoapp.animation.AnimationListenerAdapter;
import com.github.randoapp.api.API;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.RandoPair;
import com.github.randoapp.log.Log;
import com.github.randoapp.menu.ReportMenu;
import com.github.randoapp.network.VolleySingleton;
import com.github.randoapp.service.SyncService;
import com.github.randoapp.util.BitmapUtil;
import com.github.randoapp.util.RandoPairUtil;

import org.apache.http.auth.AuthenticationException;

import java.text.DateFormat;
import java.util.List;

import static android.view.View.VISIBLE;
import static com.android.volley.Request.Priority;

public class RandoPairsAdapter extends BaseAdapter {

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

    public RandoPairsAdapter(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int displayWidth = display.getWidth();
        int orientation = context.getResources().getConfiguration().orientation;
        this.context=context;
        imageSize = getRandoImageSize(orientation, displayWidth);
        initData();
    }

    private void initData() {
        randoPairs = RandoDAO.getAllRandos();
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

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rando_item, container, false);
            holder = createHolder(convertView);
            addListenersToHolder(container.getContext(), holder);
        }

        recycle(holder, randoPair);
        loadImages(holder, randoPair);
        setAnimations(holder);
        return convertView;
    }

    private ViewHolder createHolder(View convertView) {
        ViewHolder holder = new ViewHolder();

        holder.user = new ViewHolder.UserHolder();
        holder.stranger = new ViewHolder.UserHolder();

        holder.viewSwitcher = (ViewSwitcher) convertView.findViewWithTag("viewSwitcher");

        holder.imagePager = (ViewPager) convertView.findViewWithTag("image");
        holder.mapPager = (ViewPager) convertView.findViewWithTag("map");

        ViewSwitcher.LayoutParams randoImagesLayout = new ViewSwitcher.LayoutParams(imageSize, imageSize);
        holder.imagePager.setLayoutParams(randoImagesLayout);
        holder.mapPager.setLayoutParams(randoImagesLayout);

        holder.imagePagerAdapter = new ImagePagerAdapter(holder, imageSize);
        holder.imagePager.setAdapter(holder.imagePagerAdapter);

        RandoPagerListener randoPagerListener = new RandoPagerListener(holder);
        holder.imagePager.setOnPageChangeListener(randoPagerListener);
        holder.mapPager.setOnPageChangeListener(randoPagerListener);

        holder.mapPagerAdapter  = new MapPagerAdapter(holder, imageSize);
        holder.mapPager.setAdapter(holder.mapPagerAdapter);

        holder.dateTimeView = (TextView) convertView.findViewWithTag("date_time");

        holder.homeIcon = (ViewSwitcher) convertView.findViewWithTag("home_ic_switcher");

        createReportDialog(convertView, holder);

        convertView.setTag(holder);
        return holder;
    }

    private void createReportDialog(View convertView, final ViewHolder holder) {
        holder.reportDialog = (LinearLayout) convertView.findViewWithTag("report_dialog");
        Button reportButton = (Button) holder.reportDialog.getChildAt(0);
        int reportButtonWidth = convertView.getResources().getDimensionPixelSize(R.dimen.report_button_width);
        int reportButtonHeight = convertView.getResources().getDimensionPixelSize(R.dimen.report_button_height);

        LinearLayout.LayoutParams reportButtonParams = new LinearLayout.LayoutParams(reportButtonWidth, reportButtonHeight);
        int marginCenter = imageSize / 2;
        reportButtonParams.topMargin = marginCenter - reportButtonHeight / 2;
        reportButtonParams.leftMargin = marginCenter - reportButtonWidth / 2;
        reportButton.setLayoutParams(reportButtonParams);
        holder.reportDialog.setLayoutParams(new RelativeLayout.LayoutParams(imageSize, imageSize));
    }

    private void addListenersToHolder(Context context, final ViewHolder holder) {
        View.OnClickListener randoOnClickListener = createRandoOnClickListener(context, holder);
        holder.imagePagerAdapter.setOnClickListener(randoOnClickListener);
        holder.mapPagerAdapter.setOnClickListener(randoOnClickListener);
        holder.reportDialog.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        holder.reportDialog.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    API.report(holder.randoPair.stranger.randoId);
                    ReportMenu.off();
                    SyncService.run();
                } catch (AuthenticationException exc) {
                    exc.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private View.OnClickListener createRandoOnClickListener(final Context context, final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.animationInProgress) return;

                int strangerOrUserItem = ((ViewPager) holder.viewSwitcher.getCurrentView()).getCurrentItem();
                holder.viewSwitcher.showNext();
                ViewPager randoMapView = (ViewPager) holder.viewSwitcher.getCurrentView();
                randoMapView.setCurrentItem(strangerOrUserItem);
            }
        };
    }

    private void recycle(ViewHolder holder, RandoPair randoPair) {
        holder.randoPair = randoPair;
        holder.animationInProgress = false;

        cancelRequests(holder);

        recycleViewSwitcher(holder.viewSwitcher);
        recycleViewPager(holder);

        holder.dateTimeView.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(holder.randoPair.user.date) + " "); //1 space for fix italic cutting off issue

        if (ReportMenu.isReport) {
            holder.reportDialog.setVisibility(VISIBLE);
            return;
        }

        holder.imagePagerAdapter.recycle();
        holder.mapPagerAdapter.recycle();

        holder.reportDialog.setVisibility(View.GONE);
    }

    private void cancelRequests(ViewHolder holder) {
        if (holder.stranger.randoContainer != null) {
            holder.stranger.randoContainer.cancelRequest();
            holder.stranger.randoContainer = null;
        }
        if (holder.stranger.mapContainer != null) {
            holder.stranger.mapContainer.cancelRequest();
            holder.stranger.mapContainer = null;
        }
        if (holder.user.randoContainer != null) {
            holder.user.randoContainer.cancelRequest();
            holder.user.randoContainer = null;
        }
        if (holder.user.mapContainer != null) {
            holder.user.mapContainer.cancelRequest();
            holder.user.mapContainer = null;
        }
    }

    private void recycleViewSwitcher(ViewSwitcher viewSwitcher) {
        //disable animation for immediately and undetectable switching to zero child:
        viewSwitcher.setInAnimation(null);
        viewSwitcher.setOutAnimation(null);
        viewSwitcher.setDisplayedChild(0);
    }

    private void recycleViewPager(ViewHolder holder) {
        holder.imagePager.setCurrentItem(0);
        holder.mapPager.setCurrentItem(0);
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
        if (!URLUtil.isNetworkUrl(randoPair.user.imageURLSize.small) && !randoPair.user.imageURLSize.small.isEmpty()) {
            loadFile(holder, randoPair.user.imageURL);
            return;
        }

        loadImage(holder.stranger, RandoPairUtil.getUrlByImageSize(imageSize, randoPair.stranger.imageURLSize), Priority.HIGH);
        loadImage(holder.user, RandoPairUtil.getUrlByImageSize(imageSize, randoPair.user.imageURLSize), Priority.NORMAL);
        loadMapImage(holder.stranger, RandoPairUtil.getUrlByImageSize(imageSize, randoPair.stranger.mapURLSize), Priority.LOW);
        loadMapImage(holder.user, RandoPairUtil.getUrlByImageSize(imageSize, randoPair.user.mapURLSize), Priority.LOW);
    }

    private void loadFile(final ViewHolder holder, final String filePath) {
        if (holder.user.image != null) {
            holder.user.image.setImageBitmap(BitmapUtil.decodeSampledBitmap(filePath, imageSize, imageSize));
        } else if (holder.user.image == null) {
            holder.user.imageBitmap = BitmapUtil.decodeSampledBitmap(filePath, imageSize, imageSize);
        }

        if (holder.user.map != null) {
            holder.user.map.setImageResource(R.drawable.rando_pairing);
        } else {
            holder.user.needSetPairing = true;
        }

        if (holder.stranger.image != null) {
            holder.stranger.image.setImageResource(R.drawable.rando_pairing);
        } else {
            holder.stranger.needSetPairing = true;
        }

        if (holder.stranger.map != null) {
            holder.stranger.map.setImageResource(R.drawable.rando_pairing);
        } else {
            holder.stranger.needSetPairing = true;
        }
    }

    private void loadImage(final ViewHolder.UserHolder userHolder, final String url, Priority priority) {
        if (TextUtils.isEmpty(url)) {
            if (userHolder.image != null) {
                userHolder.image.setImageResource(R.drawable.rando_pairing);
            } else {
                userHolder.needSetPairing = true;
            }
            return;
        }

        if (URLUtil.isValidUrl(url)) {
            Log.d(RandoPairsAdapter.class, "image url: ", url);
            userHolder.randoContainer = VolleySingleton.getInstance().getImageLoader().get(url, priority, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (userHolder.image != null && response.getBitmap() != null) {
                        userHolder.image.setImageBitmap(response.getBitmap());
                    } else if (userHolder.image == null && response.getBitmap() != null) {
                        userHolder.imageBitmap = response.getBitmap();
                    } else if (userHolder.image != null && response.getBitmap() == null) {
                        userHolder.image.setImageResource(R.drawable.rando_loading);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(RandoPairsAdapter.class, "VolleyError when load rando image: ", url , "with imageSize = ", String.valueOf(imageSize), " , because ", error.getMessage());
                    if (userHolder.image != null) {
                        userHolder.image.setImageResource(R.drawable.rando_error);
                    } else {
                        userHolder.needSetImageError = true;
                    }
                }
            });
        } else {
            Log.e(RandoPairsAdapter.class, "Ignore rando image because url: ", url, " incorrect");
            if (userHolder.image != null) {
                userHolder.image.setImageResource(R.drawable.rando_error);
            } else {
                userHolder.needSetImageError = true;
            }
        }
    }

    private void loadMapImage(final ViewHolder.UserHolder userHolder, final String url, Priority priority) {
        if (URLUtil.isValidUrl(url)) {
            Log.d(RandoPairsAdapter.class, "map url: ", url);
            userHolder.mapContainer = VolleySingleton.getInstance().getImageLoader().get(url, priority, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (userHolder.map != null && response.getBitmap() != null) {
                        userHolder.map.setImageBitmap(response.getBitmap());
                    } else if (userHolder.map == null && response.getBitmap() != null) {
                        userHolder.mapBitmap = response.getBitmap();
                    } else if (userHolder.map != null && response.getBitmap() == null) {
                        userHolder.map.setImageResource(R.drawable.rando_loading);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(RandoPairsAdapter.class, "VolleyError when load map image: ", url , "with imageSize = ", String.valueOf(imageSize), " , because ", error.getMessage());
                    if (userHolder.map != null) {
                        userHolder.map.setImageResource(R.drawable.rando_error);
                    } else {
                        userHolder.needSetMapError = true;
                    }
                }
            });
        } else {
            Log.e(RandoPairsAdapter.class, "Ignore map image because url: ", url, " incorrect");
            if (userHolder.map != null) {
                userHolder.map.setImageResource(R.drawable.rando_error);
            } else {
                userHolder.needSetMapError = true;
            }
        }
    }

    public static class ViewHolder {
        public boolean animationInProgress = false;

        public RandoPair randoPair;

        public ViewSwitcher viewSwitcher;

        public TextView dateTimeView;

        public UserHolder user;
        public UserHolder stranger;
        public LinearLayout reportDialog;

        public RandoPagerAdapter imagePagerAdapter;
        public ViewPager imagePager;
        public RandoPagerAdapter mapPagerAdapter;
        public ViewPager mapPager;
        public ViewSwitcher homeIcon;


        public static class UserHolder {
            public ImageView image;
            public ImageView map;

            public ImageLoader.ImageContainer randoContainer;
            public ImageLoader.ImageContainer mapContainer;

            public boolean needSetImageError = false;
            public boolean needSetMapError = false;

            public boolean needSetPairing = false;

            public Bitmap imageBitmap;
            public Bitmap mapBitmap;
        }
    }
}
