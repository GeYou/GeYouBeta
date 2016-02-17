/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.entity;

/**
 * Created by ivanwesleychua on 16/02/2016.
 */
public class History {

    private Integer id;

    private Float startLong;

    private Float startLat;

    private Float endLong;

    private Float endLat;

    private Party party;

    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getStartLong() {
        return startLong;
    }

    public void setStartLong(Float startLong) {
        this.startLong = startLong;
    }

    public Float getStartLat() {
        return startLat;
    }

    public void setStartLat(Float startLat) {
        this.startLat = startLat;
    }

    public Float getEndLong() {
        return endLong;
    }

    public void setEndLong(Float endLong) {
        this.endLong = endLong;
    }

    public Float getEndLat() {
        return endLat;
    }

    public void setEndLat(Float endLat) {
        this.endLat = endLat;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "[id : " + getId()
                + "] [ startLong : " + getStartLong()
                + "] [ startLat : " + getStartLat()
                + "] [ endLong : " + getEndLong()
                + "] [ endLat : " + getEndLat()
                + "]";
    }
}
