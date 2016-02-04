/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.entity;

/**
 * Created by ivanwesleychua on 01/02/2016.
 */
public class Party {

    private Integer id;

    private String name;

    private String startDateTime;

    private String endDateTime;

    private String destination;

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

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "[id : " + getId()
                + "] [ name : " + getName()
                + "] [ startDateTime : " + getStartDateTime()
                + "] [ endDateTime : " + getEndDateTime()
                + "] [ destination : " + getDestination()
                + "]";
    }
}
