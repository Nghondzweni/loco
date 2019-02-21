package com.project.loco;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(version = 1, entities = {LocoLocation.class, User.class})
public abstract class LocationsDatabase extends RoomDatabase {

    public abstract LocationsDao locationsDao();
    private static volatile LocationsDatabase INSTANCE;

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    static LocationsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocationsDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here

                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LocationsDatabase.class,
                            "location_database"
                    ).addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final LocationsDao locationsDao;

        PopulateDbAsync(LocationsDatabase db) {
            locationsDao = db.locationsDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            locationsDao.deleteAll();
            LocoLocation locoLocation = new LocoLocation("location1", "123", "2342");
            locationsDao.insertLocation(locoLocation);
            return null;
        }
    }
}
