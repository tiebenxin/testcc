package com.lensim.fingerchat.hexmeet.db;


import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestEndpoint;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;

import java.util.ArrayList;
import java.util.List;

public class Convertor {

  public static RestMeeting fromDbMeeting(RestMeeting_ o) {
    RestMeeting s = new RestMeeting();

    s.setId(o.getId().intValue());
    s.setName(o.getName());
    s.setStartTime(o.getStartTime());
    s.setDuration(o.getDuration());
    s.setApplicant(fromDbRestUser(o.getApplicant()));
    s.setMasterEndpointId(o.getMasterEndpointId());
    s.setMasterEndpointName(o.getMasterEndpointName());
    s.setLayout(o.getLayout());
    s.setStatus(o.getStatus());
    s.setAutoRedialing(o.getAutoRedialing());
    s.setConfPassword(o.getConfPassword());
    s.setNumericId(o.getNumericId());
    s.setConfTemplate(o.getConfTemplate());
    s.setPrivateTemplate(o.getPrivateTemplate());
    s.setStatusInfo(o.getStatusInfo());
    s.setGroupId(o.getGroupId());
    s.setGroupName(o.getGroupName());
    s.setConfType(o.getConfType());
    s.setRemarks(o.getRemarks());
    s.setUnitId(o.getUnitId());
    s.setUnitName(o.getUnitName());
    s.setDepartmentId(o.getDepartmentId());
    s.setDepartName(o.getDepartName());
    s.setContact(o.getContact());
    s.setContactPhone(o.getContactPhone());
    s.setMaxBandwidth(o.getMaxBandwidth());
    s.setEnableRecording(o.getEnableRecording());
    s.setMessageOverlayEnabled(o.getMessageOverlayEnabled());
    s.setMessageOverlayContent(o.getMessageOverlayContent());
    s.setMessageOverlayDisplayDuration(o.getMessageOverlayDisplayDuration());
    s.setMessageOverlayTransparency(o.getMessageOverlayTransparency());
    s.setMessageOverlaySpeed(o.getMessageOverlaySpeed());
    s.setMessageOverlayColor(o.getMessageOverlayColor());
    s.setMessageOverlayFontSize(o.getMessageOverlayFontSize());
    s.setMessageOverlayPosition(o.getMessageOverlayPosition());
    s.setLastModifiedTime(o.getLastModifiedTime());

    List<RestContact> contacts = new ArrayList<RestContact>();
    for (MeetingContact_ t : o.getMeetingContacts()) {
      contacts.add(fromDbRestContact(t.getContact()));
    }
    s.setContacts(contacts);

    List<RestUser> users = new ArrayList<RestUser>();
    for (MeetingUser_ t : o.getMeetingUsers()) {
      users.add(fromDbRestUser(t.getUser()));
    }
    s.setUsers(users);

    List<RestEndpoint> eps = new ArrayList<RestEndpoint>();
    for (MeetingEndpoint_ t : o.getMeetingEndpoints()) {
      eps.add(fromDbRestEndpoint(t.getEndpoint()));
    }
    s.setEndpoints(eps);

    return s;
  }

  public static RestContact fromDbRestContact(RestContact_ o) {
    RestContact s = new RestContact();

    s.setId(o.getId().intValue());
    s.setName(o.getName());
    s.setOwnerId(o.getOwnerId());
    s.setEmail(o.getEmail());
    s.setTelephone(o.getTelephone());
    s.setCellphone(o.getCellphone());
    s.setUserId(o.getUserId());
    s.setLastModifiedTime(o.getLastModifiedTime());
    s.setUserName(o.getUserName());
    s.setOrgName(o.getOrgName());
    s.setH323ConfNumber(o.getH323ConfNumber());
    s.setSipConfNumber(o.getSipConfNumber());
    s.setPstn(o.getPstn());
    s.setCallNumber(o.getCallNumber());
    s.setImageURL(o.getImageURL());
    s.setStatus(o.getStatus());

    return s;
  }

  public static RestUser fromDbRestUser(RestUser_ o) {
    RestUser s = new RestUser();

    s.setId(o.getId().intValue());
    s.setName(o.getName());
    s.setDisplayName(o.getDisplayName());
    s.setEmail(o.getEmail());
    s.setTelephone(o.getTelephone());
    s.setCellphone(o.getCellphone());
    s.setH323ConfNumber(o.getH323ConfNumber());
    s.setSipConfNumber(o.getSipConfNumber());
    s.setPstn(o.getPstn());
    s.setDescription(o.getDescription());
    s.setCallNumber(o.getCallNumber());
    s.setImageURL(o.getImageURL());
    s.setLastModifiedTime(o.getLastModifiedTime());

    return s;
  }

  public static RestEndpoint fromDbRestEndpoint(RestEndpoint_ o) {
    RestEndpoint s = new RestEndpoint();

    s.setId(o.getId().intValue());
    s.setName(o.getName());
    s.setType(o.getType());
    s.setAvailable(o.getAvailable());
    s.setUnitId(o.getUnitId());
    s.setUnitName(o.getUnitName());
    s.setE164(o.getE164());
    s.setCallType(o.getCallType());
    s.setCallNumber(o.getCallNumber());
    s.setOrgId(o.getOrgId());
    s.setOrgName(o.getOrgName());
    s.setDescription(o.getDescription());
    s.setCallNumber(o.getCallNumber());
    s.setImageURL(o.getImageURL());

    return s;
  }

