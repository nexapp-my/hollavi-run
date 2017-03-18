
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse implements Serializable, Parcelable
{

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("fb_id")
    @Expose
    private Long fbId;
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
    private String emergencyContactPerson;
    @SerializedName("emergency_contact_phone")
    @Expose
    private Object emergencyContactPhone;
    @SerializedName("number_of_runs")
    @Expose
    private Long numberOfRuns;
    @SerializedName("last_rank")
    @Expose
    private Object lastRank;
    public final static Parcelable.Creator<LoginResponse> CREATOR = new Creator<LoginResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public LoginResponse createFromParcel(Parcel in) {
            LoginResponse instance = new LoginResponse();
            instance.id = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.fbId = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.email = in.readValue((Object.class.getClassLoader()));
            instance.country = in.readValue((Object.class.getClassLoader()));
            instance.authToken = ((String) in.readValue((String.class.getClassLoader())));
            instance.phoneNo = in.readValue((Object.class.getClassLoader()));
            instance.profilePicture = ((ProfilePicture) in.readValue((ProfilePicture.class.getClassLoader())));
            instance.emergencyContactPerson = ((String) in.readValue((String.class.getClassLoader())));
            instance.emergencyContactPhone = in.readValue((Object.class.getClassLoader()));
            instance.numberOfRuns = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.lastRank = in.readValue((Object.class.getClassLoader()));
            return instance;
        }

        public LoginResponse[] newArray(int size) {
            return (new LoginResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = -5877959178619302781L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFbId() {
        return fbId;
    }

    public void setFbId(Long fbId) {
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

    public String getEmergencyContactPerson() {
        return emergencyContactPerson;
    }

    public void setEmergencyContactPerson(String emergencyContactPerson) {
        this.emergencyContactPerson = emergencyContactPerson;
    }

    public Object getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(Object emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public Long getNumberOfRuns() {
        return numberOfRuns;
    }

    public void setNumberOfRuns(Long numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    public Object getLastRank() {
        return lastRank;
    }

    public void setLastRank(Object lastRank) {
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