
package com.groomify.hollavirun.rest.models.response;

import java.io.Serializable;
import java.util.Date;

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
    @SerializedName("posted_at")
    @Expose
    private Date postedDate;

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

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.content);
        dest.writeParcelable(this.cover, flags);
        dest.writeString(this.url);
        dest.writeLong(this.postedDate != null ? this.postedDate.getTime() : -1);
    }

    public Info() {
    }

    protected Info(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.content = in.readString();
        this.cover = in.readParcelable(Cover.class.getClassLoader());
        this.url = in.readString();
        long tmpPostedDate = in.readLong();
        this.postedDate = tmpPostedDate == -1 ? null : new Date(tmpPostedDate);
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel source) {
            return new Info(source);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };
}
