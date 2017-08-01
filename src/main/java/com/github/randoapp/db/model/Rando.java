package com.github.randoapp.db.model;

import android.text.TextUtils;

import com.github.randoapp.log.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import static com.github.randoapp.Constants.CREATION_PARAM;
import static com.github.randoapp.Constants.DETECTED_PARAM;
import static com.github.randoapp.Constants.IMAGE_URL_PARAM;
import static com.github.randoapp.Constants.IMAGE_URL_SIZES_PARAM;
import static com.github.randoapp.Constants.LARGE_PARAM;
import static com.github.randoapp.Constants.MAP_URL_PARAM;
import static com.github.randoapp.Constants.MAP_URL_SIZES_PARAM;
import static com.github.randoapp.Constants.MEDIUM_PARAM;
import static com.github.randoapp.Constants.RANDO_ID_PARAM;
import static com.github.randoapp.Constants.RATING_PARAM;
import static com.github.randoapp.Constants.SMALL_PARAM;

public class Rando implements Serializable {

    public enum Status {
        IN, OUT
    }

    public int id;
    public String randoId;
    public String imageURL;
    public UrlSize imageURLSize = new UrlSize();
    public Date date;
    public String mapURL;
    public UrlSize mapURLSize = new UrlSize();
    public Status status;
    public String detected;
    public int rating;

    public boolean toUpload = false;

    public Rando() {
    }

    public boolean isUnwanted() {
        return detected != null && detected.contains("unwanted");
    }

    public static Rando fromJSON(String jsonRandoString, Rando.Status status) {
        try {
            return fromJSON(new JSONObject(jsonRandoString), status);
        } catch (JSONException e){
            return null;
        }
    }

    public static Rando fromJSON(JSONObject jsonRando, Rando.Status status) throws JSONException {
        Rando rando = new Rando();
        JSONObject userRandoUrlSizes = jsonRando.getJSONObject(IMAGE_URL_SIZES_PARAM);
        JSONObject userMapUrlSizes = jsonRando.getJSONObject(MAP_URL_SIZES_PARAM);

        rando.randoId = jsonRando.getString(RANDO_ID_PARAM);
        rando.imageURL = jsonRando.getString(IMAGE_URL_PARAM);
        rando.status = status;
        rando.imageURLSize.small = userRandoUrlSizes.getString(SMALL_PARAM);
        rando.imageURLSize.medium = userRandoUrlSizes.getString(MEDIUM_PARAM);
        rando.imageURLSize.large = userRandoUrlSizes.getString(LARGE_PARAM);

        rando.mapURL = jsonRando.getString(MAP_URL_PARAM);
        rando.mapURLSize.small = userMapUrlSizes.getString(SMALL_PARAM);
        rando.mapURLSize.medium = userMapUrlSizes.getString(MEDIUM_PARAM);
        rando.mapURLSize.large = userMapUrlSizes.getString(LARGE_PARAM);

        rando.date = new Date(jsonRando.getLong(CREATION_PARAM));

        if (jsonRando.has(DETECTED_PARAM)){
            JSONArray detected = jsonRando.getJSONArray(DETECTED_PARAM);
            rando.detected = detected.join(",");
        }

        if (jsonRando.has(RATING_PARAM)){
            rando.rating = jsonRando.getInt(RATING_PARAM);
        }
        return rando;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rando rando = (Rando) o;

        if (imageURL != null ? !imageURL.equals(rando.imageURL) : rando.imageURL != null)
            return false;
        if (imageURLSize != null ? !imageURLSize.equals(rando.imageURLSize) : rando.imageURLSize != null)
            return false;
        if (mapURL != null ? !mapURL.equals(rando.mapURL) : rando.mapURL != null) return false;
        if (mapURLSize != null ? !mapURLSize.equals(rando.mapURLSize) : rando.mapURLSize != null)
            return false;
        if (randoId != null ? !randoId.equals(rando.randoId) : rando.randoId != null) return false;
        if (status != rando.status) return false;
        if (detected != null ? !detected.equals(rando.detected) : rando.detected != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = randoId != null ? randoId.hashCode() : 0;
        result = 31 * result + (imageURL != null ? imageURL.hashCode() : 0);
        result = 31 * result + (imageURLSize != null ? imageURLSize.hashCode() : 0);
        result = 31 * result + (mapURL != null ? mapURL.hashCode() : 0);
        result = 31 * result + (mapURLSize != null ? mapURLSize.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (detected != null ? detected.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Rando{" +
                "id=" + id +
                ", randoId='" + randoId + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", imageURLSize=" + imageURLSize +
                ", date=" + date +
                ", mapURL='" + mapURL + '\'' +
                ", mapURLSize=" + mapURLSize +
                ", status=" + status +
                ", detected='" + detected + '\'' +
                ", rating=" + rating +
                ", toUpload=" + toUpload +
                '}';
    }

    public class UrlSize implements Serializable {
        public String small;
        public String medium;
        public String large;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UrlSize)) return false;

            UrlSize urlSize = (UrlSize) o;

            if (large != null ? !large.equals(urlSize.large) : urlSize.large != null)
                return false;
            if (medium != null ? !medium.equals(urlSize.medium) : urlSize.medium != null)
                return false;
            if (small != null ? !small.equals(urlSize.small) : urlSize.small != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = small != null ? small.hashCode() : 0;
            result = 31 * result + (medium != null ? medium.hashCode() : 0);
            result = 31 * result + (large != null ? large.hashCode() : 0);
            return result;
        }
    }

    public boolean isMapEmpty() {
        return TextUtils.isEmpty(mapURLSize.large) && TextUtils.isEmpty(mapURLSize.medium) && TextUtils.isEmpty(mapURLSize.small);
    }

    public static class DateComparator implements Comparator<Rando> {

        @Override
        public int compare(Rando lhs, Rando rhs) {
            Log.d(Rando.DateComparator.class, "Compare date: ", Long.toString(rhs.date.getTime()), " == ", Long.toString(lhs.date.getTime()), "  > ", Integer.toString((int) (rhs.date.getTime() - lhs.date.getTime())));
            return (int) (rhs.date.getTime() - lhs.date.getTime());
        }
    }
}