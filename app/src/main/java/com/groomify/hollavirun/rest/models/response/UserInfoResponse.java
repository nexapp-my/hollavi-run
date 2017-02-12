package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfoResponse implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("fb_id")
    @Expose
    private Integer fbId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private Object email;
    @SerializedName("country")
    @Expose
    private Object country;
    @SerializedName("auth_token")
    @Expose
    private String authToken;
    @SerializedName("phone_no")
    @Expose
    private Object phoneNo;
    @SerializedName("profile_picture")
    @Expose
    private ProfilePicture profilePicture;
    @SerializedName("emergency_contact_person")
    @Expose
    private Object emergencyContactPerson;
    @SerializedName("emergency_contact_phone")
    @Expose
    private Object emergencyContactPhone;
    @SerializedName("number_of_runs")
    @Expose
    private Integer numberOfRuns;
    @SerializedName("last_rank")
    @Expose
    private Integer lastRank;
    public final static Parcelable.Creator<UserInfoResponse> CREATOR = new Creator<UserInfoResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UserInfoResponse createFromParcel(Parcel in) {
            UserInfoResponse instance = new UserInfoResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.fbId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.email = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.country = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.authToken = ((String) in.readValue((String.class.getClassLoader())));
            instance.phoneNo = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.profilePicture = ((ProfilePicture) in.readValue((ProfilePicture.class.getClassLoader())));
            instance.emergencyContactPerson = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.emergencyContactPhone = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.numberOfRuns = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.lastRank = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public UserInfoResponse[] newArray(int size) {
            return (new UserInfoResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = -4220787807358928934L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFbId() {
        return fbId;
    }

    public void setFbId(Integer fbId) {
        this.fbId = fbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public Object getCountry() {
        return country;
    }

    public void setCountry(Object country) {
        this.country = country;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Object getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(Object phoneNo) {
        this.phoneNo = phoneNo;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Object getEmergencyContactPerson() {
        return emergencyContactPerson;
    }

    public void setEmergencyContactPerson(Object emergencyContactPerson) {
        this.emergencyContactPerson = emergencyContactPerson;
    }

    public Object getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(Object emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public Integer getNumberOfRuns() {
        return numberOfRuns;
    }

    public void setNumberOfRuns(Integer numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    public Integer getLastRank() {
        return lastRank;
    }

    public void setLastRank(Integer lastRank) {
        this.lastRank = lastRank;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(fbId);
        dest.writeValue(name);
        dest.writeValue(email);
        dest.writeValue(country);
        dest.writeValue(authToken);
        dest.writeValue(phoneNo);
        dest.writeValue(profilePicture);
        dest.writeValue(emergencyContactPerson);
        dest.writeValue(emergencyContactPhone);
        dest.writeValue(numberOfRuns);
        dest.writeValue(lastRank);
    }

    public int describeContents() {
        return 0;
    }

}