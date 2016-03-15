/*
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Copyright (C) 2015 Ivan Wesley Chua and Jethro Divino
 */

package com.thesis.geyoubeta.service;

import com.thesis.geyoubeta.entity.History;
import com.thesis.geyoubeta.entity.Message;
import com.thesis.geyoubeta.entity.Party;
import com.thesis.geyoubeta.entity.PartyMember;
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

    @GET("/user/get/{id}")
    void getUserById(@Path("id") Integer id, Callback<User> callback);

    @GET("/user/get/all")
    void getAllUsers(Callback<List<User>> callback);

    @POST("/user/create")
    void createUser(@Body User user, Callback<User> callback);

    @DELETE("/user/delete/{id}")
    void deleteUser(@Path("id") Integer id, Callback<Integer> callback);

    @GET("/user/validateEmail")
    void validateEmail(@Query("email") String email, Callback<Boolean> callback);

    @GET("/user/login")
    void checkCredentials(@Query("email") String email, @Query("password") String password, Callback<User> callback);

    @PUT("/user/update")
    void updateUser(@Body User user, Callback<User> callback);

    @GET("/user/getByEmail")
    void getUserByEmail(@Query("email") String email, Callback<User> callback);

    //PARTY

    @GET("/party/get/{id}")
    void getPartyById(@Path("id") Integer id, Callback<Party> callback);

    @GET("/party/get/all")
    void getAllparties(Callback<List<Party>> callback);

    @POST("/party/create/{id}")
    void createParty(@Body Party party, @Path("id") Integer id, Callback<Party> callback);

    @DELETE("/party/delete/{id}")
    void deleteParty(@Path("id") Integer id, Callback<Integer> callback);

    @GET("/party/validateName")
    void validateName(@Query("name") String name, Callback<Boolean> callback);

    @PUT("/party/update")
    void updateParty(@Body Party party, Callback<Party> callback);

    //HISTORY

    @POST("/history/add")
    void addHistory(@Body History history, Callback<History> callback);

    @GET("/history/get/{id}")
    void getHistory(@Path("id") Integer id, Callback<History> callback);

    @GET("/history/getAll/{id}")
    void getAllUserHistory(@Path("id") Integer id, Callback<List<History>> callback);

    @PUT("/history/update")
    void editHistory(@Body History history, Callback<History> callback);

    @GET("/history/getExistingHistory")
    void getExistingHistory(@Query("partyId") Integer partyId, @Query("userId") Integer userId, Callback<History> callback);

    //PartyMember

    @POST("/partyMember/add")
    void addMember(@Body PartyMember partyMember, Callback<PartyMember> callback);

    @GET("/partyMember/get/{id}")
    void getPartyMember(@Path("id") Integer id, Callback<PartyMember> callback);

    @GET("/partyMember/getByParty/{id}")
    void getPartyMembers(@Path("id") Integer id, Callback<List<User>> callback);

    @GET("/partyMember/getActiveParty/{id}")
    void getActiveParty(@Path("id") Integer id, Callback<PartyMember> callback);

    @GET("/partyMember/checkPartyMembership")
    void checkPartyMembership(@Query("partyId") Integer partyId, @Query("userId") Integer userId, Callback<Boolean> callback);

    @GET("/partyMember/getByUserAndParty")
    void getPartyMemberByUserAndParty(@Query("partyId") Integer partyId, @Query("userId") Integer userId, Callback<PartyMember> callback);

    @PUT("/partyMember/edit")
    void editMember(@Body PartyMember partyMember, Callback<PartyMember> callback);

    //Messages

    @POST("/msg/create")
    void addMessage(@Body Message message, Callback<Message> callback);

    @GET("/msg/getByParty/{id}")
    void getMessagesByParty(@Path("id") Integer id, Callback<List<Message>> callback);
}