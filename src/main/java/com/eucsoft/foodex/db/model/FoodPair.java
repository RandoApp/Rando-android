package com.eucsoft.foodex.db.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class FoodPair implements Serializable {

    public long id;
    //user FoodPair props
    public User user = new User();
    //stranger FoodPair props
    public User stranger = new User();

    @Override
    public String toString() {
        return "FoodPair{" +
                "id=" + id +
                ", user=" + user +
                ", stranger=" + stranger +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FoodPair)) return false;

        FoodPair foodPair = (FoodPair) o;

        if (stranger != null ? !stranger.equals(foodPair.stranger) : foodPair.stranger != null)
            return false;
        if (user != null ? !user.equals(foodPair.user) : foodPair.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (stranger != null ? stranger.hashCode() : 0);
        return result;
    }

    public class User implements Serializable {

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

        public String foodId;
        public String foodURL;
        public UrlSize foodUrlSize = new UrlSize();
        public Date foodDate;
        public int bonAppetit;
        public String mapURL;
        public UrlSize mapUrlSize = new UrlSize();


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;

            User user = (User) o;

            if (bonAppetit != user.bonAppetit) return false;
            if (foodDate != null ? !foodDate.equals(user.foodDate) : user.foodDate != null)
                return false;
            if (foodId != null ? !foodId.equals(user.foodId) : user.foodId != null) return false;
            if (foodURL != null ? !foodURL.equals(user.foodURL) : user.foodURL != null)
                return false;
            if (foodUrlSize != null ? !foodUrlSize.equals(user.foodUrlSize) : user.foodUrlSize != null)
                return false;
            if (mapURL != null ? !mapURL.equals(user.mapURL) : user.mapURL != null) return false;
            if (mapUrlSize != null ? !mapUrlSize.equals(user.mapUrlSize) : user.mapUrlSize != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = foodId != null ? foodId.hashCode() : 0;
            result = 31 * result + (foodURL != null ? foodURL.hashCode() : 0);
            result = 31 * result + (foodUrlSize != null ? foodUrlSize.hashCode() : 0);
            result = 31 * result + (foodDate != null ? foodDate.hashCode() : 0);
            result = 31 * result + bonAppetit;
            result = 31 * result + (mapURL != null ? mapURL.hashCode() : 0);
            result = 31 * result + (mapUrlSize != null ? mapUrlSize.hashCode() : 0);
            return result;
        }


        public boolean isBonAppetit() {
            return bonAppetit > 0;
        }

        public String getFoodFileName() {
            return foodURL == null ? null : foodURL.substring(foodURL.lastIndexOf('/') + 1);
        }

        public String getMapFileName() {
            return mapURL == null ? null : mapURL.substring(mapURL.lastIndexOf('/') + 1);
        }

        @Override
        public String toString() {
            return "User{" +
                    "foodId='" + foodId + '\'' +
                    ", foodURL='" + foodURL + '\'' +
                    ", foodUrlSize.small='" + foodUrlSize.small + '\'' +
                    ", foodUrlSize.medium='" + foodUrlSize.medium + '\'' +
                    ", foodUrlSize.large='" + foodUrlSize.large + '\'' +
                    ", foodDate=" + foodDate +
                    ", bonAppetit=" + bonAppetit +
                    ", mapURL='" + mapURL + '\'' +
                    ", mapUrlSize.small='" + mapUrlSize.small + '\'' +
                    ", mapUrlSize.medium='" + mapUrlSize.medium + '\'' +
                    ", mapUrlSize.large='" + mapUrlSize.large + '\'' +
                    '}';
        }
    }

    public static class DateComparator implements Comparator<FoodPair> {

        @Override
        public int compare(FoodPair lhs, FoodPair rhs) {
            return (int) (rhs.user.foodDate.getTime() - lhs.user.foodDate.getTime());
        }
    }
}
