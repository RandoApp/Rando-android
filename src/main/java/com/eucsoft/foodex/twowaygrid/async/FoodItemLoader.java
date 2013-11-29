package com.eucsoft.foodex.twowaygrid.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.widget.Adapter;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.adapter.FoodPairsAdapter;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.log.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapDrawable;

public class FoodItemLoader extends SimpleItemLoader<FoodPair, CacheableBitmapDrawable[]> {

    final BitmapLruCache mCache;

    private static BitmapFactory.Options decodeOptions = new BitmapFactory.Options();

    static {
        decodeOptions.inDither = false;
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        decodeOptions.inSampleSize = 3;
        decodeOptions.inPurgeable = true;
    }

    public FoodItemLoader(BitmapLruCache mCache) {
        this.mCache = mCache;
    }

    @Override
    public CacheableBitmapDrawable[] loadItem(FoodPair foodPair) {

        CacheableBitmapDrawable[] wrapper = new CacheableBitmapDrawable[4];

        CacheableBitmapDrawable strangerFoodImage = mCache.get(foodPair.stranger.foodURL, decodeOptions);
        if (strangerFoodImage == null) {
            strangerFoodImage = mCache.put(foodPair.stranger.foodURL, loadImage(foodPair.stranger.foodURL), decodeOptions);
        }
        wrapper[FoodPair.STRANGER_FOOD] = strangerFoodImage;

        CacheableBitmapDrawable strangerMapImage = mCache.get(foodPair.stranger.mapURL, decodeOptions);
        if (strangerMapImage == null) {
            strangerMapImage = mCache.put(foodPair.stranger.mapURL, loadImage(foodPair.stranger.mapURL), decodeOptions);
        }
        wrapper[FoodPair.STRANGER_MAP] = strangerMapImage;

        CacheableBitmapDrawable userFoodImage = mCache.get(foodPair.user.foodURL, decodeOptions);
        if (userFoodImage == null) {
            userFoodImage = mCache.put(foodPair.user.foodURL, loadImage(foodPair.user.foodURL), decodeOptions);
        }
        wrapper[FoodPair.USER_FOOD] = userFoodImage;

        CacheableBitmapDrawable userMapImage = mCache.get(foodPair.stranger.foodURL, decodeOptions);
        if (userMapImage == null) {
            userMapImage = mCache.put(foodPair.user.mapURL, loadImage(foodPair.user.mapURL), decodeOptions);
        }
        wrapper[FoodPair.USER_MAP] = userMapImage;

        return wrapper;
    }

    @Override
    public CacheableBitmapDrawable[] loadItemFromMemory(FoodPair foodPair) {
        CacheableBitmapDrawable[] wrapper = new CacheableBitmapDrawable[4];

        wrapper[FoodPair.STRANGER_FOOD] = mCache.getFromMemoryCache(foodPair.stranger.foodURL);
        wrapper[FoodPair.STRANGER_MAP] = mCache.getFromMemoryCache(foodPair.stranger.mapURL);
        wrapper[FoodPair.USER_FOOD] = mCache.getFromMemoryCache(foodPair.user.foodURL);
        wrapper[FoodPair.USER_MAP] = mCache.getFromMemoryCache(foodPair.user.mapURL);

        if (wrapper[FoodPair.STRANGER_FOOD] == null || wrapper[FoodPair.STRANGER_MAP] == null
                || wrapper[FoodPair.USER_FOOD] == null || wrapper[FoodPair.USER_MAP] == null)
            return null;

        return wrapper;
    }

    @Override
    public void displayItem(View itemView, CacheableBitmapDrawable[] result, boolean fromMemory) {

        Log.i(FoodItemLoader.class, "display item");

        FoodPairsAdapter.ViewHolder holder = (FoodPairsAdapter.ViewHolder) itemView.getTag();

        if (result == null) {
            return;
        }

        result[0].setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);

        if (fromMemory) {
            holder.stranger.foodImage.setImageDrawable(result[FoodPair.STRANGER_FOOD]);
        } else {
            BitmapDrawable emptyDrawable = new BitmapDrawable(itemView.getResources());

            TransitionDrawable fadeInDrawable =
                    new TransitionDrawable(new Drawable[]{emptyDrawable, result[FoodPair.STRANGER_FOOD]});

            holder.stranger.foodImage.setImageDrawable(fadeInDrawable);
            fadeInDrawable.startTransition(200);
        }
        holder.stranger.mapImage.setImageDrawable(result[FoodPair.STRANGER_MAP]);
        holder.user.foodImage.setImageDrawable(result[FoodPair.USER_FOOD]);
        holder.user.mapImage.setImageDrawable(result[FoodPair.USER_MAP]);
    }

    @Override
    public FoodPair getItemParams(Adapter adapter, int position) {
        return (FoodPair) adapter.getItem(position);
    }


    static private InputStream loadImage(String url) {
        HttpURLConnection connection;
        InputStream is = null;

        try {
            connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setConnectTimeout(15000);

            is = new BufferedInputStream(connection.getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return is;
    }
}
