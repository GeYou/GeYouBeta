/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.entity;

import java.util.Date;

/**
 * Created by ivanwesleychua on 16/02/2016.
 */
public class History {

    private Integer id;

    private Double startLong;

    private Double startLat;

    private Party party;

    private User user;

    private Date userDate;

    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getStartLong() {
        return startLong;
    }

    public void setStartLong(Double startLong) {
        this.startLong = startLong;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
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

    public Date getUserDate() {
        return userDate;
    }

    public void setUserDate(Date userDate) {
        this.userDate = userDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "[id : " + getId()
                + "] [ startLong : " + getStartLong()
                + "] [ startLat : " + getStartLat()
                + "]";
    }
}
