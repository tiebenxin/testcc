package com.lensim.fingerchat.hexmeet.api.model;

import java.util.ArrayList;
import java.util.List;

public class RestGroupContactLink
{
   private int id;
   private List<Integer> contactIds = new ArrayList<Integer>();
   
   public int getId()
   {
      return id;
   }
   public void setId(int id)
   {
      this.id = id;
   }
   public List<Integer> getContactIds()
   {
      return contactIds;
   }
   public void setContactIds(List<Integer> contactIds)
   {
      this.contactIds = contactIds;
   }
   @Override
   public String toString()
   {
      return "RestGroupContactLink [id=" + id + ", contactIds=" + contactIds + "]";
   }
   
}
