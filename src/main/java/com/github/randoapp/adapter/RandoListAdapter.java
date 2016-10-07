package com.github.randoapp.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.github.randoapp.App;
import com.github.randoapp.R;
import com.github.randoapp.animation.AnimationFactory;
import com.github.randoapp.animation.AnimationListenerAdapter;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.log.Log;
import com.github.randoapp.network.VolleySingleton;
import com.github.randoapp.util.BitmapUtil;
import com.github.randoapp.util.RandoUtil;
import com.makeramen.RoundedImageView;

import java.util.List;

import static com.android.volley.Request.Priority;

public class RandoListAdapter extends BaseAdapter {

    private boolean isStranger;

    private List<Rando> randos;
    private int imageSize;

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

    public RandoListAdapter(boolean isStranger) {
        this.isStranger = isStranger;
        initData();
    }

    private void initData() {
        if (isStranger) {
            randos = RandoDAO.getAllInRandos();
        } else {
            randos = RandoDAO.getAllOutRandosWithUploadQueue();
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

        final Rando rando = randos.get(position);
        final RandoViewHolder holder;

        if (imageSize == 0) {
            imageSize = getRandoImageSize(container);
        }

        Log.i(RandoListAdapter.class, "isStranger", String.valueOf(isStranger), "Size:", String.valueOf(size), "Position", String.valueOf(position));

        if (convertView != null) {
            holder = (RandoViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rando_item, container, false);
            holder = createHolder(convertView);
            addListenersToHolder(holder);
        }

        recycle(holder);
        loadImages(holder, rando);
        holder.randoId = rando.randoId;
        setAnimations(holder);
        return convertView;
    }

    private RandoViewHolder createHolder(View convertView) {
        RandoViewHolder holder = new RandoViewHolder();

        holder.viewSwitcher = (ViewSwitcher) convertView.findViewWithTag("viewSwitcher");

        holder.image = (RoundedImageView) convertView.findViewWithTag("image");
        holder.map = (RoundedImageView) convertView.findViewWithTag("map");
        ViewSwitcher.LayoutParams randoImagesLayout = new ViewSwitcher.LayoutParams(imageSize, imageSize);
        holder.image.setLayoutParams(randoImagesLayout);
        holder.map.setLayoutParams(randoImagesLayout);

        holder.actionsLayer = (RelativeLayout) convertView.findViewWithTag("actions_layer");
        RelativeLayout.LayoutParams actionsLayerLayoutParams = (RelativeLayout.LayoutParams) holder.actionsLayer.getLayoutParams();
        actionsLayerLayoutParams.height = imageSize;
        holder.actionsLayer.setLayoutParams(actionsLayerLayoutParams);


        convertView.setTag(holder);
        return holder;
    }

    private void addListenersToHolder(final RandoViewHolder holder) {
        View.OnClickListener randoOnClickListener = createRandoOnClickListener(holder);
        holder.image.setOnClickListener(randoOnClickListener);
        holder.map.setOnClickListener(randoOnClickListener);

        View.OnLongClickListener randoOnLongClickListener = createRandoOnLongClickListener(holder);
        holder.image.setOnLongClickListener(randoOnLongClickListener);
        holder.map.setOnLongClickListener(randoOnLongClickListener);

        final Animation scale = AnimationUtils.loadAnimation(App.context, R.anim.scale);
        holder.actionsLayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.startAnimation(scale);
                } else {
                    //view.clearAnimation();
                }
                return true;
            }
        });
    }

    private View.OnClickListener createRandoOnClickListener(final RandoViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.animationInProgress) return;
                holder.viewSwitcher.showNext();
            }
        };
    }

    private View.OnLongClickListener createRandoOnLongClickListener(final RandoViewHolder holder) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.actionsLayer.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT < 11) {
                    holder.image.setAlpha(64);
                    holder.map.setAlpha(64);
                } else {
                    holder.image.setAlpha(0.25f);
                    holder.map.setAlpha(0.25f);
                }

                return true;
            }
        };
    }

    private void recycle(RandoViewHolder holder) {
        holder.animationInProgress = false;

        cancelRequests(holder);

        recycleViewSwitcher(holder.viewSwitcher);

        holder.image.setImageBitmap(null);
        holder.map.setImageBitmap(null);
        removeDeleteButton(holder);
        holder.randoId = "";
    }

    private void removeDeleteButton(RandoViewHolder randoViewHolder) {
        if (Build.VERSION.SDK_INT < 11) {
            randoViewHolder.image.setAlpha(255);
            randoViewHolder.map.setAlpha(255);
        } else {
            randoViewHolder.image.setAlpha(1f);
            randoViewHolder.map.setAlpha(1f);
        }

        randoViewHolder.actionsLayer.setVisibility(View.GONE);

    }

    private void cancelRequests(RandoViewHolder holder) {
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

    private int getRandoImageSize(ViewGroup container) {
        return container.getWidth() - container.getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left)
                - container.getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_right);
    }

    private void setAnimations(final RandoViewHolder holder) {
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

    private void loadImages(final RandoViewHolder holder, final Rando rando) {
        if (rando.imageURLSize.small != null && !URLUtil.isNetworkUrl(rando.imageURLSize.small) && !rando.imageURLSize.small.isEmpty()) {
            loadFile(holder, rando.imageURL);
            return;
        }

        loadImage(holder, RandoUtil.getUrlByImageSize(imageSize, isStranger ? rando.imageURLSize : rando.imageURLSize), Priority.HIGH);
        loadMapImage(holder, RandoUtil.getUrlByImageSize(imageSize, isStranger ? rando.mapURLSize : rando.mapURLSize), Priority.LOW);
    }

    private void loadFile(final RandoViewHolder holder, final String filePath) {
        if (holder.image != null) {
            holder.image.setImageBitmap(BitmapUtil.decodeSampledBitmap(filePath, imageSize, imageSize));
        }
        if (holder.map != null) {
            holder.map.setImageResource(R.drawable.rando_pairing);
        } else {
            holder.needSetPairing = true;
        }
    }

    private void loadImage(final RandoViewHolder randoViewHolder, final String url, Priority priority) {
        if (TextUtils.isEmpty(url)) {
            if (randoViewHolder.image != null) {
                randoViewHolder.image.setImageResource(R.drawable.rando_pairing);
            } else {
                randoViewHolder.needSetPairing = true;
            }
            return;
        }

        if (URLUtil.isValidUrl(url)) {
            Log.d(RandoListAdapter.class, "image url: ", url);
            randoViewHolder.randoContainer = VolleySingleton.getInstance().getImageLoader().get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (randoViewHolder.image != null && response.getBitmap() != null) {
                        randoViewHolder.image.setImageBitmap(response.getBitmap());
                    } else if (randoViewHolder.image != null && response.getBitmap() == null) {
                        randoViewHolder.image.setImageResource(R.drawable.rando_loading);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(RandoListAdapter.class, "VolleyError when load rando image: ", url, "with imageSize = ", String.valueOf(imageSize), " , because ", error.getMessage());
                    if (randoViewHolder.image != null) {
                        randoViewHolder.image.setImageResource(R.drawable.rando_error);
                    } else {
                        randoViewHolder.needSetImageError = true;
                    }
                }
            }, ImageView.ScaleType.CENTER, 0, 0, priority);
        } else {
            Log.e(RandoListAdapter.class, "Ignore rando image because url: ", url, " incorrect");
            if (randoViewHolder.image != null) {
                randoViewHolder.image.setImageResource(R.drawable.rando_error);
            } else {
                randoViewHolder.needSetImageError = true;
            }
        }
    }

    private void loadMapImage(final RandoViewHolder randoViewHolder, final String url, Priority priority) {
        if (URLUtil.isValidUrl(url)) {
            Log.d(RandoListAdapter.class, "map url: ", url);
            randoViewHolder.mapContainer = VolleySingleton.getInstance().getImageLoader().get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (randoViewHolder.map != null && response.getBitmap() != null) {
                        randoViewHolder.map.setImageBitmap(response.getBitmap());
                    } else if (randoViewHolder.map != null && response.getBitmap() == null) {
                        randoViewHolder.map.setImageResource(R.drawable.rando_loading);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(RandoListAdapter.class, "VolleyError when load map image: ", url, "with imageSize = ", String.valueOf(imageSize), " , because ", error.getMessage());
                    if (randoViewHolder.map != null) {
                        randoViewHolder.map.setImageResource(R.drawable.rando_error);
                    } else {
                        randoViewHolder.needSetMapError = true;
                    }
                }
            }, ImageView.ScaleType.CENTER, 0, 0, priority);
        } else {
            Log.d(RandoListAdapter.class, "Ignore map image because url: ", url, " incorrect");
            if (randoViewHolder.map != null) {
                randoViewHolder.map.setImageResource(R.drawable.rando_error);
            } else {
                randoViewHolder.needSetMapError = true;
            }
        }
    }

    public static class RandoViewHolder {
        public boolean animationInProgress = false;

        public ViewSwitcher viewSwitcher;
        public RoundedImageView image;
        public RoundedImageView map;
        public ImageLoader.ImageContainer randoContainer;

        public ImageLoader.ImageContainer mapContainer;
        public RelativeLayout actionsLayer;


        public String randoId = "";
        public boolean needSetImageError = false;
        public boolean needSetMapError = false;

        public boolean needSetPairing = false;
    }
}
