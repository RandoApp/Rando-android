package com.github.randoapp.db.model;

import com.github.randoapp.log.Log;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class RandoPair implements Serializable {

    public long id;
    //user RandoPair props
    public User user = new User();
    //stranger RandoPair props
    public User stranger = new User();

    @Override
    public String toString() {
        return "RandoPair{" +
                "id=" + id +
                ", user=" + user +
                ", stranger=" + stranger +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RandoPair)) return false;

        RandoPair randoPair = (RandoPair) o;

        if (stranger != null ? !stranger.equals(randoPair.stranger) : randoPair.stranger != null)
            return false;
        if (user != null ? !user.equals(randoPair.user) : randoPair.user != null) return false;

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

        public String randoId;
        public String imageURL;
        public UrlSize imageURLSize = new UrlSize();
        public Date date;
        public String mapURL;
        public UrlSize mapURLSize = new UrlSize();


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;

            User user = (User) o;

            if (date != null ? !date.equals(user.date) : user.date != null)
                return false;
            if (randoId != null ? !randoId.equals(user.randoId) : user.randoId != null) return false;
            if (imageURL != null ? !imageURL.equals(user.imageURL) : user.imageURL != null)
                return false;
            if (imageURLSize != null ? !imageURLSize.equals(user.imageURLSize) : user.imageURLSize != null)
                return false;
            if (mapURL != null ? !mapURL.equals(user.mapURL) : user.mapURL != null) return false;
            if (mapURLSize != null ? !mapURLSize.equals(user.mapURLSize) : user.mapURLSize != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = randoId != null ? randoId.hashCode() : 0;
            result = 31 * result + (imageURL != null ? imageURL.hashCode() : 0);
            result = 31 * result + (imageURLSize != null ? imageURLSize.hashCode() : 0);
            result = 31 * result + (date != null ? date.hashCode() : 0);
            result = 31 * result + (mapURL != null ? mapURL.hashCode() : 0);
            result = 31 * result + (mapURLSize != null ? mapURLSize.hashCode() : 0);
            return result;
        }

        public String getRandoFileName() {
            return imageURL == null ? null : imageURL.substring(imageURL.lastIndexOf('/') + 1);
        }

        public String getMapFileName() {
            return mapURL == null ? null : mapURL.substring(mapURL.lastIndexOf('/') + 1);
        }

        @Override
        public String toString() {
            return "User{" +
                    "randoId='" + randoId + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", imageURLSize.small='" + imageURLSize.small + '\'' +
                    ", imageURLSize.medium='" + imageURLSize.medium + '\'' +
                    ", imageURLSize.large='" + imageURLSize.large + '\'' +
                    ", date=" + date +
                    ", mapURL='" + mapURL + '\'' +
                    ", mapURLSize.small='" + mapURLSize.small + '\'' +
                    ", mapURLSize.medium='" + mapURLSize.medium + '\'' +
                    ", mapURLSize.large='" + mapURLSize.large + '\'' +
                    '}';
        }
    }

    public static class DateComparator implements Comparator<RandoPair> {

        @Override
        public int compare(RandoPair lhs, RandoPair rhs) {
            Log.d(RandoPair.DateComparator.class, "Compare date: ", Long.toString(rhs.user.date.getTime()), " == ", Long.toString(lhs.user.date.getTime()), "  > ", Integer.toString((int) (rhs.user.date.getTime() - lhs.user.date.getTime())));
//            return rhs.user.date.compareTo(lhs.user.date);

            return (int) (rhs.user.date.getTime() - lhs.user.date.getTime());
        }
    }
}
