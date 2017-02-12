
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfilePicture_ implements Serializable, Parcelable
{

    @SerializedName("url")
    @Expose
    private String url;
    public final static Creator<ProfilePicture_> CREATOR = new Creator<ProfilePicture_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ProfilePicture_ createFromParcel(Parcel in) {
            ProfilePicture_ instance = new ProfilePicture_();
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ProfilePicture_[] newArray(int size) {
            return (new ProfilePicture_[size]);
        }

    }
    ;
    private final static long serialVersionUID = 5384580728631946458L;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(url);
    }

    public int describeContents() {
        return  0;
    }

}
