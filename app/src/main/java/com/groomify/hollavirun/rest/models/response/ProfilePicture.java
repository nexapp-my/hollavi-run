
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

    @SerializedName("url")
    @Expose
    private String url;
    public final static Parcelable.Creator<ProfilePicture> CREATOR = new Creator<ProfilePicture>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ProfilePicture createFromParcel(Parcel in) {
            ProfilePicture instance = new ProfilePicture();
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ProfilePicture[] newArray(int size) {
            return (new ProfilePicture[size]);
        }

    }
            ;
    private final static long serialVersionUID = 3895019402738333084L;

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
        return 0;
    }

    @Override
    public String toString() {
        return "ProfilePicture{" +
                "url='" + url + '\'' +
                '}';
    }
}
