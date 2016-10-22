package com.groomify.hollavirun.entities;

/**
 * Created by Valkyrie1988 on 10/23/2016.
 */

public class Mission {
    private String missionNumber;
    private String missionTitle;
    private String missionDesc;

    public Mission(String missionNumber, String missionTitle, String missionDesc) {
        this.missionNumber = missionNumber;
        this.missionTitle = missionTitle;
        this.missionDesc = missionDesc;
    }

    public String getMissionNumber() {
        return missionNumber;
    }

    public void setMissionNumber(String missionNumber) {
        this.missionNumber = missionNumber;
    }

    public String getMissionTitle() {
        return missionTitle;
    }

    public void setMissionTitle(String missionTitle) {
        this.missionTitle = missionTitle;
    }

    public String getMissionDesc() {
        return missionDesc;
    }

    public void setMissionDesc(String missionDesc) {
        this.missionDesc = missionDesc;
    }
}
