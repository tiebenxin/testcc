package com.lensim.fingerchat.hexmeet.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig restMeeting_DaoConfig;
    private final DaoConfig restUser_DaoConfig;
    private final DaoConfig restContact_DaoConfig;
    private final DaoConfig restEndpoint_DaoConfig;
    private final DaoConfig meetingContact_DaoConfig;
    private final DaoConfig meetingUser_DaoConfig;
    private final DaoConfig meetingEndpoint_DaoConfig;
    private final DaoConfig restCallRecord_DaoConfig;
    private final DaoConfig restCallRow_DaoConfig;

    private final RestMeeting_Dao restMeeting_Dao;
    private final RestUser_Dao restUser_Dao;
    private final RestContact_Dao restContact_Dao;
    private final RestEndpoint_Dao restEndpoint_Dao;
    private final MeetingContact_Dao meetingContact_Dao;
    private final MeetingUser_Dao meetingUser_Dao;
    private final MeetingEndpoint_Dao meetingEndpoint_Dao;
    private final RestCallRecord_Dao restCallRecord_Dao;
    private final RestCallRow_Dao restCallRow_Dao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        restMeeting_DaoConfig = daoConfigMap.get(RestMeeting_Dao.class).clone();
        restMeeting_DaoConfig.initIdentityScope(type);

        restUser_DaoConfig = daoConfigMap.get(RestUser_Dao.class).clone();
        restUser_DaoConfig.initIdentityScope(type);

        restContact_DaoConfig = daoConfigMap.get(RestContact_Dao.class).clone();
        restContact_DaoConfig.initIdentityScope(type);

        restEndpoint_DaoConfig = daoConfigMap.get(RestEndpoint_Dao.class).clone();
        restEndpoint_DaoConfig.initIdentityScope(type);

        meetingContact_DaoConfig = daoConfigMap.get(MeetingContact_Dao.class).clone();
        meetingContact_DaoConfig.initIdentityScope(type);

        meetingUser_DaoConfig = daoConfigMap.get(MeetingUser_Dao.class).clone();
        meetingUser_DaoConfig.initIdentityScope(type);

        meetingEndpoint_DaoConfig = daoConfigMap.get(MeetingEndpoint_Dao.class).clone();
        meetingEndpoint_DaoConfig.initIdentityScope(type);

        restCallRecord_DaoConfig = daoConfigMap.get(RestCallRecord_Dao.class).clone();
        restCallRecord_DaoConfig.initIdentityScope(type);

        restCallRow_DaoConfig = daoConfigMap.get(RestCallRow_Dao.class).clone();
        restCallRow_DaoConfig.initIdentityScope(type);

        restMeeting_Dao = new RestMeeting_Dao(restMeeting_DaoConfig, this);
        restUser_Dao = new RestUser_Dao(restUser_DaoConfig, this);
        restContact_Dao = new RestContact_Dao(restContact_DaoConfig, this);
        restEndpoint_Dao = new RestEndpoint_Dao(restEndpoint_DaoConfig, this);
        meetingContact_Dao = new MeetingContact_Dao(meetingContact_DaoConfig, this);
        meetingUser_Dao = new MeetingUser_Dao(meetingUser_DaoConfig, this);
        meetingEndpoint_Dao = new MeetingEndpoint_Dao(meetingEndpoint_DaoConfig, this);
        restCallRecord_Dao = new RestCallRecord_Dao(restCallRecord_DaoConfig, this);
        restCallRow_Dao = new RestCallRow_Dao(restCallRow_DaoConfig, this);

        registerDao(RestMeeting_.class, restMeeting_Dao);
        registerDao(RestUser_.class, restUser_Dao);
        registerDao(RestContact_.class, restContact_Dao);
        registerDao(RestEndpoint_.class, restEndpoint_Dao);
        registerDao(MeetingContact_.class, meetingContact_Dao);
        registerDao(MeetingUser_.class, meetingUser_Dao);
        registerDao(MeetingEndpoint_.class, meetingEndpoint_Dao);
        registerDao(RestCallRecord_.class, restCallRecord_Dao);
        registerDao(RestCallRow_.class, restCallRow_Dao);
    }
    
    public void clear() {
        restMeeting_DaoConfig.getIdentityScope().clear();
        restUser_DaoConfig.getIdentityScope().clear();
        restContact_DaoConfig.getIdentityScope().clear();
        restEndpoint_DaoConfig.getIdentityScope().clear();
        meetingContact_DaoConfig.getIdentityScope().clear();
        meetingUser_DaoConfig.getIdentityScope().clear();
        meetingEndpoint_DaoConfig.getIdentityScope().clear();
        restCallRecord_DaoConfig.getIdentityScope().clear();
        restCallRow_DaoConfig.getIdentityScope().clear();
    }

    public RestMeeting_Dao getRestMeeting_Dao() {
        return restMeeting_Dao;
    }

    public RestUser_Dao getRestUser_Dao() {
        return restUser_Dao;
    }

    public RestContact_Dao getRestContact_Dao() {
        return restContact_Dao;
    }

    public RestEndpoint_Dao getRestEndpoint_Dao() {
        return restEndpoint_Dao;
    }

    public MeetingContact_Dao getMeetingContact_Dao() {
        return meetingContact_Dao;
    }

    public MeetingUser_Dao getMeetingUser_Dao() {
        return meetingUser_Dao;
    }

    public MeetingEndpoint_Dao getMeetingEndpoint_Dao() {
        return meetingEndpoint_Dao;
    }

    public RestCallRecord_Dao getRestCallRecord_Dao() {
        return restCallRecord_Dao;
    }

    public RestCallRow_Dao getRestCallRow_Dao() {
        return restCallRow_Dao;
    }

}
