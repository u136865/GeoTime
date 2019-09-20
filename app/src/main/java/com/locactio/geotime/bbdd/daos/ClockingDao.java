package com.locactio.geotime.bbdd.daos;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.locactio.geotime.entities.Clocking;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ClockingDao {

    @Query("SELECT * FROM clocking")
    List<Clocking> getNotas();

//    @Query("SELECT * FROM clocking WHERE mId LIKE :uuid")
//    Nota getNota(String uuid);

    @Insert
    void addClocking(Clocking clock);

    @Insert(onConflict = REPLACE)
    Long[] addClockings(List<Clocking> clockings);

    @Delete
    void deleteClocking(Clocking clock);

    @Query("DELETE FROM clocking")
    void deleteAllClocking();

    @Update
    void updateClocking(Clocking clock);
}