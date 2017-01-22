package com.groomify.hollavirun.rest.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfilePicture {

    @SerializedName("profile_picture")
    @Expose
    private ProfilePicture_ profilePicture;

    public ProfilePicture_ getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture_ profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "ProfilePicture{" +
                "profilePicture=" + profilePicture +
                '}';
    }
}
