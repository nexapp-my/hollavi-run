package com.groomify.hollavirun.entities;

import android.graphics.Bitmap;

/**
 * Created by Valkyrie1988 on 10/4/2016.
 */
public class MissionCard {
    private String missionNumber;
    private String missionTitle;
    private Bitmap missionBackgroundImage;

    public MissionCard(){}

    public MissionCard(String missionNumber, String missionTitle, Bitmap missionBackgroundImage) {
        this.missionNumber = missionNumber;
        this.missionTitle = missionTitle;
        this.missionBackgroundImage = missionBackgroundImage;
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

    public Bitmap getMissionBackgroundImage() {
        return missionBackgroundImage;
    }

    public void setMissionBackgroundImage(Bitmap missionBackgroundImage) {
        this.missionBackgroundImage = missionBackgroundImage;
    }
}
