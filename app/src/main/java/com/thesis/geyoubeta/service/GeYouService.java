/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.service;

import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.User;


import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by ivanwesleychua on 01/02/2016.
 */
public interface GeYouService {

    //USERS

    @POST("/user/create")
    void createUser(@Body User user, Callback<User> callback);

    @GET("/user/get/{id}")
    void getUserById(@Path("id") Integer id, Callback<User> callback);

    @GET("/user/get/all")
    void getAllUsers(Callback<List<User>> callback);

    @DELETE("/user/delete/{id}")
    void deleteUser(@Path("id") Integer id, Callback<Integer> callback);

    @GET("/user/login")
    void checkCredentials(@Query("email") String email, @Query("password") String password, Callback<User> callback);

    @PUT("/user/update")
    void updateUser(@Body User user, Callback<User> callback);

    @GET("/user/validateEmail")
    void validateEmail(@Query("email") String email, Callback<Boolean> callback);

    //PARTY

    @POST("/party/create")
    void createParty(@Body Party party, Callback<Party> callback);

    @GET("/party/get/{id}")
    void getPartyById(@Path("id") Integer id, Callback<Party> callback);

    @GET("/party/get/all")
    void getAllparties(Callback<List<Party>> callback);

    @DELETE("/party/delete/{id}")
    void deleteParty(@Path("id") Integer id, Callback<Integer> callback);

    @GET("/party/validateName")
    void validateName(@Query("name") String name, Callback<Boolean> callback);

    @PUT("/party/update")
    void updateParty(@Body Party party, Callback<Party> callback);
}
