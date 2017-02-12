package com.groomify.hollavirun.rest.models.request;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateUserLocationRequest implements Serializable, Parcelable
{

    @SerializedName("user_location")
    @Expose
    private UserLocation userLocation;
    public final static Parcelable.Creator<UpdateUserLocationRequest> CREATOR = new Creator<UpdateUserLocationRequest>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UpdateUserLocationRequest createFromParcel(Parcel in) {
            UpdateUserLocationRequest instance = new UpdateUserLocationRequest();
            instance.userLocation = ((UserLocation) in.readValue((UserLocation.class.getClassLoader())));
            return instance;
        }

        public UpdateUserLocationRequest[] newArray(int size) {
            return (new UpdateUserLocationRequest[size]);
        }

    }
            ;
    private final static long serialVersionUID = -2866170899065770736L;

    public UserLocation getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(userLocation);
    }

    public int describeContents() {
        return 0;
    }

}