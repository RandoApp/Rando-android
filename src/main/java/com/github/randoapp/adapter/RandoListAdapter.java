package com.github.randoapp.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.animation.AnimationFactory;
import com.github.randoapp.animation.AnimationListenerAdapter;
import com.github.randoapp.animation.AnimatorListenerAdapter;
import com.github.randoapp.api.API;
import com.github.randoapp.api.beans.Error;
import com.github.randoapp.api.listeners.NetworkResultListener;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;
import com.github.randoapp.network.VolleySingleton;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.BitmapUtil;
import com.github.randoapp.util.NetworkUtil;
import com.github.randoapp.view.RoundProgress;
import com.github.randoapp.view.UnwantedRandoView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import static android.widget.Toast.makeText;
import static com.android.volley.Request.Priority;

public class RandoListAdapter extends BaseAdapter {

    private boolean isStranger;
    private FirebaseAnalytics firebaseAnalytics;
    private List<Rando> randos;
    private int imageSize;
    private Context context;

    private int size;

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return randos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public RandoListAdapter(Context context, boolean isStranger, FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
        this.isStranger = isStranger;
        this.context = context;
        initData();
    }

    private void initData() {
        if (isStranger) {
            randos = RandoDAO.getAllInRandos(context);
        } else {
            randos = RandoDAO.getAllOutRandosWithUploadQueue(context);
        }
        size = randos.size();
    }

