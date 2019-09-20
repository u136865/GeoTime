package com.locactio.geotime.bbdd.facades;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.locactio.geotime.bbdd.ClockingDatabase;
import com.locactio.geotime.bbdd.daos.ClockingDao;
import com.locactio.geotime.entities.Clocking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClockingLab {

    private static ClockingLab sClockingDao;
    private ClockingDao clockingDao;

    private ClockingLab(Context context)
    {
        Context appContext = context.getApplicationContext();
        ClockingDatabase database = Room.databaseBuilder(appContext, ClockingDatabase.class, "clocking")
                .allowMainThreadQueries().build();
        clockingDao = database.getClockingDao();
    }

    public static ClockingLab get(Context context) {
        if (sClockingDao == null) {
            sClockingDao = new ClockingLab(context);
        }
        return sClockingDao;
    }

    public ArrayList<Clocking> getClockings(){
        ArrayList<Clocking> clockings = new ArrayList<Clocking>();
        clockings.addAll(clockingDao.getNotas());
        return clockings;
    }

    public void addClocking(Clocking clocking)
    {
        clockingDao.addClocking(clocking);
    }

    public void addAllClockings(List<Clocking> clockings)
    {
        clockingDao.addClockings(clockings);
    }

    public void updateClocking(Clocking clocking)
    {
        clockingDao.updateClocking(clocking);
    }

    public void deleteClocking(Clocking clocking)
    {
        clockingDao.deleteClocking(clocking);
    }

    public void deleteAllClockings()
    {
        clockingDao.deleteAllClocking();
    }
}
