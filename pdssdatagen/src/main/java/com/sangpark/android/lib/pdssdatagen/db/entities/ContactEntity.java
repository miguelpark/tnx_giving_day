package com.sangpark.android.lib.pdssdatagen.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ContactEntity{
    @PrimaryKey
    public long contactId;
    public String contactName;
    public long tsContactModified;
    public long tsSynced;

    public ContactEntity(long contactId, String contactName, long tsContactModified, long tsSynced) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.tsContactModified = tsContactModified;
        this.tsSynced = tsSynced;
    }

    boolean isUpdated() {
        return tsContactModified > tsSynced;
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "contactId=" + contactId +
                ", contactName='" + contactName + '\'' +
                ", tsContactModified=" + tsContactModified +
                ", tsSynced=" + tsSynced +
                ", isUpdate=" + isUpdated() +
                '}';
    }
}
