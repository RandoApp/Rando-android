package com.eucsoft.foodex.db.model;

import java.util.Date;

public class Food {

    public long id;
    //user Food props
    public User user;
    //stranger Food props
    public User stranger;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Food food = (Food) o;

        if (stranger != null ? !stranger.equals(food.stranger) : food.stranger != null)
            return false;
        if (user != null ? !user.equals(food.user) : food.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (stranger != null ? stranger.hashCode() : 0);
        return result;
    }

    public class User {
        public String foodURL;
        public Date foodDate;
        public int bonAppetit;
        public String mapURL;

        public boolean isBonAppetit() {
            return bonAppetit > 0;
        }

        public String getFoodFileName() {
            return foodURL.substring(foodURL.lastIndexOf('/'));
        }

        public String getMapFileName() {
            return mapURL.substring(foodURL.lastIndexOf('/'));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            User user = (User) o;

            if (bonAppetit != user.bonAppetit) return false;
            if (foodDate != null ? !foodDate.equals(user.foodDate) : user.foodDate != null)
                return false;
            if (foodURL != null ? !foodURL.equals(user.foodURL) : user.foodURL != null)
                return false;
            if (mapURL != null ? !mapURL.equals(user.mapURL) : user.mapURL != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = foodURL != null ? foodURL.hashCode() : 0;
            result = 31 * result + (foodDate != null ? foodDate.hashCode() : 0);
            result = 31 * result + bonAppetit;
            result = 31 * result + (mapURL != null ? mapURL.hashCode() : 0);
            return result;
        }
    }
}
