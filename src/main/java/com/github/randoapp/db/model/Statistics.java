package com.github.randoapp.db.model;

import com.github.randoapp.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Statistics implements Serializable {
    private int likes;
    private int dislikes;

    public static Statistics of(int likes, int dislikes) {
        Statistics statistics = new Statistics();
        statistics.likes = likes;
        statistics.dislikes = dislikes;
        return statistics;
    }

    public static Statistics from(JSONObject obj) {
        Statistics statistics = new Statistics();
        try {
            statistics.likes = obj.has(Constants.USER_STATISTICS_LIKES) ? obj.getInt(Constants.USER_STATISTICS_LIKES) : 0;
            statistics.dislikes = obj.has(Constants.USER_STATISTICS_DISLIKES) ? obj.getInt(Constants.USER_STATISTICS_DISLIKES) : 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statistics;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
