package com.sangpark.android.lib.pdssdatagen.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.sangpark.android.lib.pdssdatagen.data.DeviceContact;
import com.sangpark.android.lib.pdssdatagen.data.PdssSyncData;
import com.sangpark.android.lib.pdssdatagen.db.entities.ContactConvertedEntity;
import com.sangpark.android.lib.pdssdatagen.db.entities.ContactEntity;
import com.sangpark.android.lib.pdssdatagen.db.entities.ContactPdssSyncedEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Dao
public abstract class ContactDao {

    private static final String TAG = "ContactDao";

    @Insert
    abstract void insert(ContactEntity entity);

    @Update
    abstract void update(ContactEntity entity);

    @Insert
    abstract void insert(ContactConvertedEntity entity);

    @Query("delete from ContactEntity where contactId = :contactId")
    abstract void deleteContact(long contactId);

    @Query("delete from ContactConvertedEntity where savedContactId =:contactId")
    abstract void deleteContactPdss(long contactId);

    @Query("select contactId from ContactEntity")
    abstract int[] getSavedContactIds();

    @Query("select count(contactId) = 0 as hasContact from ContactEntity where contactId =:contactId")
    abstract boolean isNewContact(long contactId);

    @Query("select count(contactId) > 0 as isUpdatedRequred from ContactEntity where contactId =:contactId and tsSynced < :tsContactModified")
    abstract boolean isUpdated(long contactId, long tsContactModified);

    @Insert
    abstract void addToSyncedTable(ContactPdssSyncedEntity entity);

    @Query("delete from ContactPdssSyncedEntity where data=:data")
    abstract void deleteFromSyncedTable(String data);

    @Query("select distinct data, 0 as isRemoved from ContactConvertedEntity conv where conv.data not in (select synced.data from ContactPdssSyncedEntity synced)")
    abstract List<PdssSyncDataImpl> getNewAddedData();

    @Query("select data, 1 as isRemoved from ContactPdssSyncedEntity sync where sync.data not in (select conv.data from ContactConvertedEntity conv)")
    abstract List<PdssSyncDataImpl> getRemovedData();


    @Transaction
    public List<PdssSyncData> getPdssSyncDataList(Supplier<List<DeviceContact>> supplier, Function<String, List<String>> toolFunc) {
        Log.i(TAG, "getPdssSyncDataList");
        long updateTimestamp = System.currentTimeMillis();
        List<DeviceContact> deviceContactList = supplier.get();

        //Check Deleted Contact
        int[] ids = getSavedContactIds();
        Arrays.stream(ids).forEach(id -> {
            if(!deviceContactList.stream().anyMatch(deviceContact -> deviceContact.getContactId() == id)) {
//                deleteContactPdss(id); // TODO :: Can be removed by define Foreign Key.. Maybe??
                deleteContact(id);
            }
        });

        //Update or Add New Contact
        deviceContactList.forEach(deviceContact -> {
            if(isNewContact(deviceContact.getContactId())){
                insert(new ContactEntity(deviceContact.getContactId(), deviceContact.getContactName(), deviceContact.getTimestamp(), updateTimestamp));
                toolFunc.apply(deviceContact.getContactName()).forEach(s -> insert(new ContactConvertedEntity(s, deviceContact.getContactId())));
            } else if(isUpdated(deviceContact.getContactId(), deviceContact.getTimestamp())){
                deleteContactPdss(deviceContact.getContactId());
                update(new ContactEntity(deviceContact.getContactId(), deviceContact.getContactName(), deviceContact.getTimestamp(), updateTimestamp));
                toolFunc.apply(deviceContact.getContactName()).forEach(s -> insert(new ContactConvertedEntity(s, deviceContact.getContactId())));
            }
        });

//        getCurrentConvertedTable().forEach(d ->Log.d(TAG, "current converted table :: " + d.toString()));


        List<PdssSyncData> result = new ArrayList<>();
        result.addAll(getRemovedData());
        result.addAll(getNewAddedData());

        result.forEach( syncedData -> {
            if(syncedData.isRemoved()) {
                deleteFromSyncedTable(syncedData.getData());
            } else {
                addToSyncedTable(new ContactPdssSyncedEntity(syncedData.getData()));
            }
        });

//        result.forEach(data -> Log.d(TAG, "Sync Request Data :: " + data.toString()));
//        getSyncedTable().forEach(d -> Log.d(TAG, "Engine has :: " + d));

        return result;
    }

    @VisibleForTesting
    @Query("select * from ContactConvertedEntity")
    public abstract List<ContactConvertedEntity> getCurrentConvertedTable();

    @VisibleForTesting
    @Query("select data from ContactPdssSyncedEntity")
    public abstract List<String> getSyncedTable();
}
