package com.groomify.hollavirun.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("fb_user")
    @Expose
    private FbUser fbUser;

    public LoginRequest(String facebookUserId) {

        FbUser fbUser = new FbUser(facebookUserId);
        this.fbUser = fbUser;
    }

    public FbUser getFbUser() {
        return fbUser;
    }

    public void setFbUser(FbUser fbUser) {
        this.fbUser = fbUser;
    }

}
