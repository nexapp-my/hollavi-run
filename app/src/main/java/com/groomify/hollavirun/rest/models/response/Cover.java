
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cover implements Serializable, Parcelable
{

    @SerializedName("cover")
    @Expose
    private Cover_ cover;
    public final static Creator<Cover> CREATOR = new Creator<Cover>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Cover createFromParcel(Parcel in) {
            Cover instance = new Cover();
            instance.cover = ((Cover_) in.readValue((Cover_.class.getClassLoader())));
            return instance;
        }

        public Cover[] newArray(int size) {
            return (new Cover[size]);
        }

    }
    ;
    private final static long serialVersionUID = -5992673251247564333L;

    public Cover_ getCover() {
        return cover;
    }

    public void setCover(Cover_ cover) {
        this.cover = cover;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cover);
    }

    public int describeContents() {
        return  0;
    }

}
