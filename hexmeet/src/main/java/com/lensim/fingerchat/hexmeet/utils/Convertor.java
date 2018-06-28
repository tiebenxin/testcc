package com.lensim.fingerchat.hexmeet.utils;


import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.bean.Contact;

public class Convertor {

  public static String getAvatarUrl(RestContact restContact) {
    return "https://" + RuntimeData.getUcmServer() + restContact.getImageURL();
  }

  public static String getAvatarUrl(RestUser restUser) {
    return "https://" + RuntimeData.getUcmServer() + restUser.getImageURL();
  }

  public static String getLogUserAvatarUrl() {
    long lastModifiedTime = RuntimeData.getLogUser() != null ? RuntimeData.getLogUser()
        .getLastModifiedTime() : 0;
    return "https://" + RuntimeData.getUcmServer() + "/userFiles/avatar/"
        + RuntimeData.getLogUser().getId() + ".jpg?v=" + lastModifiedTime;
  }

  public static Contact from(RestContact restContact) {
    if (restContact == null) {
      return null;
    }

    Contact contact = new Contact();
    contact.setId(restContact.getId());
    contact.setName(restContact.getName());
    contact.setImageUrl("https://" + RuntimeData.getUcmServer() + restContact.getImageURL());
    contact.setTitle(restContact.getUserName());
    contact.setStatus(restContact.getStatus());

    return contact;
  }

}
