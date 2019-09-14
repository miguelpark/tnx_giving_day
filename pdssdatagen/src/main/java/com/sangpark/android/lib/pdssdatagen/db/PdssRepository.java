package com.sangpark.android.lib.pdssdatagen.db;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.sangpark.android.lib.pdssdatagen.data.DeviceContact;
import com.sangpark.android.lib.pdssdatagen.data.PdssSyncData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class PdssRepository {

    private static final String TAG = "PdssRepository";
    private final ContactDao mContactDao;

    public PdssRepository(Context context) {
        PdssRoomDatabase db = PdssRoomDatabase.getDatabase(context);
        mContactDao = db.getContactDao();
    }

    //TODO :: Make Not to work on MainThread.
    public List<PdssSyncData> getSyncDataList(Supplier<List<DeviceContact>> supplier, Function<String, List<String>> toolFunc) {
        Log.d(TAG, "getSyncDataList ");
        return mContactDao.getPdssSyncDataList(supplier, toolFunc);
    }

    @VisibleForTesting
    public void clearAllTablesForTesting(Context context) {
        CompletableFuture.runAsync(() -> {
            PdssRoomDatabase.getDatabase(context).clearAllTables();
        });
    }
}
