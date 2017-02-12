
package com.groomify.hollavirun.rest.models.response;
import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mission implements Serializable, Parcelable
{

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cover_photo")
    @Expose
    private Object coverPhoto;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Parcelable.Creator<Mission> CREATOR = new Creator<Mission>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Mission createFromParcel(Parcel in) {
            Mission instance = new Mission();
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.coverPhoto = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Mission[] newArray(int size) {
            return (new Mission[size]);
        }

    }
            ;
    private final static long serialVersionUID = -1434140419791461395L;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(Object coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        dest.writeValue(description);
        dest.writeValue(coverPhoto);
        dest.writeValue(url);
    }

    public int describeContents() {
        return 0;
    }

}