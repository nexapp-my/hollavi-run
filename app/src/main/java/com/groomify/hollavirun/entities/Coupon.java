package com.groomify.hollavirun.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valkyrie1988 on 11/29/2016.
 */

public class Coupon extends RealmObject implements Parcelable{


    @PrimaryKey
    private int id;
    private String name;
    private String description;
    private Date expirationTime;
    private String coverPhotoUrl;
    private int resourceId;

    private boolean redeemed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeLong(this.expirationTime != null ? this.expirationTime.getTime() : -1);
        dest.writeString(this.coverPhotoUrl);
        dest.writeInt(this.resourceId);
        dest.writeByte(this.redeemed ? (byte) 1 : (byte) 0);
    }

    public Coupon() {
    }

    protected Coupon(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        long tmpExpirationTime = in.readLong();
        this.expirationTime = tmpExpirationTime == -1 ? null : new Date(tmpExpirationTime);
        this.coverPhotoUrl = in.readString();
        this.resourceId = in.readInt();
        this.redeemed = in.readByte() != 0;
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel source) {
            return new Coupon(source);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };


}
