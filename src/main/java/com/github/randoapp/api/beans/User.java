package com.github.randoapp.api.beans;

import com.github.randoapp.db.model.Rando;

import java.util.List;

public class User {
    public List<Rando> randosIn;
    public List<Rando> randosOut;
    public String email;

    @Override
    public String toString() {
        return "User{" +
                "randosIn=" + randosIn +
                ", randosOut=" + randosOut +
                ", email='" + email + '\'' +
                '}';
    }
}
