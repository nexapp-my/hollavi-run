
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cover_ implements Serializable, Parcelable
{

    @SerializedName("url")
    @Expose
    private String url;
    public final static Creator<Cover_> CREATOR = new Creator<Cover_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Cover_ createFromParcel(Parcel in) {
            Cover_ instance = new Cover_();
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Cover_[] newArray(int size) {
            return (new Cover_[size]);
        }

    }
    ;
    private final static long serialVersionUID = 593598530041151192L;

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
