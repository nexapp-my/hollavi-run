package com.groomify.hollavirun.rest.models.request;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FbUser implements Serializable, Parcelable
{

    @SerializedName("fb_id")
    @Expose
    private String fbId;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("phone_no")
    @Expose
    private String phoneNo;
    @SerializedName("emergency_contact_person")
    @Expose
    private String emergencyContactPerson;
    @SerializedName("emergency_contact_phone")
    @Expose
    private String emergencyContactPhone;
    @SerializedName("number_of_runs")
    @Expose
    private Long numberOfRuns;
    @SerializedName("last_rank")
    @Expose
    private Object lastRank;
    public final static Parcelable.Creator<FbUser> CREATOR = new Creator<FbUser>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FbUser createFromParcel(Parcel in) {
            FbUser instance = new FbUser();
            instance.fbId = ((String) in.readValue((String.class.getClassLoader())));
            instance.profilePicture = ((String) in.readValue((String.class.getClassLoader())));
            instance.email = ((String) in.readValue((String.class.getClassLoader())));
            instance.country = ((String) in.readValue((String.class.getClassLoader())));
            instance.phoneNo = ((String) in.readValue((String.class.getClassLoader())));
            instance.emergencyContactPerson = ((String) in.readValue((String.class.getClassLoader())));
            instance.emergencyContactPhone = ((String) in.readValue((String.class.getClassLoader())));
            instance.numberOfRuns = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.lastRank = ((Object) in.readValue((Object.class.getClassLoader())));
            return instance;
        }

        public FbUser[] newArray(int size) {
            return (new FbUser[size]);
        }

    }
            ;
    private final static long serialVersionUID = 6702478361975914245L;

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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
        dest.writeValue(fbId);
        dest.writeValue(profilePicture);
        dest.writeValue(email);
        dest.writeValue(country);
        dest.writeValue(phoneNo);
        dest.writeValue(emergencyContactPerson);
        dest.writeValue(emergencyContactPhone);
        dest.writeValue(numberOfRuns);
        dest.writeValue(lastRank);
    }

    public int describeContents() {
        return 0;
    }

}