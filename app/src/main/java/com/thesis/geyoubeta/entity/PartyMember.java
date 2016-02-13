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

    private PartyMemberId pk = new PartyMemberId();

    private Date joinDate;

    public PartyMemberId getPk() {
        return pk;
    }

    public void setPk(PartyMemberId pk) {
        this.pk = pk;
    }

    public User getUser() {
        return getPk().getUser();
    }

    public void setUser(User user) {
        getPk().setUser(user);
    }

    public Party getParty() {
        return getPk().getParty();
    }

    public void setParty(Party party) {
        getPk().setParty(party);
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
}
