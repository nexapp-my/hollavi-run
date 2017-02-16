package com.groomify.hollavirun.entities;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class NewsFeed extends RealmObject{

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
}
