package com.lensim.fingerchat.hexmeet.api;


import com.lensim.fingerchat.hexmeet.api.model.OfflineMessage;
import com.lensim.fingerchat.hexmeet.api.model.RestAppVersionInfo;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestContactReq;
import com.lensim.fingerchat.hexmeet.api.model.RestFeedback;
import com.lensim.fingerchat.hexmeet.api.model.RestGroup;
import com.lensim.fingerchat.hexmeet.api.model.RestGroupContactLink;
import com.lensim.fingerchat.hexmeet.api.model.RestGroupReq;
import com.lensim.fingerchat.hexmeet.api.model.RestLoginReq;
import com.lensim.fingerchat.hexmeet.api.model.RestLoginResp;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.api.model.RestMeetingReq;
import com.lensim.fingerchat.hexmeet.api.model.RestParticipant;
import com.lensim.fingerchat.hexmeet.api.model.RestResult;
import com.lensim.fingerchat.hexmeet.api.model.RestTerminal;
import com.lensim.fingerchat.hexmeet.api.model.RestTerminalReq;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.api.model.RestUserReq;
import com.lensim.fingerchat.hexmeet.db.RestCallRow_;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

  @GET("overview")
  Call<ResponseBody> getCameras(@Query("token") String token);

  @PUT("login")
  Call<RestLoginResp> login(@Body RestLoginReq loginReq);

  @PUT("logout")
  Call<RestResult> logout(@Query("token") String token);

  @GET("latestAppVersion")
  Call<RestAppVersionInfo> getLatestAppVersion(@Query("appType") String appType, @Query("appLanguage") String lanugage);

  @PUT("user/{userid}/password")
  Call<RestResult> updatePassword(@Path("userid") int userId, @Query("token") String token, @Query("type") String type, @Body RestUserReq user);

  @PUT("users/{userid}")
  Call<RestUser> updateUser(@Path("userid") int userId, @Query("token") String token, @Body RestUserReq restUserReq);

  @POST("terminals")
  Call<RestTerminal> registerTerminal(@Query("token") String token, @Body RestTerminalReq restTerminalReq);

  @GET("contacts")
  Call<List<RestContact>> getContacts(@Query("token") String token);

  @GET("contacts")
  Call<List<RestContact>> getContacts(@Query("token") String token, @Query("topAscSize") int topAscSize);

  @POST("contacts")
  Call<RestContact> addContact(@Query("token") String token, @Body RestContactReq restContactReq);

  @DELETE("contacts/{contactid}")
  Call<RestResult> deleteContact(@Path("contactid") int contactId, @Query("token") String token);

  @GET("groups")
  Call<List<RestGroup>> getGroups(@Query("token") String token);

  @GET("groups/{groupid}")
  Call<RestGroup> getGroup(@Path("groupid") int groupId, @Query("token") String token);

  @PUT("groups/{groupid}")
  Call<RestGroup> updateGroup(@Path("groupid") int groupId, @Query("token") String token, @Body RestGroupReq restGroupReq);

  @DELETE("groups/{groupid}")
  Call<RestResult> deleteGroup(@Path("groupid") int groupId, @Query("token") String token);

  @POST("groups/{groupid}/contacts")
  Call<RestResult> addContactsToGroup(@Path("groupid") int groupId, @Query("token") String token, @Body RestGroupContactLink groupContactLink);

  @DELETE("groups/{groupid}/contacts/{contactid}")
  Call<RestResult> deleteContactFromGroup(@Path("groupid") int groupId, @Path("contactid") int contactId, @Query("token") String token);

  @GET("users")
  Call<RestUser> getUser(@Query("token") String token, @Path("userId") int userId);

  @GET("users")
  Call<List<RestUser>> getUsers(@Query("token") String token, @Query("userName") String userName);

  @GET("users")
  Call<List<RestUser>> getUsers(@Query("token") String token, @Query("userName") String userName, @Query("topAscSize") int topAscSize);

  @GET("meetings")
  Call<List<RestMeeting>> getMeetings(@Query("token") String token, @Query("joinMode") String joinMode, @Query("lastModifiedTime") long lastModifiedTime);

  @GET("meetings/{meetingid}")
  Call<RestMeeting> getMeeting(@Path("meetingid") int meetingId, @Query("token") String token);

  @POST("meetings")
  Call<RestMeeting> addMeeting(@Query("token") String token, @Body RestMeetingReq restMeetingReq);

  @DELETE("meetings/{meetingid}")
  Call<RestResult> deleteMeeting(@Path("meetingid") int meetingId, @Query("token") String token);

  @PUT("meetings/{meetingid}")
  Call<RestMeeting> updateMeeting(@Path("meetingid") int meetingId, @Query("token") String token,
                                  @Body RestMeetingReq restMeetingReq);

  @PUT("meetings/{meetingid}/control/{controltype}")
  Call<RestResult> controlMeeting(@Path("meetingid") int meetingId, @Path("controltype") String controlType, @Query("token") String token);

  @GET("meetings/{meetingid}/simpleparticipants")
  Call<List<RestParticipant>> getParticipants(@Path("meetingid") int meetingId, @Query("token") String token);

  @POST("pushService")
  Call<RestResult> pushOfflineMessage(@Query("token") String token, @Body OfflineMessage offlineMessage);

  @POST("callRecords")
  Call<RestResult> addCallRecord(@Query("token") String token, @Body RestCallRow_ restCallRecord);

  @GET("callRecords")
  Call<List<RestCallRow_>> getCallRecords(@Query("token") String token, @Query("sipNum") int sipNum);

  @POST("feedbacks")
  Call<RestFeedback> saveFeedback(@Body RestFeedback feedback);

}
