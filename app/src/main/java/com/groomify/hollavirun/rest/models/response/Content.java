
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Content implements Serializable, Parcelable
{

    @SerializedName("url")
    @Expose
    private String url;
    public final static Creator<Content> CREATOR = new Creator<Content>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Content createFromParcel(Parcel in) {
            Content instance = new Content();
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Content[] newArray(int size) {
            return (new Content[size]);
        }

    }
    ;
    private final static long serialVersionUID = 7603943913164556142L;

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
