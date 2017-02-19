package com.groomify.hollavirun.utils;

import io.realm.RealmConfiguration;

/**
 * Created by Valkyrie1988 on 2/20/2017.
 */

public class RealmUtils {

    public static RealmConfiguration getRealmConfiguration(){
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        return config;
    }
}
