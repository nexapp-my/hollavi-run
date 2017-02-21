package com.groomify.hollavirun.entities;

import android.os.Parcel;
import android.os.Parcelable;

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

    private boolean redeemed;

    public Coupon(int id, String name, String description, Date expirationTime, byte[] imageByteArr) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.expirationTime = expirationTime;
        this.imageByteArr = imageByteArr;
    }

    protected Coupon(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        imageByteArr = in.createByteArray();
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel in) {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeByteArray(imageByteArr);
    }
}
