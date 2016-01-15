package com.thesis.geyoubeta.entity;

/**
 * Created by ivanwesleychua on 09/01/2016.
 */
public class User {

    private Integer id;

    private String fName;

    private String lName;

    private String email;

    private String password;

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

    @Override
    public String toString() {
        return "[id : " + getId()
                + "] [ fName : " + getfName()
                + "] [ lName : " + getlName()
                + "] [ email : " + getEmail()
                + "] [ password : " + getPassword();
    }
}
