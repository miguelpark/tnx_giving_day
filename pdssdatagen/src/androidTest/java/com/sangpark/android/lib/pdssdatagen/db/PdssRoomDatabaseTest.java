package com.sangpark.android.lib.pdssdatagen.db;

import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.sangpark.android.lib.pdssdatagen.data.DeviceContact;
import com.sangpark.android.lib.pdssdatagen.data.PdssSyncData;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdssRoomDatabaseTest {

    private static final String TAG = "ContactDaoTest";
    private PdssRoomDatabase db;
    private ContactDao dao;

    @Before
    public void setUp() throws Exception {

        db = PdssRoomDatabase.getDatabase(InstrumentationRegistry.getContext());
        dao = db.getContactDao();
        db.clearAllTables();
    }

    @Test
    public void test() {

        long firstAddTs = System.currentTimeMillis();


        //init Add
        Log.d(TAG,"init add contact");
        List<PdssSyncData> ret = dao.getPdssSyncDataList(() -> {
            ArrayList<DeviceContact> list = new ArrayList<>();
            list.add(new DummyContact(1, "A A", firstAddTs));
            list.add(new DummyContact(2, "B B", firstAddTs));
            list.add(new DummyContact(3, "A B C", firstAddTs));
            return list;
        }, this::genToolFunc);

        ret.forEach(d -> Log.d(TAG, d.toString()));


        Log.d(TAG,"removed 'B B', 'A B C' ");
        ret = dao.getPdssSyncDataList(() -> {
            ArrayList<DeviceContact> list = new ArrayList<>();
            list.add(new DummyContact(1, "A A", firstAddTs));
            return list;
        }, this::genToolFunc);

        ret.forEach(d -> Log.d(TAG, d.toString()));


        Log.d(TAG,"edit 'A A' to 'A B' ");
        ret = dao.getPdssSyncDataList(() -> {
            ArrayList<DeviceContact> list = new ArrayList<>();
            list.add(new DummyContact(1, "A B", System.currentTimeMillis()));
            return list;
        }, this::genToolFunc);

        ret.forEach(d -> Log.d(TAG, d.toString()));


    }

    private List<String> genToolFunc(String inputString) {
        return Arrays.asList(inputString.split(" "));
    }

    private static class DummyContact implements DeviceContact{


        private long contactId;
        private String contactName;
        private long ts;

        public DummyContact(long contactId, String contactName, long ts) {
            this.contactId = contactId;
            this.contactName = contactName;
            this.ts = ts;
        }

        @Override
        public long getContactId() {
            return contactId;
        }

        @Override
        public String getContactName() {
            return contactName;
        }

        @Override
        public long getTimestamp() {
            return ts;
        }
    }
}