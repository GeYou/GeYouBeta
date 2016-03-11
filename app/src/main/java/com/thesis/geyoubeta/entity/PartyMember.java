/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.entity;

import java.util.Date;

/**
 * Created by ivanwesleychua on 07/02/2016.
 */
public class PartyMember {

    private Integer id;

    private User user;

    private Party party;

    private Float lastLong;

    private Float lastLat;

    private Date joinDate;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Float getLastLong() {
        return lastLong;
    }

    public void setLastLong(Float lastLong) {
        this.lastLong = lastLong;
    }

    public Float getLastLat() {
        return lastLat;
    }

    public void setLastLat(Float lastLat) {
        this.lastLat = lastLat;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
