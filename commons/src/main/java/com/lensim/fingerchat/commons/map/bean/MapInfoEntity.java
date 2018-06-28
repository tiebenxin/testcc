package com.lensim.fingerchat.commons.map.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.lensim.fingerchat.commons.utils.NoteStringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fussen on 2016/11/1.
 *
 * 每个条目中的信息
 */

public class MapInfoEntity implements Parcelable {


    public static final Creator<MapInfoEntity> CREATOR = new Creator<MapInfoEntity>() {
        @Override
        public MapInfoEntity createFromParcel(Parcel source) {
            return new MapInfoEntity(source);
        }

        @Override
        public MapInfoEntity[] newArray(int size) {
            return new MapInfoEntity[size];
        }
    };
    private String addressName;
    private String street;
    private Double latitude;
    //  public boolean isChoose;
    private Double longitude;

    public MapInfoEntity() {
    }

    protected MapInfoEntity(Parcel in) {
        this.addressName = in.readString();
        this.street = in.readString();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
    }

    public MapInfoEntity(String addressName, String street, Double latitude, Double longitude) {
        this.addressName = addressName;
        this.street = street;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.addressName);
        dest.writeString(this.street);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
    }

    public static MapInfoEntity fromJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object != null) {
                MapInfoEntity info = new MapInfoEntity();
                info.setStreet(object.optString("locationAddress"));
                info.setAddressName(object.optString("locationName"));
                info.setLatitude(object.optDouble("latitude"));
                info.setLongitude(object.optDouble("longitude"));
                return info;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;


    }

    public static String toJson(MapInfoEntity info) {
        try {
            JSONObject object = new JSONObject();
            object.put("locationAddress", info.getStreet());
            object.put("locationName", info.getAddressName());
            object.put("latitude", info.getLatitude());
            object.put("longitude", info.getLongitude());
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String toString() {
        return addressName + NoteStringUtils.SPLIT_COMER + latitude + NoteStringUtils.SPLIT_COMER
            + longitude + NoteStringUtils.SPLIT_COMER + street;
    }

}
