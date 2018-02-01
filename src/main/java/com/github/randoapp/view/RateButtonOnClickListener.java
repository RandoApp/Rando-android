package com.github.randoapp.view;

import android.content.res.Resources;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.randoapp.R;
import com.github.randoapp.adapter.RandoListAdapter;
import com.github.randoapp.api.API;
import com.github.randoapp.api.beans.Error;
import com.github.randoapp.api.listeners.NetworkResultListener;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.util.Analytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import static android.widget.Toast.makeText;


public class RateButtonOnClickListener implements View.OnClickListener {
    private RandoListAdapter randoListAdapter;
    private RandoListAdapter.RandoViewHolder holder;
    private FirebaseAnalytics firebaseAnalytics;

    public RateButtonOnClickListener(RandoListAdapter randoListAdapter, RandoListAdapter.RandoViewHolder holder, FirebaseAnalytics firebaseAnalytics) {
        this.randoListAdapter = randoListAdapter;
        this.holder = holder;
        this.firebaseAnalytics = firebaseAnalytics;
    }

    @Override
    public void onClick(View v) {
        holder.recycleCircleMenu();
        if (holder.ratingMenu != null) {
            return;
        }
        holder.ratingMenu = new CircleMenu(v.getContext());
        Resources res = v.getResources();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(holder.imageSize, holder.imageSize);
        holder.randoItemLayout.addView(holder.ratingMenu, layoutParams);

        holder.ratingMenu.setMainMenu(res.getColor(R.color.menu_button_color), R.drawable.ic_close_white_24dp, R.drawable.ic_close_white_24dp)
                .addSubMenu(res.getColor(R.color.thumbs_up_down_button_background), R.drawable.ic_thumbs_up_down_white_24dp)
                .addSubMenu(res.getColor(R.color.thumbs_up_button_background), R.drawable.ic_thumb_up_white_24dp)
                .addSubMenu(res.getColor(R.color.thumbs_down_button_background), R.drawable.ic_thumb_down_white_24dp)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        boolean isRatingChanged = false;
                        switch (index) {
                            case 0:
                                Analytics.logRateRandoNormal(firebaseAnalytics);
                                isRatingChanged = 2 != holder.rando.rating;
                                if (isRatingChanged) {
                                    holder.rando.rating = 2;
                                    rateRando(holder, 2);
                                }
                                break;
                            case 1:
                                Analytics.logRateRandoGood(firebaseAnalytics);
                                isRatingChanged = 3 != holder.rando.rating;
                                if (isRatingChanged) {
                                    holder.rando.rating = 3;
                                    rateRando(holder, 3);
                                }
                                break;
                            case 2:
                                Analytics.logRateRandoBad(firebaseAnalytics);
                                isRatingChanged = 1 != holder.rando.rating;
                                if (isRatingChanged) {
                                    holder.rando.rating = 1;
                                    rateRando(holder, 1);
                                }
                                break;
                            default:
                                break;
                        }

                        randoListAdapter.setRatingIcon(holder, isRatingChanged);
                    }

                }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

            @Override
            public void onMenuOpened() {
                //do nothing
            }

            @Override
            public void onMenuClosed() {
                holder.recycleRatingMenu();
                if (holder.circleMenu == null) {
                    holder.image.setAlpha(1f);
                    holder.map.setAlpha(1f);
                }
            }
        }).openMenu();
        holder.image.setAlpha(0.25f);
        holder.map.setAlpha(0.25f);
    }

    private void rateRando(final RandoListAdapter.RandoViewHolder holder, final int newRating) {
        API.rate(holder.rando.randoId, holder.randoItemLayout.getContext(), newRating, new NetworkResultListener(holder.randoItemLayout.getContext()) {
            @Override
            public void onOk() {
                randoListAdapter.changeCursor(RandoDAO.getCursor(holder.image.getContext(), randoListAdapter.isStranger()));
                randoListAdapter.notifyItemChanged(holder.position);
            }

            @Override
            public void onError(Error error) {
                makeText(holder.randoItemLayout.getContext(), "Error setting rating. Please check internet connection.", Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onFail(Error error) {
                makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                        Toast.LENGTH_LONG).show();
                holder.showSpinner(false);
            }

        });
    }

}
