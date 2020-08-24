package com.xseth.homey.storage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.xseth.homey.homey.models.Device;
import com.xseth.homey.homey.models.Flow;

import java.util.List;

@Dao
public interface FlowDAO {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Flow flow);

    @Delete
    void deleteDevice(Flow... flows);

    @Query("DELETE FROM devices")
    void deleteAll();

    @Query("SELECT EXISTS(SELECT * FROM flows)")
    boolean hasFlows();

    @Query("SELECT * from flows")
    LiveData<List<Flow>> getFlows();

    @Update
    void updateFlows(Flow... flows);
}
