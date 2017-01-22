package com.groomify.hollavirun.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FbUser {

    @SerializedName("fb_id")
    @Expose
    private String fbId;

    public FbUser(String fbId) {
        this.fbId = fbId;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

}
