/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.service;

import com.thesis.geyoubeta.entity.User;


import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by ivanwesleychua on 01/02/2016.
 */
public interface GeYouService {

    //@FormUrlEncoded
    @POST("/user/create")
    void createUser(@Body User user, Callback<User> callback);

    @GET("/user/get/{id}")
    void getUserById(@Path("id") Integer id, Callback<User> callback);
}
