package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckAppVersionResponse implements Serializable, Parcelable
{

    @SerializedName("android_version")
    @Expose
    private String androidVersion;
    @SerializedName("ios_version")
    @Expose
    private String iosVersion;
    public final static Parcelable.Creator<CheckAppVersionResponse> CREATOR = new Creator<CheckAppVersionResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CheckAppVersionResponse createFromParcel(Parcel in) {
            CheckAppVersionResponse instance = new CheckAppVersionResponse();
            instance.androidVersion = ((String) in.readValue((String.class.getClassLoader())));
            instance.iosVersion = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public CheckAppVersionResponse[] newArray(int size) {
            return (new CheckAppVersionResponse[size]);
        }

    }
            ;
    private final static long serialVersionUID = -1881790886764111435L;

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getIosVersion() {
        return iosVersion;
    }

    public void setIosVersion(String iosVersion) {
        this.iosVersion = iosVersion;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(androidVersion);
        dest.writeValue(iosVersion);
    }

    public int describeContents() {
        return 0;
    }

}