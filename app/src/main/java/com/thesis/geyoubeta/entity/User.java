/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User {

    private Integer id;

    private String fName;

    private String lName;

    private String email;

    private String password;

    private Date createdDate;

    private Set<PartyMember> partyMembers = new HashSet<PartyMember>(0);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Set<PartyMember> getPartyMembers() {
        return partyMembers;
    }

    public void setPartyMembers(Set<PartyMember> partyMembers) {
        this.partyMembers = partyMembers;
    }

    @Override
    public String toString() {
        return "[id : " + getId()
                + "] [ fName : " + getfName()
                + "] [ lName : " + getlName()
                + "] [ email : " + getEmail()
                + "] [ password : " + getPassword()
                + "] [ createdDate : " + getCreatedDate()
                + "]";
    }
}
