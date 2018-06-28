package com.lensim.fingerchat.hexmeet.db;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import de.greenrobot.dao.DaoException;

/**
 * Entity mapped to table "MEETING_CONTACT_".
 */
public class MeetingContact_ {

    private Long id;
    private Long meetingId;
    private Long contactId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MeetingContact_Dao myDao;

    private RestContact_ contact;
    private Long contact__resolvedKey;

    private RestMeeting_ meeting;
    private Long meeting__resolvedKey;


    public MeetingContact_() {
    }

    public MeetingContact_(Long id) {
        this.id = id;
    }

    public MeetingContact_(Long id, Long meetingId, Long contactId) {
        this.id = id;
        this.meetingId = meetingId;
        this.contactId = contactId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMeetingContact_Dao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    /** To-one relationship, resolved on first access. */
    public RestContact_ getContact() {
        Long __key = this.contactId;
        if (contact__resolvedKey == null || !contact__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RestContact_Dao targetDao = daoSession.getRestContact_Dao();
            RestContact_ contactNew = targetDao.load(__key);
            synchronized (this) {
                contact = contactNew;
            	contact__resolvedKey = __key;
            }
        }
        return contact;
    }

    public void setContact(RestContact_ contact) {
        synchronized (this) {
            this.contact = contact;
            contactId = contact == null ? null : contact.getId();
            contact__resolvedKey = contactId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public RestMeeting_ getMeeting() {
        Long __key = this.meetingId;
        if (meeting__resolvedKey == null || !meeting__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RestMeeting_Dao targetDao = daoSession.getRestMeeting_Dao();
            RestMeeting_ meetingNew = targetDao.load(__key);
            synchronized (this) {
                meeting = meetingNew;
            	meeting__resolvedKey = __key;
            }
        }
        return meeting;
    }

    public void setMeeting(RestMeeting_ meeting) {
        synchronized (this) {
            this.meeting = meeting;
            meetingId = meeting == null ? null : meeting.getId();
            meeting__resolvedKey = meetingId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}