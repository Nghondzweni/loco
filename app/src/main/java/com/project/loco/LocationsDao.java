package com.project.loco;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationsDao {
    @Query("Select * FROM LocoLocation")
    LiveData<List<LocoLocation>> loadAll();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocation(LocoLocation locoLocation);
    @Query("DELETE FROM LocoLocation")
    void deleteAll();
}
