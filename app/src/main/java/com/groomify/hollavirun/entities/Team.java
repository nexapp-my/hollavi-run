package com.groomify.hollavirun.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Valkyrie1988 on 2/26/2017.
 */

public class Team implements Parcelable {

    private String teamName;
    private int resourceId;

    public Team(int resourceId, String teamName) {
        this.resourceId = resourceId;
        this.teamName = teamName;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.teamName);
        dest.writeInt(this.resourceId);
    }

    public Team() {
    }

    protected Team(Parcel in) {
        this.teamName = in.readString();
        this.resourceId = in.readInt();
    }

    public static final Parcelable.Creator<Team> CREATOR = new Parcelable.Creator<Team>() {
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
