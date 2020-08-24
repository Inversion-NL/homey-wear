package com.xseth.homey.storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.xseth.homey.homey.models.Device;
import com.xseth.homey.homey.models.Flow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Device.class, Flow.class}, version = 12, exportSchema = false)
@TypeConverters({BitmapConverter.class})
public abstract class HomeyRoomDatabase extends RoomDatabase {

    public abstract DeviceDAO deviceDAO();
    public abstract FlowDAO flowDAO();

    private static volatile HomeyRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static HomeyRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HomeyRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HomeyRoomDatabase.class, "device_databases")
                            .fallbackToDestructiveMigration() // Remove data if no migration is available
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
