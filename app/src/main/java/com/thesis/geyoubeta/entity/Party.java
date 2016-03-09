/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.entity;

import java.util.Date;

/**
 * Created by ivanwesleychua on 01/02/2016.
 */
public class Party {

    private Integer id;

    private String name;

    private Date startDateTime;

    private Date endDateTime;

    private String destination;

    private Float destLong;

    private Float destLat;

    private String status;

    private Date createdDate;

    private User createdBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Float getDestLong() {
        return destLong;
    }

    public void setDestLong(Float destLong) {
        this.destLong = destLong;
    }

    public Float getDestLat() {
        return destLat;
    }

    public void setDestLat(Float destLat) {
        this.destLat = destLat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "[id : " + getId()
                + "] [ name : " + getName()
                + "] [ startDateTime : " + getStartDateTime()
                + "] [ endDateTime : " + getEndDateTime()
                + "] [ destination : " + getDestination()
                + "] [ destLong : " + getDestLong()
                + "] [ destLat : " + getDestLat()
                + "] [ status : " + getStatus()
                + "] [ createdDate : " + getCreatedDate()
                + "] [ createdBy : " + getCreatedBy()
                + "]";
    }
}
