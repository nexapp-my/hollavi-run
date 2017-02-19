package com.groomify.hollavirun.entities;

import io.realm.RealmObject;

/**
 * Created by Valkyrie1988 on 2/19/2017.
 */

public class RunnerInfo extends RealmObject{

    private Long userId;
    private String name;
    private String team;
    private String bibNo;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getBibNo() {
        return bibNo;
    }

    public void setBibNo(String bibNo) {
        this.bibNo = bibNo;
    }
}
