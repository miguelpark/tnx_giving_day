package com.sangpark.android.lib.pdssdatagen.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.sangpark.android.lib.pdssdatagen.db.entities.ContactConvertedEntity;
import com.sangpark.android.lib.pdssdatagen.db.entities.ContactEntity;
import com.sangpark.android.lib.pdssdatagen.db.entities.ContactPdssSyncedEntity;

@Database(entities = {ContactEntity.class, ContactConvertedEntity.class, ContactPdssSyncedEntity.class}, version = 1)
public abstract class PdssRoomDatabase extends RoomDatabase {

    private static final String DB_FILE_NAME = "pdss_database";
    private static volatile PdssRoomDatabase instance;

    public abstract ContactDao getContactDao();

    static PdssRoomDatabase getDatabase(final Context context) {
        if(instance == null) {
            synchronized (PdssRoomDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context, PdssRoomDatabase.class, DB_FILE_NAME).build();
                }
            }
        }
        return instance;
    }

}
