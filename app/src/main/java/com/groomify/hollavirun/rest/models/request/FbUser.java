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
    private String profilePicture;
    public final static Parcelable.Creator<FbUser> CREATOR = new Creator<FbUser>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FbUser createFromParcel(Parcel in) {
            FbUser instance = new FbUser();
            instance.fbId = ((Long) in.readValue((Long.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.email = ((String) in.readValue((String.class.getClassLoader())));
            instance.country = ((String) in.readValue((String.class.getClassLoader())));
            instance.phoneNo = ((String) in.readValue((String.class.getClassLoader())));
            instance.emergencyContactPerson = ((String) in.readValue((String.class.getClassLoader())));
            instance.emergencyContactPhone = ((String) in.readValue((String.class.getClassLoader())));
            instance.profilePicture = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public FbUser[] newArray(int size) {
            return (new FbUser[size]);
        }

    }
            ;
    private final static long serialVersionUID = 6940264791101527105L;

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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(fbId);
        dest.writeValue(name);
        dest.writeValue(email);
        dest.writeValue(country);
        dest.writeValue(phoneNo);
        dest.writeValue(emergencyContactPerson);
        dest.writeValue(emergencyContactPhone);
        dest.writeValue(profilePicture);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "FbUser{" +
                "country='" + country + '\'' +
                ", fbId=" + fbId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", emergencyContactPerson='" + emergencyContactPerson + '\'' +
                ", emergencyContactPhone='" + emergencyContactPhone + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}