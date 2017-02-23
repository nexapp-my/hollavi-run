package com.groomify.hollavirun.entities;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valkyrie1988 on 10/23/2016.
 */

public class Mission extends RealmObject implements Parcelable {
    @PrimaryKey
    private int id;

    private int sequenceNumber;
    private String title;
    private String description;
    private String coverPhotoBase64;
    private int coverPhotoDefaultResourceId;
    private String validationCode = "0000";
    private boolean unlocked;
    private String latitude;
    private String longitude;

    public Mission(String coverPhotoBase64, int coverPhotoDefaultResourceId,String description, int id, String latitude, String longitude, int sequenceNumber, String title, boolean unlocked, String validationCode) {
        this.coverPhotoBase64 = coverPhotoBase64;
        this.coverPhotoDefaultResourceId = coverPhotoDefaultResourceId;
        this.description = description;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sequenceNumber = sequenceNumber;
        this.title = title;
        this.unlocked = unlocked;
        this.validationCode = validationCode;
    }

    public String getCoverPhotoBase64() {
        return coverPhotoBase64;
    }

    public void setCoverPhotoBase64(String coverPhotoBase64) {
        this.coverPhotoBase64 = coverPhotoBase64;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public int getCoverPhotoDefaultResourceId() {
        return coverPhotoDefaultResourceId;
    }

    public void setCoverPhotoDefaultResourceId(int coverPhotoDefaultResourceId) {
        this.coverPhotoDefaultResourceId = coverPhotoDefaultResourceId;
    }

    public Mission() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.sequenceNumber);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.coverPhotoBase64);
        dest.writeInt(this.coverPhotoDefaultResourceId);
        dest.writeString(this.validationCode);
        dest.writeByte(this.unlocked ? (byte) 1 : (byte) 0);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
    }

    protected Mission(Parcel in) {
        this.id = in.readInt();
        this.sequenceNumber = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.coverPhotoBase64 = in.readString();
        this.coverPhotoDefaultResourceId = in.readInt();
        this.validationCode = in.readString();
        this.unlocked = in.readByte() != 0;
        this.latitude = in.readString();
        this.longitude = in.readString();
    }

    public static final Creator<Mission> CREATOR = new Creator<Mission>() {
        @Override
        public Mission createFromParcel(Parcel source) {
            return new Mission(source);
        }

        @Override
        public Mission[] newArray(int size) {
            return new Mission[size];
        }
    };
}