  public static RestMeeting_ fromRestMeeting(RestMeeting o) {
    RestMeeting_ s = new RestMeeting_();

    s.setId((long) o.getId());
    s.setName(o.getName());
    s.setStartTime(o.getStartTime());
    s.setDuration(o.getDuration());
    s.setApplicantId(o.getApplicant().getId());
    s.setMasterEndpointId(o.getMasterEndpointId());
    s.setMasterEndpointName(o.getMasterEndpointName());
    s.setLayout(o.getLayout());
    s.setStatus(o.getStatus());
    s.setAutoRedialing(o.isAutoRedialing());
    s.setConfPassword(o.getConfPassword());
    s.setNumericId(o.getNumericId());
    s.setConfTemplate(o.isConfTemplate());
    s.setPrivateTemplate(o.isPrivateTemplate());
    s.setStatusInfo(o.getStatusInfo());
    s.setGroupId(o.getGroupId());
    s.setGroupName(o.getGroupName());
    s.setConfType(o.getConfType());
    s.setRemarks(o.getRemarks());
    s.setUnitId(o.getUnitId());
    s.setUnitName(o.getUnitName());
    s.setDepartmentId(o.getDepartmentId());
    s.setDepartName(o.getDepartName());
    s.setContact(o.getContact());
    s.setContactPhone(o.getContactPhone());
    s.setMaxBandwidth(o.getMaxBandwidth());
    s.setEnableRecording(o.isEnableRecording());
    s.setMessageOverlayEnabled(o.isMessageOverlayEnabled());
    s.setMessageOverlayContent(o.getMessageOverlayContent());
    s.setMessageOverlayDisplayDuration(o.getMessageOverlayDisplayDuration());
    s.setMessageOverlayTransparency(o.getMessageOverlayTransparency());
    s.setMessageOverlaySpeed(o.getMessageOverlaySpeed());
    s.setMessageOverlayColor(o.getMessageOverlayColor());
    s.setMessageOverlayFontSize(o.getMessageOverlayFontSize());
    s.setMessageOverlayPosition(o.getMessageOverlayPosition());
    s.setLastModifiedTime(o.getLastModifiedTime());

    return s;
  }

  public static RestContact_ fromRestContact(RestContact o) {
    RestContact_ s = new RestContact_();

    s.setId((long) o.getId());
    s.setName(o.getName());
    s.setOwnerId(o.getOwnerId());
    s.setEmail(o.getEmail());
    s.setTelephone(o.getTelephone());
    s.setCellphone(o.getCellphone());
    s.setUserId(o.getUserId());
    s.setLastModifiedTime(o.getLastModifiedTime());
    s.setUserName(o.getUserName());
    s.setOrgName(o.getOrgName());
    s.setH323ConfNumber(o.getH323ConfNumber());
    s.setSipConfNumber(o.getSipConfNumber());
    s.setPstn(o.getPstn());
    s.setCallNumber(o.getCallNumber());
    s.setImageURL(o.getImageURL());
    s.setStatus(o.getStatus());

    return s;
  }

  public static RestEndpoint_ fromRestEndpoint(RestEndpoint o) {
    RestEndpoint_ s = new RestEndpoint_();

    s.setId((long) o.getId());
    s.setType(o.getType());
    s.setName(o.getName());
    s.setIp(o.getIp());
    s.setAvailable(o.isAvailable());
    s.setOrgId(o.getOrgId());
    s.setOrgName(o.getOrgName());
    s.setUnitId(o.getUnitId());
    s.setUnitName(o.getUnitName());
    s.setInPrimaryVS(o.isInPrimaryVS());
    s.setPrimaryVSSortIndex(o.getPrimaryVSSortIndex());
    s.setInSecondaryVS(o.isInSecondaryVS());
    s.setSecondaryVSSortIndex(o.getSecondaryVSSortIndex());
    s.setSortIndex(o.getSortIndex());
    s.setE164(o.getE164());
    s.setCallType(o.getCallType());
    s.setCallNumber(o.getCallNumber());
    s.setOutwardType(o.getOutwardType());
    s.setMaster(o.isMaster());
    s.setSipUrl(o.getSipUrl());
    s.setDeviceStatus(o.getDeviceStatus());
    s.setAdminId(o.getAdminId());
    s.setAdminPassword(o.getAdminPassword());
    s.setDeviceName(o.getDeviceName());
    s.setDescription(o.getDescription());
    s.setSerialNumber(o.getSerialNumber());
    s.setSoftwareVersion(o.getSoftwareVersion());
    s.setCallSpeed(o.getCallSpeed());
    s.setContact(o.getContact());
    s.setContactEmail(o.getContactEmail());
    s.setContactPhone(o.getContactPhone());
    s.setUserCapacity(o.getUserCapacity());
    s.setImageURL(o.getImageURL());

    return s;
  }

  public static RestUser_ fromRestUser(RestUser o) {
    RestUser_ s = new RestUser_();

    s.setId((long) o.getId());
    s.setName(o.getName());
    s.setDisplayName(o.getDisplayName());
    s.setEmail(o.getEmail());
    s.setTelephone(o.getTelephone());
    s.setCellphone(o.getCellphone());
    s.setH323ConfNumber(o.getH323ConfNumber());
    s.setSipConfNumber(o.getSipConfNumber());
    s.setPstn(o.getPstn());
    s.setDescription(o.getDescription());
    s.setCallNumber(o.getCallNumber());
    s.setImageURL(o.getImageURL());
    s.setLastModifiedTime(o.getLastModifiedTime());

    return s;
  }

}
