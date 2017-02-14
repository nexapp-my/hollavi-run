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

    public NewsFeed(){}

    public NewsFeed(int id, String header, String content, String timeStamp) {
        this.id = id;
        this.header = header;
        this.content = content;
        this.timeStamp = timeStamp;
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
}
