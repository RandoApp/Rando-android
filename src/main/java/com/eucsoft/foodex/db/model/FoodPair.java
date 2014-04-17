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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (getClass() != o.getClass())) return false;

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

    @Override
    public String toString() {
        return "FoodPair{" +
                "id=" + id +
                ", user=" + user +
                ", stranger=" + stranger +
                '}';
    }

    public class User implements Serializable {
        public String foodId;
        public String foodURL;
        public String foodURLSmall;
        public String foodURLMedium;
        public String foodURLLarge;
        public Date foodDate;
        public int bonAppetit;
        public String mapURL;

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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            User user = (User) o;

            if (bonAppetit != user.bonAppetit) return false;
            if (foodDate != null ? !foodDate.equals(user.foodDate) : user.foodDate != null)
                return false;
            if (foodId != null ? !foodId.equals(user.foodId) : user.foodId != null) return false;
            if (foodURL != null ? !foodURL.equals(user.foodURL) : user.foodURL != null)
                return false;
            if (foodURLLarge != null ? !foodURLLarge.equals(user.foodURLLarge) : user.foodURLLarge != null)
                return false;
            if (foodURLMedium != null ? !foodURLMedium.equals(user.foodURLMedium) : user.foodURLMedium != null)
                return false;
            if (foodURLSmall != null ? !foodURLSmall.equals(user.foodURLSmall) : user.foodURLSmall != null)
                return false;
            if (mapURL != null ? !mapURL.equals(user.mapURL) : user.mapURL != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = foodId != null ? foodId.hashCode() : 0;
            result = 31 * result + (foodURL != null ? foodURL.hashCode() : 0);
            result = 31 * result + (foodURLSmall != null ? foodURLSmall.hashCode() : 0);
            result = 31 * result + (foodURLMedium != null ? foodURLMedium.hashCode() : 0);
            result = 31 * result + (foodURLLarge != null ? foodURLLarge.hashCode() : 0);
            result = 31 * result + (foodDate != null ? foodDate.hashCode() : 0);
            result = 31 * result + bonAppetit;
            result = 31 * result + (mapURL != null ? mapURL.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "User{" +
                    "foodId='" + foodId + '\'' +
                    ", foodURL='" + foodURL + '\'' +
                    ", foodURLSmall='" + foodURLSmall + '\'' +
                    ", foodURLMedium='" + foodURLMedium + '\'' +
                    ", foodURLLarge='" + foodURLLarge + '\'' +
                    ", foodDate=" + foodDate +
                    ", bonAppetit=" + bonAppetit +
                    ", mapURL='" + mapURL + '\'' +
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
