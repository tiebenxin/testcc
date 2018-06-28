package com.lensim.fingerchat.data.hexmeet;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.text.Collator;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取所有的群成员
 *
 * @author LY309313
 */
public class RoomInfo implements Comparable<RoomInfo> {
    /**
     * "USR_ID": "ly289019",
     * "USR_Name": "林谦",
     * "USR_UserImage": "C:\HnlensWeb\HnlensImage\Users\ly289019\Avatar\headimage.png",
     * "createuser": "ly309313@fingerchat.cn",
     * "isValid": 1,
     * "jid": "ly289019@fingerchat.cn",
     * "name": "test123432",
     * "roomID": 1623
     */

    private final String ROOM_ID = "room_id";
    private final String JID = "jid";
    private final String USR_ID_My = "user_id";
    private final String USR_NAME = "user_name";
    private final String USR_IMAGE = "user_image";
    private final String IS_VALID = "is_valid";
    private final String USER_TYPE = "user_type";
    private final String NAME = "name";
    private String CREATE_USER = "create_user";

    private int roomID;
    private String jid;
    private String USR_ID;
    private String USR_Name;
    private String USR_UserImage;
    private int isValid;
    private int usertype;
    private String name;
    private String createuser;

    public RoomInfo() {

    }

    public RoomInfo(JSONObject object) {
        if (object != null) {
            roomID = object.optInt(ROOM_ID);
            jid = object.optString(JID);
            USR_ID = object.optString(USR_ID_My);
            USR_Name = object.optString(USR_NAME);
            USR_UserImage = object.optString(USR_IMAGE);
            isValid = object.optInt(IS_VALID);
            usertype = object.optInt(USER_TYPE);
            name = object.optString(NAME);
            createuser = object.optString(CREATE_USER);
        }
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getUSR_ID() {
        return USR_ID;
    }

    public void setUSR_ID(String USR_ID) {
        this.USR_ID = USR_ID;
    }

    public String getUSR_Name() {
        return USR_Name;
    }

    public void setUSR_Name(String USR_Name) {
        this.USR_Name = USR_Name;
    }

    public String getUSR_UserImage() {
        return USR_UserImage;
    }

    public void setUSR_UserImage(String USR_UserImage) {
        this.USR_UserImage = USR_UserImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateuser() {
        return createuser;
    }

    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }


    @Override
    public int compareTo(@NonNull RoomInfo o) {
        String name1 = getUSR_Name();
        String name2 = o.getUSR_Name();
        if(name1 == null){
            name1 = getUSR_ID();
        }

        if(name2 == null){
            name2 = o.getUSR_ID();
        }
        return Collator.getInstance(Locale.CHINESE).compare(name1, name2);
    }

    public RoomInfo fromJson(String s) {
        if (!TextUtils.isEmpty(s)) {
            try {
                JSONObject obj = new JSONObject(s);
                return new RoomInfo(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    * private int roomID;
        private String jid;
        private String USR_ID;
        private String USR_Name;
        private String USR_UserImage;
        private int isValid;
        private int usertype;
        private String name;
        private String createuser;
    * */
    public JSONObject toJson() {
        try {
            JSONObject object = new JSONObject();
            object.put(ROOM_ID, getRoomID());
            object.put(JID, getJid());
            object.put(USR_NAME, getUSR_Name());
            object.put(USR_IMAGE, getUSR_UserImage());
            object.put(IS_VALID, getIsValid());
            object.put(USER_TYPE, getUsertype());
            object.put(NAME, getName());
            object.put(CREATE_USER, getCreateuser());
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }




}
