package com.xseth.homey.storage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.xseth.homey.homey.Device;

import java.util.List;

@Dao
public interface DeviceDAO {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Device device);

    @Delete
    void deleteAll(Device... devices);

    @Query("SELECT EXISTS(SELECT * FROM devices)")
    boolean hasDevices();

    @Query("SELECT * from devices")
    LiveData<List<Device>> getDevices();

    @Update
    void updateDevices(Device... devices);

}
