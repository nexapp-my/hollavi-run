
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.groomify.hollavirun.rest.models.*;

public class ProfilePicture implements Serializable, Parcelable
{

    @SerializedName("profile_picture")
    @Expose
    private com.groomify.hollavirun.rest.models.response.ProfilePicture_ profilePicture;
    public final static Creator<ProfilePicture> CREATOR = new Creator<ProfilePicture>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ProfilePicture createFromParcel(Parcel in) {
            ProfilePicture instance = new ProfilePicture();
            instance.profilePicture = ((com.groomify.hollavirun.rest.models.response.ProfilePicture_) in.readValue((com.groomify.hollavirun.rest.models.response.ProfilePicture_.class.getClassLoader())));
            return instance;
        }

        public ProfilePicture[] newArray(int size) {
            return (new ProfilePicture[size]);
        }

    }
    ;
    private final static long serialVersionUID = -2545254387752246057L;

    public com.groomify.hollavirun.rest.models.response.ProfilePicture_ getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(com.groomify.hollavirun.rest.models.response.ProfilePicture_ profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(profilePicture);
    }

    public int describeContents() {
        return  0;
    }

}
