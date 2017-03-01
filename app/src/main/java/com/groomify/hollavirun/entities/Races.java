package com.groomify.hollavirun.entities;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Races extends RealmObject implements Parcelable{

    @PrimaryKey
    public long id;
    public String raceName;
    public String raceLocation;
    public String distance;
    public String totalMission;
    public boolean status;
    public String endTime;
    public String firstAid;
    public String groomifySupport;
    public RealmList<Mission> missions;



    @Ignore
    public byte[] miniMapByteArr;
    @Ignore
    public byte[] completetionBadgeByteArr;


    public Races(long id, String raceName, String raceLocation, String distance, String totalMission, boolean status, String endTime, String firstAid, String groomifySupport) {
        this.id = id;
        this.raceName = raceName;
        this.raceLocation = raceLocation;
        this.distance = distance;
        this.totalMission = totalMission;
        this.status = status;
        this.endTime = endTime;
        this.firstAid = firstAid;
        this.groomifySupport = groomifySupport;
    }

    public Races() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public String getRaceLocation() {
        return raceLocation;
    }

    public void setRaceLocation(String raceLocation) {
        this.raceLocation = raceLocation;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTotalMission() {
        return totalMission;
    }

    public void setTotalMission(String totalMission) {
        this.totalMission = totalMission;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFirstAid() {
        return firstAid;
    }

    public void setFirstAid(String firstAid) {
        this.firstAid = firstAid;
    }

    public String getGroomifySupport() {
        return groomifySupport;
    }

    public void setGroomifySupport(String groomifySupport) {
        this.groomifySupport = groomifySupport;
    }

    public RealmList<Mission> getMissions() {
        return missions;
    }

    public void setMissions(RealmList<Mission> missions) {
        this.missions = missions;
    }

    public byte[] getMiniMapByteArr() {
        return miniMapByteArr;
    }

    public void setMiniMapByteArr(byte[] miniMapByteArr) {
        this.miniMapByteArr = miniMapByteArr;
    }

    public byte[] getCompletetionBadgeByteArr() {
        return completetionBadgeByteArr;
    }

    public void setCompletetionBadgeByteArr(byte[] completetionBadgeByteArr) {
        this.completetionBadgeByteArr = completetionBadgeByteArr;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.raceName);
        dest.writeString(this.raceLocation);
        dest.writeString(this.distance);
        dest.writeString(this.totalMission);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeString(this.endTime);
        dest.writeString(this.firstAid);
        dest.writeString(this.groomifySupport);
        dest.writeTypedList(this.missions);
        dest.writeByteArray(this.miniMapByteArr);
        dest.writeByteArray(this.completetionBadgeByteArr);
    }



    protected Races(Parcel in) {
        this.id = in.readLong();
        this.raceName = in.readString();
        this.raceLocation = in.readString();
        this.distance = in.readString();
        this.totalMission = in.readString();
        this.status = in.readByte() != 0;
        this.endTime = in.readString();
        this.firstAid = in.readString();
        this.groomifySupport = in.readString();
        this.miniMapByteArr = in.createByteArray();
        this.completetionBadgeByteArr = in.createByteArray();
    }

    public static final Creator<Races> CREATOR = new Creator<Races>() {
        @Override
        public Races createFromParcel(Parcel source) {
            return new Races(source);
        }

        @Override
        public Races[] newArray(int size) {
            return new Races[size];
        }
    };
}
