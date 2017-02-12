
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Info implements Serializable, Parcelable
{

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("cover")
    @Expose
    private Cover cover;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Creator<Info> CREATOR = new Creator<Info>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Info createFromParcel(Parcel in) {
            Info instance = new Info();
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.content = ((String) in.readValue((String.class.getClassLoader())));
            instance.cover = ((Cover) in.readValue((Cover.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Info[] newArray(int size) {
            return (new Info[size]);
        }

    }
    ;
    private final static long serialVersionUID = -8956671277762071512L;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
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
        dest.writeValue(content);
        dest.writeValue(cover);
        dest.writeValue(url);
    }

    public int describeContents() {
        return  0;
    }

}
