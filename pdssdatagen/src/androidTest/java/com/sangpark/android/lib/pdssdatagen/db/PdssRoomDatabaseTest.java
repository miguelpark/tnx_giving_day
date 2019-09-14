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
    public void testSimple() {

        long firstAddTs = System.currentTimeMillis();


        //init Add
        Log.d(TAG,"init add contact");
        ArrayList<DeviceContact> list = new ArrayList<>();
        list.add(new DummyContact(1, "A A", firstAddTs));
        list.add(new DummyContact(2, "B B", firstAddTs));
        list.add(new DummyContact(3, "A B C", firstAddTs));

        List<PdssSyncData> ret = dao.getPdssSyncDataList(list, this::genToolFunc);

        ret.forEach(d -> Log.d(TAG, d.toString()));


        Log.d(TAG,"removed 'B B', 'A B C' ");
        list = new ArrayList<>();
        list.add(new DummyContact(1, "A A", firstAddTs));
        ret = dao.getPdssSyncDataList(list, this::genToolFunc);

        ret.forEach(d -> Log.d(TAG, d.toString()));


        Log.d(TAG,"edit 'A A' to 'A B' ");
        list = new ArrayList<>();
        list.add(new DummyContact(1, "A B", System.currentTimeMillis()));
        ret = dao.getPdssSyncDataList(list, this::genToolFunc);

        ret.forEach(d -> Log.d(TAG, d.toString()));


    }

    @Test
    public void testForeignKeyDelete() {
        //init Add
        Log.d(TAG,"init add contact");
        ArrayList<DeviceContact> list = new ArrayList<>();
        list.add(new DummyContact(1, "A A", 1));
        list.add(new DummyContact(2, "B B", 1));
        list.add(new DummyContact(3, "A B C", 1));

        List<PdssSyncData> ret = dao.getPdssSyncDataList(list, this::genToolFunc);

        ret.forEach(d -> Log.d(TAG, d.toString()));

        Log.d(TAG,"removed 'B B', 'A B C' ");
        list = new ArrayList<>();
        list.add(new DummyContact(1, "A A", 2));
        ret = dao.getPdssSyncDataList(list, this::genToolFunc);

        ret.forEach(d -> Log.d(TAG, d.toString()));

        dao.getCurrentConvertedTable().forEach(d -> Log.d(TAG, d.toString()));

    }

    @Test
    public void testLargeData() {

        long ts = System.currentTimeMillis();
        Log.d(TAG,"testLargeData");
        ArrayList<DeviceContact> list = new ArrayList<>();
        for(int i = 0; i < 1000; i++)
            list.add(new DummyContact(i, "WHAT HI " + i, 1));
        List<PdssSyncData> ret = dao.getPdssSyncDataList(list, this::genToolFunc);

        Log.d(TAG, "testLargeData done :: " + ret.size() +", "+ (System.currentTimeMillis() - ts));
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