package com.groomify.hollavirun.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Valkyrie1988 on 10/23/2016.
 */

public class Ranking extends RealmObject{

    @PrimaryKey
    private Integer rankNumber;
    private String name;
    private String id;
    private String teamName;
    private Integer completionTime;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Integer completionTime) {
        this.completionTime = completionTime;
    }

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

    public Integer getRankNumber() {
        return rankNumber;
    }

    public void setRankNumber(int rankNumber) {
        this.rankNumber = rankNumber;
    }


    public Ranking() {
    }

    public Ranking(Integer completionTime, String id, String name, Integer rankNumber, String teamName) {
        this.completionTime = completionTime;
        this.id = id;
        this.name = name;
        this.rankNumber = rankNumber;
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return "Ranking{" +
                "completionTime=" + completionTime +
                ", rankNumber=" + rankNumber +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", teamName='" + teamName + '\'' +
                '}';
    }
}
