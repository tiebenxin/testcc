package com.lensim.fingerchat.hexmeet.api.model;

import java.io.Serializable;
import java.util.List;

public class RestGroup implements Serializable
{
   private static final long serialVersionUID = 6420010791961661831L;
   private int id;
   private String name;
   private int ownerId;
   private String tip;
   private String lastMessage;
   private String orderMark;
   private List<RestContact> contacts;

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public int getOwnerId()
   {
      return ownerId;
   }

   public void setOwnerId(int ownerId)
   {
      this.ownerId = ownerId;
   }

   public List<RestContact> getContacts()
   {
      return contacts;
   }

   public void setContacts(List<RestContact> contacts)
   {
      this.contacts = contacts;
   }

   public String getTip()
   {
      return tip;
   }

   public void setTip(String tip)
   {
      this.tip = tip;
   }

   public String getLastMessage()
   {
      return lastMessage;
   }

   public void setLastMessage(String lastMessage)
   {
      this.lastMessage = lastMessage;
   }

   public String getOrderMark()
   {
      return orderMark;
   }

   public void setOrderMark(String orderMark)
   {
      this.orderMark = orderMark;
   }

   @Override
   public String toString()
   {
      return "RestGroup [id=" + id + ", name=" + name + ", ownerId=" + ownerId + ", tip=" + tip
            + ", lastMessage=" + lastMessage + ", orderMark=" + orderMark + ", contacts=" + contacts + "]";
   }
   
}
