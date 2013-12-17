package com.eucsoft.foodex.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.animation.AnimationFactory;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.task.BonAppetitTask;

import java.util.HashMap;
import java.util.List;

public class FoodPairsAdapter extends BaseAdapter {

    private List<FoodPair> foodPairs;
    private int foodImageSize;

    private int size;

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return foodPairs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public FoodPairsAdapter(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int displayWidth = display.getWidth();
        int orientation = context.getResources().getConfiguration().orientation;
        foodImageSize = getFoodImageSize(orientation, displayWidth);
        initData();
    }

    private void initData() {
        FoodDAO foodDAO = new FoodDAO(App.context);
        foodPairs = foodDAO.getAllFoodPairs();
        foodDAO.close();
        size = foodPairs.size();
    }

    @Override
    public void notifyDataSetChanged() {
        initData();
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final ViewHolder holder;

        final FoodPair foodPair = foodPairs.get(position);

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.food_pair_item, container, false);
            holder = createHolder(convertView);
            addListenersToHolder(holder, foodPair);
        }

        recycle(holder, foodPair);
        loadImages(holder, foodPair);
        setAnimations(holder);
        return convertView;
    }

    private ViewHolder createHolder(View convertView) {
        ViewHolder holder = new ViewHolder();

        holder.user = new ViewHolder.UserHolder();
        holder.stranger = new ViewHolder.UserHolder();

        holder.bonAppetitButton = (ImageButton) convertView.findViewWithTag("bon_appetit_button");
        holder.viewSwitcher = (ViewSwitcher) convertView.findViewWithTag("viewSwitcher");

        holder.stranger.foodPager = (ViewPager) convertView.findViewWithTag("stranger");
        holder.user.foodPager = (ViewPager) convertView.findViewWithTag("user");

        ViewSwitcher.LayoutParams foodImagesLayout = new ViewSwitcher.LayoutParams(foodImageSize, foodImageSize);
        holder.stranger.foodPager.setLayoutParams(foodImagesLayout);
        holder.user.foodPager.setLayoutParams(foodImagesLayout);

        holder.user.foodMapPagerAdatper = new FoodMapSwitcherAdapter(holder.user);
        holder.user.foodPager.setAdapter(holder.user.foodMapPagerAdatper);

        holder.stranger.foodMapPagerAdatper = new FoodMapSwitcherAdapter(holder.stranger);
        holder.stranger.foodPager.setAdapter(holder.stranger.foodMapPagerAdatper);

        convertView.setTag(holder);

        return holder;
    }

    private void addListenersToHolder(final ViewHolder holder, final FoodPair foodPair) {
        View.OnClickListener foodOnClickListener = createFoodOnClickListener(holder, foodPair);
        holder.user.foodMapPagerAdatper.setOnClickListener(foodOnClickListener);
        holder.stranger.foodMapPagerAdatper.setOnClickListener(foodOnClickListener);

        holder.bonAppetitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.stranger.foodPager.isShown() && !foodPair.stranger.isBonAppetit()) {
                    holder.bonAppetitButton.setImageResource(R.drawable.bonappetit2);
                    BonAppetitTask bonAppetitTask = new BonAppetitTask();
                    bonAppetitTask.setTaskResultListener(new TaskResultListener() {
                        @Override
                        public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {

                            switch (taskCode) {
                                case BonAppetitTask.TASK_ID:
                                    if (resultCode != BaseTask.RESULT_OK && holder.stranger.foodPager.isShown()) {
                                        holder.bonAppetitButton.setImageResource(R.drawable.bonappetit);
                                        if (data.get(Constants.ERROR) != null) {
                                            Toast.makeText(App.context, (CharSequence) data.get(Constants.ERROR), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(App.context, R.string.failed_to_set_bon_appetit_for_food, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    break;
                            }
                        }
                    });
                    bonAppetitTask.execute(foodPair);
                }
            }
        });
    }

    private View.OnClickListener createFoodOnClickListener(final ViewHolder holder, final FoodPair foodPair) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.animationInProgress) {
                    ViewPager oldFoodMapView = (ViewPager) holder.viewSwitcher.getCurrentView();
                    holder.viewSwitcher.showNext();
                    ViewPager newFoodMapView = (ViewPager) holder.viewSwitcher.getCurrentView();
                    newFoodMapView.setCurrentItem(oldFoodMapView.getCurrentItem());

                    if (holder.stranger.foodPager.getVisibility() == View.VISIBLE) {
                        if (foodPair.stranger.isBonAppetit()) {
                            holder.bonAppetitButton.setImageResource(R.drawable.bonappetit2);
                        } else {
                            holder.bonAppetitButton.setImageResource(R.drawable.bonappetit);
                        }
                    } else {
                        if (foodPair.user.isBonAppetit()) {
                            holder.bonAppetitButton.setImageResource(R.drawable.bonappetit2);
                        } else {
                            holder.bonAppetitButton.setImageResource(R.drawable.bonappetit);
                        }
                    }
                }
            }
        };
    }

    private void recycle(ViewHolder holder, FoodPair foodPair) {
        holder.animationInProgress = false;

        if (foodPair.stranger.isBonAppetit()) {
            holder.bonAppetitButton.setImageResource(R.drawable.bonappetit2);
        } else {
            holder.bonAppetitButton.setImageResource(R.drawable.bonappetit);
        }

        if (holder.stranger.foodImage != null && holder.user.foodImage != null
                && holder.stranger.mapImage != null && holder.user.mapImage != null) {
            holder.user.foodMapPagerAdatper.recycle(holder.user.foodImage, holder.user.mapImage);
            holder.stranger.foodMapPagerAdatper.recycle(holder.stranger.foodImage, holder.stranger.mapImage);
        }

        if(holder.stranger.foodContainer != null){
            holder.stranger.foodContainer.cancelRequest();
            holder.stranger.foodContainer = null;
        }
        if(holder.stranger.mapContainer!= null){
            holder.stranger.mapContainer.cancelRequest();
            holder.stranger.mapContainer = null;
        }
        if(holder.user.foodContainer != null){
            holder.user.foodContainer.cancelRequest();
            holder.user.foodContainer = null;
        }
        if(holder.user.mapContainer!= null){
            holder.user.mapContainer.cancelRequest();
            holder.user.mapContainer = null;
        }

        setPagesToDefault(holder);
    }

    private void setPagesToDefault(ViewHolder holder) {
        holder.user.foodPager.setCurrentItem(0);
        holder.stranger.foodPager.setCurrentItem(0);
    }

    private int getFoodImageSize(int orientation, int displayWidth) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return displayWidth / 2 - (Constants.FOOD_PADDING_LANDSCAPE_COLUMN_LEFT + Constants.FOOD_PADDING_LANDSCAPE_COLUMN_RIGHT);
        } else {
            return displayWidth - Constants.FOOD_MARGIN_PORTRAIT;
        }
    }

    private void setAnimations(final ViewHolder holder) {
        final Animation[] leftToRightAnimation = AnimationFactory.flipAnimation(foodImageSize, AnimationFactory.FlipDirection.LEFT_RIGHT, 600, null);
        final Animation[] rightToLeftAnimation = AnimationFactory.flipAnimation(foodImageSize, AnimationFactory.FlipDirection.RIGHT_LEFT, 600, null);

        holder.viewSwitcher.setOutAnimation(leftToRightAnimation[0]);
        holder.viewSwitcher.setInAnimation(leftToRightAnimation[1]);

        Animation.AnimationListener outAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                holder.animationInProgress = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
        leftToRightAnimation[0].setAnimationListener(outAnimationListener);
        leftToRightAnimation[1].setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                holder.viewSwitcher.setOutAnimation(rightToLeftAnimation[0]);
                holder.viewSwitcher.setInAnimation(rightToLeftAnimation[1]);
                holder.animationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        rightToLeftAnimation[0].setAnimationListener(outAnimationListener);
        rightToLeftAnimation[1].setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                holder.viewSwitcher.setOutAnimation(leftToRightAnimation[0]);
                holder.viewSwitcher.setInAnimation(leftToRightAnimation[1]);
                holder.animationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void loadImages(final ViewHolder holder, FoodPair foodPair){
        if (!TextUtils.isEmpty(foodPair.stranger.foodURL))
        {
            holder.stranger.foodContainer = App.getInstance(App.context).getImageLoader(). get(foodPair.stranger.foodURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (holder.stranger.foodImage != null) {
                        holder.stranger.foodImage.setImageBitmap(response.getBitmap());
                    } else {
                        holder.stranger.foodBitmap = response.getBitmap();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

        if (!TextUtils.isEmpty(foodPair.stranger.mapURL))
        {
            holder.stranger.mapContainer = App.getInstance(App.context).getImageLoader().get(foodPair.stranger.mapURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (holder.stranger.mapImage !=null){
                        holder.stranger.mapImage.setImageBitmap(response.getBitmap());
                    } else {
                        holder.stranger.mapBitmap = response.getBitmap();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

        if (!TextUtils.isEmpty(foodPair.user.foodURL))
        {
            holder.user.foodContainer = App.getInstance(App.context).getImageLoader().get(foodPair.user.foodURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (holder.user.foodImage !=null){
                        holder.user.foodImage.setImageBitmap(response.getBitmap());
                    } else {
                        holder.user.foodBitmap = response.getBitmap();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }

        if (!TextUtils.isEmpty(foodPair.user.mapURL))
        {
            holder.user.mapContainer = App.getInstance(App.context).getImageLoader().get(foodPair.user.mapURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (holder.user.mapImage !=null){
                        holder.user.mapImage.setImageBitmap(response.getBitmap());
                    } else {
                        holder.user.mapBitmap = response.getBitmap();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    public static class ViewHolder {
        public boolean animationInProgress = false;

        public ImageButton bonAppetitButton;
        public ViewSwitcher viewSwitcher;

        public UserHolder user;
        public UserHolder stranger;

        public static class UserHolder {
            public ViewPager foodPager;
            public FoodMapSwitcherAdapter foodMapPagerAdatper;

            public ImageView foodImage;
            public ImageView mapImage;

            public ImageLoader.ImageContainer foodContainer;
            public ImageLoader.ImageContainer mapContainer;

            public Bitmap foodBitmap;
            public Bitmap mapBitmap;
        }
    }
}
