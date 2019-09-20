package com.locactio.geotime.bbdd;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.locactio.geotime.bbdd.daos.ClockingDao;
import com.locactio.geotime.entities.Clocking;


    @Database(entities = {Clocking.class}, version = 1)
    public abstract class ClockingDatabase extends RoomDatabase {
        public abstract ClockingDao getClockingDao();
    }

