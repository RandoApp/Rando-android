package com.eucsoft.foodex.task;

import android.os.AsyncTask;

import com.eucsoft.foodex.adapter.FoodPairsAdapter;

public class UpdateFoodPairViewTask extends AsyncTask {
    private int mPosition;
    private FoodPairsAdapter.ViewHolder holder;

    public UpdateFoodPairViewTask(int mPosition, FoodPairsAdapter.ViewHolder holder) {
        this.mPosition = mPosition;
        this.holder = holder;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        //holder.strangerFoodPager.setUser(holder.foodPair.stranger);
        //holder.userFoodPager.setUser(holder.foodPair.user);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
