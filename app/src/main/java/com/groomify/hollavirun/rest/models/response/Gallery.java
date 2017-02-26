
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gallery implements Serializable, Parcelable
{

    @SerializedName("photo")
    @Expose
    private Photo photo;
    public final static Creator<Gallery> CREATOR = new Creator<Gallery>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Gallery createFromParcel(Parcel in) {
            Gallery instance = new Gallery();
            instance.photo = ((Photo) in.readValue((Photo.class.getClassLoader())));
            return instance;
        }

        public Gallery[] newArray(int size) {
            return (new Gallery[size]);
        }

    }
    ;
    private final static long serialVersionUID = 1005468361455474752L;

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(photo);
    }

    public int describeContents() {
        return  0;
    }

}
