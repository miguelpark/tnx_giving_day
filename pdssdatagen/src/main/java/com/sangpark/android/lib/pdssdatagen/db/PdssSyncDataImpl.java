package com.sangpark.android.lib.pdssdatagen.db;

import com.sangpark.android.lib.pdssdatagen.data.PdssSyncData;

class PdssSyncDataImpl implements PdssSyncData {

    String data;
    boolean isRemoved;

    public PdssSyncDataImpl(String data, boolean isRemoved) {
        this.data = data;
        this.isRemoved = isRemoved;
    }

    @Override
    public boolean isRemoved() {
        return isRemoved;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PdssSyncDataImpl{" +
                "isRemoved=" + isRemoved() +
                ", data='" + data + '\'' +
                '}';
    }
}
