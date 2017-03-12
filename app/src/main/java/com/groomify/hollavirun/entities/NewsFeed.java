package com.groomify.hollavirun.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class NewsFeed extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;
    private String header;
    private String description;
    private String content;
    private Date timeStamp;
    private String coverPhotoUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
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

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.header);
        dest.writeString(this.description);
        dest.writeString(this.content);
        dest.writeLong(this.timeStamp != null ? this.timeStamp.getTime() : -1);
        dest.writeString(this.coverPhotoUrl);
    }

    public NewsFeed() {
    }

    protected NewsFeed(Parcel in) {
        this.id = in.readInt();
        this.header = in.readString();
        this.description = in.readString();
        this.content = in.readString();
        long tmpTimeStamp = in.readLong();
        this.timeStamp = tmpTimeStamp == -1 ? null : new Date(tmpTimeStamp);
        this.coverPhotoUrl = in.readString();
    }

    public static final Creator<NewsFeed> CREATOR = new Creator<NewsFeed>() {
        @Override
        public NewsFeed createFromParcel(Parcel source) {
            return new NewsFeed(source);
        }

        @Override
        public NewsFeed[] newArray(int size) {
            return new NewsFeed[size];
        }
    };
}
