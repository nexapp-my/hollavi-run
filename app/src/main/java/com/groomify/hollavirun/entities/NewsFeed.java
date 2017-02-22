package com.groomify.hollavirun.entities;

import android.os.Parcel;
import android.os.Parcelable;

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
    private String content;
    private String timeStamp;
    private String coverPhotoUrl;

    public NewsFeed(){}

    public NewsFeed(int id, String header, String content, String timeStamp, String coverPhotoUrl) {
        this.id = id;
        this.header = header;
        this.content = content;
        this.timeStamp = timeStamp;
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    @Override
    public String toString() {
        return "NewsFeed{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", content='" + content + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", coverPhotoUrl='" + coverPhotoUrl + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.header);
        dest.writeString(this.content);
        dest.writeString(this.timeStamp);
        dest.writeString(this.coverPhotoUrl);
    }

    protected NewsFeed(Parcel in) {
        this.id = in.readInt();
        this.header = in.readString();
        this.content = in.readString();
        this.timeStamp = in.readString();
        this.coverPhotoUrl = in.readString();
    }

    public static final Parcelable.Creator<NewsFeed> CREATOR = new Parcelable.Creator<NewsFeed>() {
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
