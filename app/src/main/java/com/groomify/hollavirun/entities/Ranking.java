package com.groomify.hollavirun.entities;

/**
 * Created by Valkyrie1988 on 10/23/2016.
 */

public class Ranking {
    private int rankNumber;
    private String name;
    private String id;
    private String time;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRankNumber() {
        return rankNumber;
    }

    public void setRankNumber(int rankNumber) {
        this.rankNumber = rankNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Ranking(int rankNumber, String name, String id, String time) {

        this.rankNumber = rankNumber;
        this.name = name;
        this.id = id;
        this.time = time;
    }
}
