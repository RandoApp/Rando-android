package com.github.randoapp.db.model;

import java.util.Date;

public class RandoUpload {
    public int id;
    public String file;
    public String latitude;
    public String longitude;
    public Date date;
    public Date lastTry;

    public RandoUpload() {
    }

    public RandoUpload(String file, double latitude, double longitude, Date date) {
        this.file = file;
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
        this.date = date;
        this.lastTry = new Date(0);
    }

}
