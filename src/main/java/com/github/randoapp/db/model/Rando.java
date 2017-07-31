package com.github.randoapp.db.model;

import android.text.TextUtils;

import com.github.randoapp.log.Log;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

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

    public Rando(Rando rando) {
        this.id = rando.id;
        this.randoId = rando.randoId;
        this.imageURL = rando.imageURL;
        this.imageURLSize = rando.imageURLSize;
        this.date = rando.date;
        this.mapURL = rando.mapURL;
        this.mapURLSize = rando.mapURLSize;
        this.status = rando.status;
        this.detected = rando.detected;
        this.rating = rando.rating;
    }

    public boolean isUnwanted() {
        return detected != null && detected.contains("unwanted");
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