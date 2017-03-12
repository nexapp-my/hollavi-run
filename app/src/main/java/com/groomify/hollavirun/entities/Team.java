package com.groomify.hollavirun.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Valkyrie1988 on 2/26/2017.
 */

public class Team implements Parcelable {

    private String teamName;
    private int resourceId;
    private int resrouceIdForFB;
    private String prefixAlphabet;

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResrouceIdForFB() {
        return resrouceIdForFB;
    }

    public void setResrouceIdForFB(int resrouceIdForFB) {
        this.resrouceIdForFB = resrouceIdForFB;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setPrefixAlphabet(String prefixAlphabet) {
        this.prefixAlphabet = prefixAlphabet;
    }

    public String getPrefixAlphabet() {
        return prefixAlphabet;
    }

    public Team(int resourceId, int resrouceIdForFB, String teamName, String prefixAlphabet) {
        this.resourceId = resourceId;
        this.resrouceIdForFB = resrouceIdForFB;
        this.teamName = teamName;
        this.prefixAlphabet = prefixAlphabet;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.teamName);
        dest.writeInt(this.resourceId);
        dest.writeInt(this.resrouceIdForFB);
        dest.writeString(this.prefixAlphabet);
    }

    protected Team(Parcel in) {
        this.teamName = in.readString();
        this.resourceId = in.readInt();
        this.resrouceIdForFB = in.readInt();
        this.prefixAlphabet = in.readString();
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel source) {
            return new Team(source);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };
}
