package com.groomify.hollavirun.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by Valkyrie1988 on 11/29/2016.
 */

public class Coupon implements Parcelable{


    private int id;
    private String name;
    private String description;
    private Date expirationTime;

    private byte[] imageByteArr;
    private byte[] originalImageByteArr;

    private boolean redeemed;

    public Coupon(int id, String name, String description, Date expirationTime, byte[] imageByteArr, byte[] originalImageByteArr) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.expirationTime = expirationTime;
        this.imageByteArr = imageByteArr;
        this.originalImageByteArr = originalImageByteArr;
    }

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

    public byte[] getImageByteArr() {
        return imageByteArr;
    }

    public void setImageByteArr(byte[] imageByteArr) {
        this.imageByteArr = imageByteArr;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public byte[] getOriginalImageByteArr() {
        return originalImageByteArr;
    }

    public void setOriginalImageByteArr(byte[] originalImageByteArr) {
        this.originalImageByteArr = originalImageByteArr;
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
        dest.writeByteArray(this.imageByteArr);
        dest.writeByteArray(this.originalImageByteArr);
        dest.writeByte(this.redeemed ? (byte) 1 : (byte) 0);
    }

    protected Coupon(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        long tmpExpirationTime = in.readLong();
        this.expirationTime = tmpExpirationTime == -1 ? null : new Date(tmpExpirationTime);
        this.imageByteArr = in.createByteArray();
        this.originalImageByteArr = in.createByteArray();
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
