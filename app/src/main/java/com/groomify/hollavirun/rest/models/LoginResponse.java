package com.groomify.hollavirun.rest.models;

/**
 * Created by Valkyrie1988 on 1/22/2017.
 */

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

public class LoginResponse {

    @SerializedName("fb_id")
    @Expose
    private String fbId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("auth_token")
    @Expose
    private String authToken;
    @SerializedName("profile_picture")
    @Expose
    private ProfilePicture profilePicture;

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "fbId=" + fbId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                ", authToken='" + authToken + '\'' +
                ", profilePicture=" + profilePicture +
                '}';
    }
}