    @Override
    public void notifyDataSetChanged() {
        initData();
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        final ViewHolder holder;

        if (imageSize == 0) {
            imageSize = getRandoImageSize(container);
        }

        Log.d(RandoListAdapter.class, "isStranger", String.valueOf(isStranger), "Size:", String.valueOf(size), "Position", String.valueOf(position));

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rando_item, container, false);
            holder = createHolder(convertView);
            addListenersToHolder(holder);
        }

        recycle(holder);
        holder.rando = randos.get(position);
        setRatingIcon(holder);
        loadImages(convertView.getContext(), holder, holder.rando);

        if (holder.rando.isUnwanted()) {
            UnwantedRandoView unwantedRandoView = new UnwantedRandoView(convertView.getContext());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageSize, imageSize);
            layoutParams.setMargins(convertView.getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left),
                    convertView.getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_top),
                    convertView.getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_right), 0);
            //insert Unwanted view at index 1, right after "view_switcher"
            holder.randoItemLayout.addView(unwantedRandoView, 1, layoutParams);
            holder.unwantedRandoView = unwantedRandoView;
        } else {
            if (holder.rando.toUpload) {
                RoundProgress progressBar = new RoundProgress(convertView.getContext(), (float) (container.getWidth() / 2.0 - convertView.getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left) * 0.6) - 3);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(container.getWidth(), container.getWidth());
                holder.randoItemLayout.addView(progressBar, 1, layoutParams);
                holder.uploadingProgress = progressBar;
            } else {
                setAnimations(holder);
            }
        }
        return convertView;
    }

    public int getPositionOfRando(Rando rando) {
        if (rando == null) {
            return 0;
        }
        for (int i = 0; i < randos.size(); i++) {
            if (randos.get(i).randoId.equals(rando.randoId)) {
                return i;
            }
        }
        return 0;
    }

    private ViewHolder createHolder(View convertView) {
        ViewHolder holder = new ViewHolder();

        holder.randoItemLayout = (RelativeLayout) convertView.findViewWithTag("rando_item_layout");

        holder.viewSwitcher = (ViewSwitcher) convertView.findViewWithTag("viewSwitcher");

        holder.image = (RoundedImageView) convertView.findViewWithTag("image");
        holder.image.setTag(null);
        holder.map = (RoundedImageView) convertView.findViewWithTag("map");
        ViewSwitcher.LayoutParams randoImagesLayout = new ViewSwitcher.LayoutParams(imageSize, imageSize);
        holder.image.setLayoutParams(randoImagesLayout);
        holder.map.setLayoutParams(randoImagesLayout);
        holder.rateButton = (ImageView) convertView.findViewWithTag("rating");

        convertView.setTag(holder);
        return holder;
    }

    private void addListenersToHolder(final ViewHolder holder) {
        View.OnClickListener randoOnClickListener = createRandoOnClickListener(holder);
        holder.image.setOnClickListener(randoOnClickListener);
        holder.map.setOnClickListener(randoOnClickListener);
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                recycleRatingMenu(holder);
                if (holder.circleMenu == null && !holder.rando.isUnwanted()) {
                    Resources res = v.getResources();
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (imageSize * 0.9), (int) (imageSize * 0.9));
                    layoutParams.topMargin = (int) (imageSize * 0.05) + res.getDimensionPixelSize(R.dimen.rando_padding_portrait_column_top);
                    layoutParams.leftMargin = (int) (imageSize * 0.05) + res.getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left);

                    holder.circleMenu = new CircleMenu(v.getContext());
                    holder.randoItemLayout.addView(holder.circleMenu, layoutParams);

                    holder.circleMenu.setMainMenu(res.getColor(R.color.menu_button_color), R.drawable.ic_close_white_24dp, R.drawable.ic_close_white_24dp);
                    if (!holder.rando.toUpload) {
                        holder.circleMenu.addSubMenu(res.getColor(R.color.share_menu_button_color), R.drawable.ic_share_white_24dp);
                    }

                    holder.circleMenu.addSubMenu(res.getColor(R.color.delete_menu_button_color), R.drawable.ic_delete_white_24dp);
                    if (isStranger) {
                        holder.circleMenu.addSubMenu(res.getColor(R.color.report_menu_button_color), R.drawable.ic_flag_white_24dp);
                    }
                    holder.circleMenu.setOnMenuSelectedListener(new OnMenuSelectedListener() {

                        @Override
                        public void onMenuSelected(int index) {
                            if (index == 0 && holder.rando.toUpload) {
                                deleteRando(holder);
                                return;
                            }
                            switch (index) {
                                case 0:
                                    shareRando(holder);
                                    break;
                                case 1:
                                    deleteRando(holder);
                                    break;
                                case 2:
                                    reportRando(holder);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

                        @Override
                        public void onMenuOpened() {
                            //do nothing
                        }

                        @Override
                        public void onMenuClosed() {
                            recycleCircleMenu(holder);
                            if (holder.ratingMenu == null) {
                                holder.image.setAlpha(1f);
                                holder.map.setAlpha(1f);
                            }
                        }

                    });
                    holder.image.setAlpha(0.25f);
                    holder.map.setAlpha(0.25f);
                    holder.circleMenu.openMenu();
                }
                return true;
            }
        };
        holder.image.setOnLongClickListener(onLongClickListener);
        holder.map.setOnLongClickListener(onLongClickListener);

        if (isStranger) {
            holder.rateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recycleCircleMenu(holder);
                    if (holder.ratingMenu != null) {
                        return;
                    }
                    holder.ratingMenu = new CircleMenu(v.getContext());
                    Resources res = v.getResources();
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (imageSize * 0.9), (int) (imageSize * 0.9));
                    layoutParams.topMargin = (int) (imageSize * 0.05) + res.getDimensionPixelSize(R.dimen.rando_padding_portrait_column_top);
                    layoutParams.leftMargin = (int) (imageSize * 0.05) + res.getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left);
                    holder.randoItemLayout.addView(holder.ratingMenu, layoutParams);

                    holder.ratingMenu.setMainMenu(res.getColor(R.color.menu_button_color), R.drawable.ic_close_white_24dp, R.drawable.ic_close_white_24dp)
                            .addSubMenu(res.getColor(R.color.thumbs_up_down_button_background), R.drawable.ic_thumbs_up_down_white_24dp)
                            .addSubMenu(res.getColor(R.color.thumbs_up_button_background), R.drawable.ic_thumb_up_white_24dp)
                            .addSubMenu(res.getColor(R.color.thumbs_down_button_background), R.drawable.ic_thumb_down_white_24dp)
                            .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                                @Override
                                public void onMenuSelected(int index) {
                                    switch (index) {
                                        case 0:
                                            Analytics.logRateRandoNormal(firebaseAnalytics);
                                            holder.rando.rating = 2;
                                            rateRando(holder, 2);
                                            break;
                                        case 1:
                                            Analytics.logRateRandoGood(firebaseAnalytics);
                                            holder.rando.rating = 3;
                                            rateRando(holder, 3);
                                            break;
                                        case 2:
                                            Analytics.logRateRandoBad(firebaseAnalytics);
                                            holder.rando.rating = 1;
                                            rateRando(holder, 1);
                                            break;
                                        default:
                                            break;
                                    }
                                }

                            }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

                        @Override
                        public void onMenuOpened() {
                            //do nothing
                        }

                        @Override
                        public void onMenuClosed() {
                            setRatingIcon(holder);
                            recycleRatingMenu(holder);
                            if (holder.circleMenu == null) {
                                holder.image.setAlpha(1f);
                                holder.map.setAlpha(1f);
                            }
                        }
                    }).openMenu();
                    holder.image.setAlpha(0.25f);
                    holder.map.setAlpha(0.25f);
                }
            });
        }
    }

    private void deleteRando(final ViewHolder holder) {
        Analytics.logDeleteRando(firebaseAnalytics);
        if (NetworkUtil.isOnline(holder.randoItemLayout.getContext()) || holder.rando.toUpload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.randoItemLayout.getContext());
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (holder.rando.toUpload) {
                        RandoDAO.deleteRandoToUploadById(holder.randoItemLayout.getContext(), holder.rando.id);
                        notifyDataSetChanged();
                        return;
                    }
                    try {
                        showSpinner(holder, true);
                        API.delete(holder.rando.randoId, holder.randoItemLayout.getContext(), new NetworkResultListener(context) {
                            @Override
                            public void onOk() {
                                RandoDAO.deleteRandoByRandoId(holder.randoItemLayout.getContext(), holder.rando.randoId);
                                notifyDataSetChanged();
                                makeText(holder.randoItemLayout.getContext(), R.string.rando_deleted,
                                        Toast.LENGTH_LONG).show();
                                showSpinner(holder, false);
                            }

                            @Override
                            protected void onFail(Error error) {
                                makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                                        Toast.LENGTH_LONG).show();
                                showSpinner(holder, false);
                            }
                        });
                    } catch (Exception e) {
                        makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                                Toast.LENGTH_LONG).show();
                        showSpinner(holder, false);
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, null).setTitle(R.string.delete_rando).setMessage(R.string.delete_rando_confirm).create().show();
            return;
        } else {
            makeText(holder.randoItemLayout.getContext(), R.string.error_no_network,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void reportRando(final ViewHolder holder) {
        Analytics.logReportRando(firebaseAnalytics);
        if (NetworkUtil.isOnline(holder.randoItemLayout.getContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.randoItemLayout.getContext());
            builder.setPositiveButton(R.string.report, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        showSpinner(holder, true);
                        API.report(holder.rando.randoId, holder.randoItemLayout.getContext(), new NetworkResultListener(context) {
                            @Override
                            public void onOk() {
                                RandoDAO.deleteRandoByRandoId(holder.randoItemLayout.getContext(), holder.rando.randoId);
                                notifyDataSetChanged();
                                makeText(holder.randoItemLayout.getContext(), R.string.rando_reported,
                                        Toast.LENGTH_LONG).show();
                                showSpinner(holder, false);
                            }

                            @Override
                            protected void onFail(Error error) {
                                makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                                        Toast.LENGTH_LONG).show();
                                showSpinner(holder, false);
                            }
                        });
                    } catch (Exception e) {
                        makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                                Toast.LENGTH_LONG).show();
                        showSpinner(holder, false);
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, null).setTitle(R.string.report_rando).setMessage(R.string.report_rando_confirm).create().show();
            return;
        } else {
            makeText(holder.randoItemLayout.getContext(), R.string.error_no_network,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showSpinner(ViewHolder holder, boolean show) {
        if (show) {
            holder.spinner = new ProgressBar(holder.randoItemLayout.getContext(), null, android.R.attr.progressBarStyleLarge);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            holder.spinner.setIndeterminate(true);
            holder.randoItemLayout.addView(holder.spinner, holder.randoItemLayout.getChildCount(), layoutParams);
        } else if (holder.spinner != null) {
            holder.randoItemLayout.removeView(holder.spinner);
            holder.spinner = null;
        }
    }

    private View.OnClickListener createRandoOnClickListener(final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (holder.rando.toUpload) {
                    LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View uploadingToast = inflater.inflate(R.layout.uploading_toast, null);

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    ((FrameLayout) (holder.image.getParent())).addView(uploadingToast, 1, layoutParams);
                    Animator alphaAnimator = ObjectAnimator.ofFloat(uploadingToast, "alpha", 1, 0.2f).setDuration(700);
                    alphaAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (uploadingToast != null) {
                                ((FrameLayout) (holder.image.getParent())).removeView(uploadingToast);
                            }
                        }
                    });
                    alphaAnimator.setStartDelay(1500);
                    alphaAnimator.start();
                    return;
                }
                if (holder.rando.isUnwanted()) {
                    Analytics.logClickUnwantedRando(firebaseAnalytics);
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setNegativeButton(R.string.delete_rando, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Analytics.logDeleteUnwantedRandoDialog(firebaseAnalytics);
                            try {
                                showSpinner(holder, true);
                                API.delete(holder.rando.randoId, v.getContext(), new NetworkResultListener(context) {
                                    @Override
                                    public void onOk() {
                                        RandoDAO.deleteRandoByRandoId(v.getContext(), holder.rando.randoId);
                                        notifyDataSetChanged();
                                        makeText(v.getContext(), R.string.rando_deleted,
                                                Toast.LENGTH_LONG).show();
                                        showSpinner(holder, false);
                                    }

                                    @Override
                                    protected void onFail(Error error) {
                                        makeText(v.getContext(), R.string.error_unknown_err,
                                                Toast.LENGTH_LONG).show();
                                        showSpinner(holder, false);
                                    }
                                });
                            } catch (Exception e) {
                                makeText(v.getContext(), R.string.error_unknown_err,
                                        Toast.LENGTH_LONG).show();
                                showSpinner(holder, false);
                            }
                        }
                    });
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Analytics.logCancelUnwantedRandoDialog(firebaseAnalytics);
                            return;
                        }
                    }).setTitle(R.string.rando_excluded).setMessage(R.string.rando_excluded_text).create().show();

                } else {
                    if (holder.animationInProgress) return;
                    if (holder.circleMenu !=null) {
                        holder.circleMenu.closeMenu();
                        //recycleCircleMenu(holder);
                        return;
                    }
                    if (holder.ratingMenu!=null) {
                        holder.ratingMenu.closeMenu();
                        //recycleRatingMenu(holder);
                        return;
                    }
                    if (isStranger) {
                        Analytics.logTapStrangerRando(firebaseAnalytics);
                    } else {
                        Analytics.logTapOwnRando(firebaseAnalytics);
                    }
                    holder.viewSwitcher.showNext();
                    holder.isMap = !holder.isMap;
                    if (holder.rando.isMapEmpty() && holder.isMap && holder.landingImage == null) {
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (imageSize * 0.05f), (int) (imageSize * 0.05f));

                        final Animation anim = AnimationUtils.loadAnimation(holder.viewSwitcher.getContext(), R.anim.flow_map);
                        anim.setFillAfter(true);
                        final ImageView floatingRando = new android.support.v7.widget.AppCompatImageView(holder.randoItemLayout.getContext()) {

                            @Override
                            protected void onAnimationEnd() {
                                super.onAnimationEnd();
                                holder.landingImage.clearAnimation();
                                holder.landingImage.startAnimation(anim);
                            }
                        };
                        floatingRando.setImageResource(R.drawable.ic_launcher);
                        holder.landingImage = floatingRando;

                        layoutParams.leftMargin = (int) (imageSize * 0.09f);
                        layoutParams.topMargin = (int) (imageSize * 0.20f);
                        ((FrameLayout) (holder.map.getParent())).addView(floatingRando, 1, layoutParams);

                        floatingRando.startAnimation(anim);
                    }
                }
            }
        };
    }

    private void rateRando(final ViewHolder holder, final int newRating) {
        Analytics.logShareRando(firebaseAnalytics);
        API.rate(holder.rando.randoId, holder.randoItemLayout.getContext(), newRating, new NetworkResultListener(context) {
            @Override
            public void onOk() {
                Rando rando = RandoDAO.getRandoByRandoId(holder.randoItemLayout.getContext(), holder.rando.randoId);
                rando.rating = newRating;
                holder.rando.rating = newRating;
                RandoDAO.updateRando(holder.randoItemLayout.getContext(), rando);
                holder.rando = rando;
                setRatingIcon(holder);
            }

            @Override
            public void onError(Error error) {
                makeText(holder.randoItemLayout.getContext(), "Error setting rating. Please check internet connection.", Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onFail(Error error) {
                makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                        Toast.LENGTH_LONG).show();
                showSpinner(holder, false);
            }

        });
    }

    private void setRatingIcon(ViewHolder holder) {

        switch (holder.rando.rating) {
            case 0:
                if(!isStranger)
                    holder.rateButton.setVisibility(View.GONE);
                break;
            case 1:
                holder.rateButton.setVisibility(View.VISIBLE);
                holder.rateButton.setImageResource(R.drawable.ic_thumb_down_white_24dp);
                holder.rateButton.setBackgroundResource(R.drawable.round_button_red_background);
                break;
            case 2:
                holder.rateButton.setVisibility(View.VISIBLE);
                holder.rateButton.setImageResource(R.drawable.ic_thumbs_up_down_white_24dp);
                holder.rateButton.setBackgroundResource(R.drawable.round_button_blue_background);
                break;
            case 3:
                holder.rateButton.setVisibility(View.VISIBLE);
                holder.rateButton.setImageResource(R.drawable.ic_thumb_up_white_24dp);
                holder.rateButton.setBackgroundResource(R.drawable.round_button_green_background);
                break;
            default:
                holder.rateButton.setVisibility(View.VISIBLE);
                holder.rateButton.setImageResource(R.drawable.ic_thumb_up_white_24dp);
                holder.rateButton.setBackgroundResource(R.drawable.round_button_grey_background);
                break;
        }
    }

    private void shareRando(final ViewHolder holder) {
        Analytics.logShareRando(firebaseAnalytics);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, holder.randoItemLayout.getContext().getResources().getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, holder.randoItemLayout.getContext().getResources().getString(R.string.share_text) + " " + String.format(Constants.SHARE_URL, holder.rando.randoId));
        holder.randoItemLayout.getContext().startActivity(Intent.createChooser(shareIntent, "Share Rando using"));
    }

    private void recycle(ViewHolder holder) {
        holder.animationInProgress = false;

        cancelRequests(holder);

        recycleViewSwitcher(holder.viewSwitcher);

        holder.image.setImageBitmap(null);
        holder.map.setImageBitmap(null);
        holder.isMap = false;

        holder.image.setAlpha(1f);
        holder.map.setAlpha(1f);
        showSpinner(holder, false);

        recycleCircleMenu(holder);
        recycleRatingMenu(holder);

        if (holder.unwantedRandoView != null) {
            holder.unwantedRandoView.clearAnimation();
            holder.randoItemLayout.removeView(holder.unwantedRandoView);
            holder.unwantedRandoView = null;
        }

        if (holder.uploadingProgress != null) {
            holder.uploadingProgress.clearAnimation();
            holder.randoItemLayout.removeView(holder.uploadingProgress);
            holder.uploadingProgress = null;
        }

        if (holder.landingImage != null) {
            holder.landingImage.clearAnimation();
            ((FrameLayout) (holder.map.getParent())).removeView(holder.landingImage);
            holder.landingImage = null;
        }

        holder.rando = null;
    }

    private void recycleCircleMenu(ViewHolder holder) {
        if (holder.circleMenu != null) {
            holder.circleMenu.closeMenu();
            holder.randoItemLayout.removeView(holder.circleMenu);
            holder.circleMenu = null;
        }
    }

    private void recycleRatingMenu(ViewHolder holder) {
        if (holder.ratingMenu != null) {
            holder.ratingMenu.closeMenu();
            holder.randoItemLayout.removeView(holder.ratingMenu);
            holder.ratingMenu = null;
        }
    }

    private void recycleViewSwitcher(ViewSwitcher viewSwitcher) {
        //disable animation for immediately and undetectable switching to zero child:
        viewSwitcher.clearAnimation();
        viewSwitcher.setInAnimation(null);
        viewSwitcher.setOutAnimation(null);
        viewSwitcher.setDisplayedChild(0);
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

    private int getRandoImageSize(ViewGroup container) {
        return container.getWidth() - container.getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left)
                - container.getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_right);
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

    private void loadImages(final Context context, final ViewHolder holder, final Rando rando) {
        if (rando.imageURLSize.small != null && !URLUtil.isNetworkUrl(rando.imageURLSize.small) && !rando.imageURLSize.small.isEmpty()) {
            loadFile(holder, rando.imageURL);
            return;
        }

        loadImage(context, holder, rando.getBestImageUrlBySize(imageSize), Priority.HIGH);
        if (rando.isMapEmpty()) {
            holder.map.setImageResource(R.drawable.flat_map_for_vec);
        } else {
            loadMapImage(context, holder, rando.getBestMapUrlBySize(imageSize), Priority.LOW);
        }
    }

    private void loadFile(final ViewHolder holder, final String filePath) {
        if (holder.image != null) {
            holder.image.setImageBitmap(BitmapUtil.decodeSampledBitmap(filePath, imageSize, imageSize));
        }
        if (holder.map != null) {
            holder.map.setImageResource(R.drawable.rando_pairing);
        }
    }

    private void loadImage(final Context context, final ViewHolder viewHolder, final String url, Priority priority) {
        if (TextUtils.isEmpty(url)) {
            if (viewHolder.image != null) {
                viewHolder.image.setImageResource(R.drawable.rando_pairing);
            }
            return;
        }

        if (URLUtil.isValidUrl(url)) {
            Log.d(RandoListAdapter.class, "image url: ", url);
            viewHolder.randoContainer = VolleySingleton.getInstance(context).getImageLoader().get(url, new ImageLoader.ImageListener() {
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
                    Log.e(RandoListAdapter.class, "VolleyError when load rando image: ", url, "with imageSize = ", String.valueOf(imageSize), " , because ", error.getMessage());
                    if (viewHolder.image != null) {
                        viewHolder.image.setImageResource(R.drawable.rando_error);
                    } else {
                        viewHolder.needSetImageError = true;
                    }
                }
            }, ImageView.ScaleType.CENTER, imageSize, imageSize, priority);
        } else {
            Log.e(RandoListAdapter.class, "Ignore rando image because url: ", url, " incorrect");
            if (viewHolder.image != null) {
                viewHolder.image.setImageResource(R.drawable.rando_error);
            } else {
                viewHolder.needSetImageError = true;
            }
        }
    }

    private void loadMapImage(final Context context, final ViewHolder viewHolder, final String url, Priority priority) {
        if (URLUtil.isValidUrl(url)) {
            Log.d(RandoListAdapter.class, "map url: ", url);
            viewHolder.mapContainer = VolleySingleton.getInstance(context).getImageLoader().get(url, new ImageLoader.ImageListener() {
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
                    Log.e(RandoListAdapter.class, "VolleyError when load map image: ", url, "with imageSize = ", String.valueOf(imageSize), " , because ", error.getMessage());
                    if (viewHolder.map != null) {
                        viewHolder.map.setImageResource(R.drawable.rando_error);
                    } else {
                        viewHolder.needSetMapError = true;
                    }
                }
            }, ImageView.ScaleType.CENTER, imageSize, imageSize, priority);
        } else {
            Log.d(RandoListAdapter.class, "Ignore map image because url: ", url, " incorrect");
            if (viewHolder.map != null) {
                viewHolder.map.setImageResource(R.drawable.rando_error);
            } else {
                viewHolder.needSetMapError = true;
            }
        }
    }

    public static class ViewHolder {
        public Rando rando;

        public RelativeLayout randoItemLayout;

        public UnwantedRandoView unwantedRandoView;

        public RoundProgress uploadingProgress;
        public ImageView landingImage;

        public boolean animationInProgress = false;

        public ViewSwitcher viewSwitcher;
        public RoundedImageView image;
        public RoundedImageView map;
        public boolean isMap;

        public CircleMenu circleMenu;
        public ImageView rateButton;
        public CircleMenu ratingMenu;

        public ProgressBar spinner;

        public ImageLoader.ImageContainer randoContainer;
        public ImageLoader.ImageContainer mapContainer;

        public boolean needSetImageError = false;
        public boolean needSetMapError = false;
    }
}
