
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RaceGalleryResponse implements Serializable, Parcelable
{

    @SerializedName("gallery")
    @Expose
    private List<Gallery> gallery = null;
    public final static Creator<RaceGalleryResponse> CREATOR = new Creator<RaceGalleryResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public RaceGalleryResponse createFromParcel(Parcel in) {
            RaceGalleryResponse instance = new RaceGalleryResponse();
            in.readList(instance.gallery, (Gallery.class.getClassLoader()));
            return instance;
        }

        public RaceGalleryResponse[] newArray(int size) {
            return (new RaceGalleryResponse[size]);
        }

    }
    ;
    private final static long serialVersionUID = -7031789477853858230L;

    public List<Gallery> getGallery() {
        return gallery;
    }

    public void setGallery(List<Gallery> gallery) {
        this.gallery = gallery;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(gallery);
    }

    public int describeContents() {
        return  0;
    }

}
