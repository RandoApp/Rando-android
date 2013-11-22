package com.eucsoft.foodex.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.animation.AnimationFactory;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.task.BonAppetitTask;
import com.eucsoft.foodex.view.FoodPager;

import java.util.HashMap;
import java.util.List;

public class FoodPairsAdapter extends BaseAdapter {

    private List<FoodPair> foodPairs;
    private int foodImageSize;

    private int orientation;

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
        FoodDAO foodDAO = new FoodDAO(context);
        foodPairs = foodDAO.getAllFoodPairs();
        foodDAO.close();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int displayWidth = display.getWidth();
        orientation = context.getResources().getConfiguration().orientation;
        foodImageSize = getFoodImageSize(orientation, displayWidth);
        size = foodPairs.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        //Debug.startMethodTracing("hw");
        final ViewHolder holder;

        final FoodPair foodPair = foodPairs.get(position);

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.food_pair_item, container, false);
            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.bonAppetitButton = (ImageButton) convertView.findViewWithTag("bon_appetit_button");
            holder.strangerFoodPager = (FoodPager) convertView.findViewWithTag("stranger");
            holder.userFoodPager = (FoodPager) convertView.findViewWithTag("user");
            holder.viewSwitcher = (ViewSwitcher) convertView.findViewWithTag("viewSwitcher");

            convertView.setTag(holder);
            ViewSwitcher.LayoutParams foodImagesLayout = new ViewSwitcher.LayoutParams(foodImageSize, foodImageSize);
            holder.strangerFoodPager.setLayoutParams(foodImagesLayout);
            holder.userFoodPager.setLayoutParams(foodImagesLayout);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(foodImageSize, foodImageSize);

            holder.strangerFoodImage = (ImageView) holder.strangerFoodPager.findViewWithTag("food");
            holder.strangerMapImage = (ImageView) holder.strangerFoodPager.findViewWithTag("map");
            holder.userFoodImage = (ImageView) holder.userFoodPager.findViewWithTag("food");
            holder.userMapImage = (ImageView) holder.userFoodPager.findViewWithTag("map");

            holder.strangerMapImage.setLayoutParams(lp);
            holder.strangerFoodImage.setLayoutParams(lp);
            holder.userMapImage.setLayoutParams(lp);
            holder.userFoodImage.setLayoutParams(lp);

            View.OnClickListener foodClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.animationInProgress) {
                        if (holder.viewSwitcher.getCurrentView() != holder.strangerFoodPager) {
                            holder.viewSwitcher.showPrevious();
                            holder.isStrangerShown = true;
                            if (foodPair.stranger.isBonAppetit()) {
                                holder.bonAppetitButton.setImageResource(R.drawable.bonappetit2);
                            } else {
                                holder.bonAppetitButton.setImageResource(R.drawable.bonappetit);
                            }
                        } else if (holder.viewSwitcher.getCurrentView() == holder.strangerFoodPager) {
                            holder.viewSwitcher.showNext();
                            holder.isStrangerShown = false;
                            if (foodPair.user.isBonAppetit()) {
                                holder.bonAppetitButton.setImageResource(R.drawable.bonappetit2);
                            } else {
                                holder.bonAppetitButton.setImageResource(R.drawable.bonappetit);
                            }
                        }
                    }
                }
            };
            holder.strangerMapImage.setOnClickListener(foodClickListener);
            holder.strangerFoodImage.setOnClickListener(foodClickListener);
            holder.userMapImage.setOnClickListener(foodClickListener);
            holder.userFoodImage.setOnClickListener(foodClickListener);
        }

        /*holder.userFoodPager.getAdapter().notifyDataSetChanged();*/

        holder.isStrangerShown = true;
        holder.animationInProgress = false;

        holder.strangerFoodImage.setImageResource(R.drawable.bonappetit);

        /*holder.strangerFoodPager.removeAllViews();
        holder.userFoodPager.removeAllViews();*/

        // Log.i(FoodPairsAdapter.class,"size="+size,"pos="+position, "holder="+holder,"foodPairs="+foodPairs);

       /* holder.strangerFoodPager.setUser(foodPair.stranger);
        holder.userFoodPager.setUser(foodPair.user);*/

        /*UpdateFoodPairViewTask updateFoodPairViewTask = new UpdateFoodPairViewTask(position,holder);
        updateFoodPairViewTask.execute();*/

        if (foodPair.stranger.isBonAppetit()) {
            holder.bonAppetitButton.setImageResource(R.drawable.bonappetit2);
        } else {
            holder.bonAppetitButton.setImageResource(R.drawable.bonappetit);
        }

        holder.bonAppetitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isStrangerShown && !foodPair.stranger.isBonAppetit()) {
                    holder.bonAppetitButton.setImageResource(R.drawable.bonappetit2);
                    BonAppetitTask bonAppetitTask = new BonAppetitTask();
                    bonAppetitTask.setTaskResultListener(new TaskResultListener() {
                        @Override
                        public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {

                            switch (taskCode) {
                                case BonAppetitTask.TASK_ID:
                                    if (resultCode != BaseTask.RESULT_OK && holder.isStrangerShown) {
                                        holder.bonAppetitButton.setImageResource(R.drawable.bonappetit);
                                        Toast.makeText(MainActivity.context, R.string.failed_to_set_bon_appetit_for_food, Toast.LENGTH_LONG);
                                    }
                                    break;
                            }
                        }
                    });
                    bonAppetitTask.execute(foodPair);
                }
            }
        });

        final Animation[] leftToRightAnimation = AnimationFactory.flipAnimation(foodImageSize, AnimationFactory.FlipDirection.LEFT_RIGHT, 600, null);
        final Animation[] rightToLeftAnimation = AnimationFactory.flipAnimation(foodImageSize, AnimationFactory.FlipDirection.RIGHT_LEFT, 600, null);

        holder.viewSwitcher.setOutAnimation(leftToRightAnimation[0]);
        holder.viewSwitcher.setInAnimation(leftToRightAnimation[1]);


        /*holder.strangerFoodPager.setOnClickListener(foodClickListener);
        holder.userFoodPager.setOnClickListener(foodClickListener);*/


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
        return convertView;
    }

    private int getFoodImageSize(int orientation, int displayWidth) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return displayWidth / 2 - (Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_LEFT + Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_RIGHT);
        } else {
            return displayWidth - Constants.FOOD_MARGIN_PORTRAIT;
        }
    }

    public static class ViewHolder {
        //flags
        public boolean isStrangerShown = true;
        public boolean animationInProgress = false;

        //views
        public FoodPager strangerFoodPager;
        public FoodPager userFoodPager;
        public ImageButton bonAppetitButton;
        public ViewSwitcher viewSwitcher;

        public ImageView strangerFoodImage;
        public ImageView strangerMapImage;
        public ImageView userFoodImage;
        public ImageView userMapImage;
    }


}
