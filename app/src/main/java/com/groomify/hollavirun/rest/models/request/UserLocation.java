package com.groomify.hollavirun.rest.models.request;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLocation implements Serializable, Parcelable
{

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("runner_id")
    @Expose
    private Integer runnerId;
    public final static Parcelable.Creator<UserLocation> CREATOR = new Creator<UserLocation>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UserLocation createFromParcel(Parcel in) {
            UserLocation instance = new UserLocation();
            instance.lat = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.lng = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.runnerId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public UserLocation[] newArray(int size) {
            return (new UserLocation[size]);
        }

    }
            ;
    private final static long serialVersionUID = 1356208012856867551L;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Integer runnerId) {
        this.runnerId = runnerId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(lat);
        dest.writeValue(lng);
        dest.writeValue(runnerId);
    }

    public int describeContents() {
        return 0;
    }

}