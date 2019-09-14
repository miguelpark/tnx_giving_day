package com.sangpark.android.lib.pdssdatagen.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ContactConvertedEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long savedContactId;
//    boolean isRemoved = false;
//    String data;
//    @Embedded
    public String data;



    public ContactConvertedEntity(String data, long savedContactId) {
        this.data = data;
        this.savedContactId = savedContactId;
    }

    @Override
    public String toString() {
        return "ContactConvertedEntity{" +
                "id=" + id +
                ", savedContactId=" + savedContactId +
                ", data=" + data +
                '}';
    }
}
