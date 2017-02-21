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
    private int missionNumber;

    private String missionNumberString;
    private String missionTitle;
    private String missionDesc;
    private String coverPhotoUrl;
    private String validationCode = "0000";
    private boolean unlocked;

    public Mission(){

    }

    public Mission(boolean unlocked, String coverPhotoUrl, String missionDesc, int missionNumber, String missionNumberString, String missionTitle, String validationCode) {
        this.unlocked = unlocked;
        this.coverPhotoUrl = coverPhotoUrl;
        this.missionDesc = missionDesc;
        this.missionNumber = missionNumber;
        this.missionNumberString = missionNumberString;
        this.missionTitle = missionTitle;
        this.validationCode = validationCode;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getMissionDesc() {
        return missionDesc;
    }

    public void setMissionDesc(String missionDesc) {
        this.missionDesc = missionDesc;
    }

    public int getMissionNumber() {
        return missionNumber;
    }

    public void setMissionNumber(int missionNumber) {
        this.missionNumber = missionNumber;
    }

    public String getMissionNumberString() {
        return missionNumberString;
    }

    public void setMissionNumberString(String missionNumberString) {
        this.missionNumberString = missionNumberString;
    }

    public String getMissionTitle() {
        return missionTitle;
    }

    public void setMissionTitle(String missionTitle) {
        this.missionTitle = missionTitle;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.missionNumber);
        dest.writeString(this.missionNumberString);
        dest.writeString(this.missionTitle);
        dest.writeString(this.missionDesc);
        dest.writeString(this.coverPhotoUrl);
        dest.writeByte(this.unlocked ? (byte) 1 : (byte) 0);
    }

    protected Mission(Parcel in) {
        this.missionNumber = in.readInt();
        this.missionNumberString = in.readString();
        this.missionTitle = in.readString();
        this.missionDesc = in.readString();
        this.coverPhotoUrl = in.readString();
        this.unlocked = in.readByte() != 0;
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
