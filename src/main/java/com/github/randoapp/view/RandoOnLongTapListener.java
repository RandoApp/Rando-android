package com.github.randoapp.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.adapter.RandoListAdapter;
import com.github.randoapp.api.API;
import com.github.randoapp.api.beans.Error;
import com.github.randoapp.api.listeners.NetworkResultListener;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.NetworkUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import static android.widget.Toast.makeText;


public class RandoOnLongTapListener implements View.OnLongClickListener {
    private RandoListAdapter randoListAdapter;
    private RandoListAdapter.RandoViewHolder holder;
    private FirebaseAnalytics firebaseAnalytics;

    public RandoOnLongTapListener(RandoListAdapter randoListAdapter, RandoListAdapter.RandoViewHolder holder, FirebaseAnalytics firebaseAnalytics) {
        this.randoListAdapter = randoListAdapter;
        this.holder = holder;
        this.firebaseAnalytics = firebaseAnalytics;
    }

    @Override
    public boolean onLongClick(View v) {
        holder.recycleRatingMenu();
        if (holder.circleMenu == null && !holder.rando.isUnwanted()) {
            Resources res = v.getResources();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(holder.imageSize, holder.imageSize);

            holder.circleMenu = new CircleMenu(v.getContext());
            holder.randoItemLayout.addView(holder.circleMenu, layoutParams);

            holder.circleMenu.setMainMenu(res.getColor(R.color.menu_button_color), R.drawable.ic_close_white_24dp, R.drawable.ic_close_white_24dp);
            if (!holder.rando.isToUpload()) {
                holder.circleMenu.addSubMenu(res.getColor(R.color.share_menu_button_color), R.drawable.ic_share_white_24dp);
            }

            holder.circleMenu.addSubMenu(res.getColor(R.color.delete_menu_button_color), R.drawable.ic_delete_white_24dp);
            if (holder.rando.status.equals(Rando.Status.IN)) {
                holder.circleMenu.addSubMenu(res.getColor(R.color.report_menu_button_color), R.drawable.ic_flag_white_24dp);
            }
            holder.circleMenu.setOnMenuSelectedListener(new OnMenuSelectedListener() {

                @Override
                public void onMenuSelected(int index) {
                    if (index == 0 && holder.rando.isToUpload()) {
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
                    holder.recycleCircleMenu();
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

    private void deleteRando(final RandoListAdapter.RandoViewHolder holder) {
        Analytics.logDeleteRando(firebaseAnalytics);
        if (NetworkUtil.isOnline(holder.randoItemLayout.getContext()) || holder.rando.isToUpload()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.randoItemLayout.getContext());
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (holder.rando.isToUpload()) {
                        RandoDAO.deleteRandoToUploadById(holder.randoItemLayout.getContext(), holder.rando.id);
                        randoListAdapter.changeCursor(RandoDAO.getCursor(holder.image.getContext(), randoListAdapter.isStranger()));
                        randoListAdapter.notifyItemRemoved(holder.position);
                        return;
                    }
                    try {
                        holder.showSpinner(true);
                        API.delete(holder.rando.randoId, holder.randoItemLayout.getContext(), new NetworkResultListener(holder.randoItemLayout.getContext()) {
                            @Override
                            public void onOk() {
                                randoListAdapter.changeCursor(RandoDAO.getCursor(context, randoListAdapter.isStranger()));
                                randoListAdapter.notifyItemRemoved(holder.position);

                                makeText(holder.randoItemLayout.getContext(), R.string.rando_deleted,
                                        Toast.LENGTH_LONG).show();
                                holder.showSpinner(false);
                            }

                            @Override
                            protected void onFail(Error error) {
                                makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                                        Toast.LENGTH_LONG).show();
                                holder.showSpinner(false);
                            }
                        });
                    } catch (Exception e) {
                        makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                                Toast.LENGTH_LONG).show();
                        holder.showSpinner(false);
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

    private void reportRando(final RandoListAdapter.RandoViewHolder holder) {
        Analytics.logReportRando(firebaseAnalytics);
        if (NetworkUtil.isOnline(holder.randoItemLayout.getContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.randoItemLayout.getContext());
            builder.setPositiveButton(R.string.report, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        holder.showSpinner(true);
                        API.report(holder.rando.randoId, holder.randoItemLayout.getContext(), new NetworkResultListener(holder.randoItemLayout.getContext()) {
                            @Override
                            public void onOk() {
                                RandoDAO.deleteRandoByRandoId(holder.randoItemLayout.getContext(), holder.rando.randoId);
                                randoListAdapter.swapCursor(RandoDAO.getCursor(holder.image.getContext(), randoListAdapter.isStranger()));
                                randoListAdapter.notifyItemRemoved(holder.position);

                                makeText(holder.randoItemLayout.getContext(), R.string.rando_reported,
                                        Toast.LENGTH_LONG).show();
                                holder.showSpinner(false);
                            }

                            @Override
                            protected void onFail(Error error) {
                                makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                                        Toast.LENGTH_LONG).show();
                                holder.showSpinner(false);
                            }
                        });
                    } catch (Exception e) {
                        makeText(holder.randoItemLayout.getContext(), R.string.error_unknown_err,
                                Toast.LENGTH_LONG).show();
                        holder.showSpinner(false);
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

    private void shareRando(final RandoListAdapter.RandoViewHolder holder) {
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
}
