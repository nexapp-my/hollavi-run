package com.groomify.hollavirun.entities;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Races implements Parcelable{

    public int id;
    public String raceName;
    public String raceLocation;
    public String distance;
    public String totalMission;

    public byte[] miniMapByteArr;
    public byte[] completetionBadgeByteArr;

    public Races(int id, String raceName, String raceLocation, String distance, String totalMission, byte[] miniMapByteArr, byte[] completetionBadgeByteArr) {
        this.id = id;
        this.raceName = raceName;
        this.raceLocation = raceLocation;
        this.distance = distance;
        this.totalMission = totalMission;
        this.miniMapByteArr = miniMapByteArr;
        this.completetionBadgeByteArr = completetionBadgeByteArr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    protected Races(Parcel in) {
        id = in.readInt();
        raceName = in.readString();
        raceLocation = in.readString();
        distance = in.readString();
        totalMission = in.readString();
        miniMapByteArr = in.createByteArray();
        completetionBadgeByteArr = in.createByteArray();
    }

    public static final Creator<Races> CREATOR = new Creator<Races>() {
        @Override
        public Races createFromParcel(Parcel in) {
            return new Races(in);
        }

        @Override
        public Races[] newArray(int size) {
            return new Races[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(raceName);
        dest.writeString(raceLocation);
        dest.writeString(distance);
        dest.writeString(totalMission);
        dest.writeByteArray(miniMapByteArr);
        dest.writeByteArray(completetionBadgeByteArr);
    }
}
