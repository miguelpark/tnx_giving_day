package com.sangpark.android.lib.pdssdatagen.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ContactPdssSyncedEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
//    @Embedded
    public String data;

    public ContactPdssSyncedEntity(String data) {
        this.data = data;
    }
}
