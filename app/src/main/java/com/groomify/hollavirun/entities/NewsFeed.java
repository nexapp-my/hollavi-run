package com.groomify.hollavirun.entities;

/**
 * Created by Valkyrie1988 on 9/18/2016.
 */
public class NewsFeed {

    private String header;
    private String content;
    private String timeStamp;

    public NewsFeed(String header, String content, String timeStamp) {
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
}
