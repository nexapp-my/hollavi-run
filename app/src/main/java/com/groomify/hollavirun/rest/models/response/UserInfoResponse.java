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
    private Long id;
    @SerializedName("fb_id")
    @Expose
    private Long fbId;
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
    @SerializedName("phone_no")
    @Expose
    private String phoneNo;
    @SerializedName("emergency_contact_person")
    @Expose
    private String emergencyContactPerson;
    @SerializedName("emergency_contact_phone")
    @Expose
    private String emergencyContactPhone;
    @SerializedName("profile_picture")
    @Expose
    private ProfilePicture profilePicture;
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
            instance.id = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.fbId = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.email = ((String) in.readValue((String.class.getClassLoader())));
            instance.country = ((String) in.readValue((String.class.getClassLoader())));
            instance.authToken = ((String) in.readValue((String.class.getClassLoader())));
            instance.phoneNo = ((String) in.readValue((String.class.getClassLoader())));
            instance.emergencyContactPerson = ((String) in.readValue((String.class.getClassLoader())));
            instance.emergencyContactPhone = ((String) in.readValue((String.class.getClassLoader())));
            instance.profilePicture = ((ProfilePicture) in.readValue((ProfilePicture.class.getClassLoader())));
            instance.numberOfRuns = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.lastRank = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public UserInfoResponse[] newArray(int size) {
            return (new UserInfoResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = 8077109803590672404L;

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

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmergencyContactPerson() {
        return emergencyContactPerson;
    }

    public void setEmergencyContactPerson(String emergencyContactPerson) {
        this.emergencyContactPerson = emergencyContactPerson;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
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
        dest.writeValue(emergencyContactPerson);
        dest.writeValue(emergencyContactPhone);
        dest.writeValue(profilePicture);
        dest.writeValue(numberOfRuns);
        dest.writeValue(lastRank);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "UserInfoResponse{" +
                "id=" + id +
                ", fbId=" + fbId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", country='" + country + '\'' +
                ", authToken='" + authToken + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", emergencyContactPerson='" + emergencyContactPerson + '\'' +
                ", emergencyContactPhone='" + emergencyContactPhone + '\'' +
                ", profilePicture=" + profilePicture +
                ", numberOfRuns=" + numberOfRuns +
                ", lastRank=" + lastRank +
                '}';
    }
}