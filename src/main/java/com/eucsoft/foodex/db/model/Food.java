package com.eucsoft.foodex.db.model;

public class Food {

    private long id;
    //user Food props
    private String userPhotoURL;
    private String userLocalFile;
    private boolean userLiked;
    private String userMap;

    //stranger Food props
    private String strangerPhotoURL;
    private String strangerLocalFile;
    private boolean strangerLiked;
    private String strangerMap;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }

    public void setUserPhotoURL(String userPhotoURL) {
        this.userPhotoURL = userPhotoURL;
    }

    public String getUserLocalFile() {
        return userLocalFile;
    }

    public void setUserLocalFile(String userLocalFile) {
        this.userLocalFile = userLocalFile;
    }

    public boolean isUserLiked() {
        return userLiked;
    }

    public void setUserLiked(int userLiked) {
        this.userLiked = userLiked > 0;
    }

    public String getUserMap() {
        return userMap;
    }

    public void setUserMap(String userMap) {
        this.userMap = userMap;
    }

    public String getStrangerPhotoURL() {
        return strangerPhotoURL;
    }

    public void setStrangerPhotoURL(String strangerPhotoURL) {
        this.strangerPhotoURL = strangerPhotoURL;
    }

    public String getStrangerLocalFile() {
        return strangerLocalFile;
    }

    public void setStrangerLocalFile(String strangerLocalFile) {
        this.strangerLocalFile = strangerLocalFile;
    }

    public boolean isStrangerLiked() {
        return strangerLiked;
    }

    public void setStrangerLiked(int strangerLiked) {
        this.strangerLiked = strangerLiked > 0;
    }

    public String getStrangerMap() {
        return strangerMap;
    }

    public void setStrangerMap(String strangerMap) {
        this.strangerMap = strangerMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Food food = (Food) o;

        if (strangerLiked != food.strangerLiked) return false;
        if (userLiked != food.userLiked) return false;
        if (strangerLocalFile != null ? !strangerLocalFile.equals(food.strangerLocalFile) : food.strangerLocalFile != null)
            return false;
        if (strangerMap != null ? !strangerMap.equals(food.strangerMap) : food.strangerMap != null)
            return false;
        if (strangerPhotoURL != null ? !strangerPhotoURL.equals(food.strangerPhotoURL) : food.strangerPhotoURL != null)
            return false;
        if (userLocalFile != null ? !userLocalFile.equals(food.userLocalFile) : food.userLocalFile != null)
            return false;
        if (userMap != null ? !userMap.equals(food.userMap) : food.userMap != null) return false;
        if (userPhotoURL != null ? !userPhotoURL.equals(food.userPhotoURL) : food.userPhotoURL != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userPhotoURL != null ? userPhotoURL.hashCode() : 0;
        result = 31 * result + (userLocalFile != null ? userLocalFile.hashCode() : 0);
        result = 31 * result + (userLiked ? 1 : 0);
        result = 31 * result + (userMap != null ? userMap.hashCode() : 0);
        result = 31 * result + (strangerPhotoURL != null ? strangerPhotoURL.hashCode() : 0);
        result = 31 * result + (strangerLocalFile != null ? strangerLocalFile.hashCode() : 0);
        result = 31 * result + (strangerLiked ? 1 : 0);
        result = 31 * result + (strangerMap != null ? strangerMap.hashCode() : 0);
        return result;
    }
}
