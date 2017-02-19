package com.groomify.hollavirun.entities;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valkyrie1988 on 2/19/2017.
 */

public class GroomifyUser extends RealmObject  {

    @PrimaryKey
    private Long id;
    private Long facebookId;
    private String authToken;
    private String email;
    private String name;
    private String facebookDisplayName;
    private String country;
    private String phoneNo;
    private String emergencyContactName;
    private String emergencyContactPhoneNo;
    private String profilePictureUrl; //a base64 image string.
    private String profilePictureBase64;
    private Integer totalRuns;
    private Integer lastRank;

    private Long currentRaceId;
    private String currentBibNo;
    private String currentRunnerId;
    private String currentTeam;

    private Ranking myRanking;


    public GroomifyUser() {
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getCurrentRaceId() {
        return currentRaceId;
    }

    public void setCurrentRaceId(Long currentRaceId) {
        this.currentRaceId = currentRaceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhoneNo() {
        return emergencyContactPhoneNo;
    }

    public void setEmergencyContactPhoneNo(String emergencyContactPhoneNo) {
        this.emergencyContactPhoneNo = emergencyContactPhoneNo;
    }

    public String getFacebookDisplayName() {
        return facebookDisplayName;
    }

    public void setFacebookDisplayName(String facebookDisplayName) {
        this.facebookDisplayName = facebookDisplayName;
    }

    public Long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLastRank() {
        return lastRank;
    }

    public void setLastRank(Integer lastRank) {
        this.lastRank = lastRank;
    }

    public Ranking getMyRanking() {
        return myRanking;
    }

    public void setMyRanking(Ranking myRanking) {
        this.myRanking = myRanking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Integer getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(Integer totalRuns) {
        this.totalRuns = totalRuns;
    }

    public String getProfilePictureBase64() {
        return profilePictureBase64;
    }

    public void setProfilePictureBase64(String profilePictureBase64) {
        this.profilePictureBase64 = profilePictureBase64;
    }

    public String getCurrentBibNo() {
        return currentBibNo;
    }

    public void setCurrentBibNo(String currentBibNo) {
        this.currentBibNo = currentBibNo;
    }

    public String getCurrentRunnerId() {
        return currentRunnerId;
    }

    public void setCurrentRunnerId(String currentRunnerId) {
        this.currentRunnerId = currentRunnerId;
    }

    public String getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(String currentTeam) {
        this.currentTeam = currentTeam;
    }

    @Override
    public String toString() {
        return "GroomifyUser{" +
                "authToken='" + authToken + '\'' +
                ", id=" + id +
                ", facebookId=" + facebookId +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", facebookDisplayName='" + facebookDisplayName + '\'' +
                ", country='" + country + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", emergencyContactName='" + emergencyContactName + '\'' +
                ", emergencyContactPhoneNo='" + emergencyContactPhoneNo + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", profilePictureBase64='" + profilePictureBase64 + '\'' +
                ", totalRuns=" + totalRuns +
                ", lastRank=" + lastRank +
                ", currentRaceId=" + currentRaceId +
                ", currentBibNo='" + currentBibNo + '\'' +
                ", currentRunnerId='" + currentRunnerId + '\'' +
                ", currentTeam='" + currentTeam + '\'' +
                ", myRanking=" + myRanking +
                '}';
    }
}
