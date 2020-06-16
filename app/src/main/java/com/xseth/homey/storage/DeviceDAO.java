package com.xseth.homey.storage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.xseth.homey.homey.Device;

import java.util.List;

@Dao
public interface DeviceDAO {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Device device);

    @Query("DELETE FROM devices")
    void deleteAll();

    @Query("SELECT * from devices ORDER BY name ASC")
    LiveData<List<Device>> getDevices();
}
