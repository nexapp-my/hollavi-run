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
    private int missionImageId;
    private boolean unlocked;

    public Mission(){

    }

    public Mission(int missionNumber, String missionNumberString, String missionTitle, String missionDesc, int missionImageId, boolean unlocked) {
        this.missionDesc = missionDesc;
        this.missionImageId = missionImageId;
        this.missionNumber = missionNumber;
        this.missionNumberString = missionNumberString;
        this.missionTitle = missionTitle;
        this.unlocked = unlocked;
    }

    protected Mission(Parcel in) {
        missionNumber = in.readInt();
        missionNumberString = in.readString();
        missionTitle = in.readString();
        missionDesc = in.readString();
        missionImageId = in.readInt();
        unlocked = in.readByte() != 0;
    }

    public static final Creator<Mission> CREATOR = new Creator<Mission>() {
        @Override
        public Mission createFromParcel(Parcel in) {
            return new Mission(in);
        }

        @Override
        public Mission[] newArray(int size) {
            return new Mission[size];
        }
    };

    public String getMissionDesc() {
        return missionDesc;
    }

    public void setMissionDesc(String missionDesc) {
        this.missionDesc = missionDesc;
    }

    public int getMissionImageId() {
        return missionImageId;
    }

    public void setMissionImageId(int missionImageId) {
        this.missionImageId = missionImageId;
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
        dest.writeInt(missionNumber);
        dest.writeString(missionNumberString);
        dest.writeString(missionTitle);
        dest.writeString(missionDesc);
        dest.writeInt(missionImageId);
        dest.writeByte((byte) (unlocked ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Mission{" +
                "missionDesc='" + missionDesc + '\'' +
                ", missionNumber=" + missionNumber +
                ", missionNumberString='" + missionNumberString + '\'' +
                ", missionTitle='" + missionTitle + '\'' +
                ", missionImageId=" + missionImageId +
                ", unlocked=" + unlocked +
                '}';
    }
}
