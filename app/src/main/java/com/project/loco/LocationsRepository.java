package com.project.loco;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class LocationsRepository {
    private LocationsDao locationsDao;
    private LiveData<List<LocoLocation>> allLocations;

    public LocationsRepository(Application application){
        LocationsDatabase locationsDB = LocationsDatabase.getDatabase(application);
        locationsDao = locationsDB.locationsDao();
        allLocations = locationsDao.loadAll();
    }

    public LiveData<List<LocoLocation>> getAllLocations(){
        return allLocations;
    }

    public void insert (LocoLocation locoLocation){
        new insertAsyncTask(locationsDao).execute(locoLocation);
    }

    //    SubClass
    private static class insertAsyncTask extends AsyncTask<LocoLocation, Void, Void> {

        private LocationsDao mAsyncTaskDao;

        insertAsyncTask(LocationsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final LocoLocation... params) {
            mAsyncTaskDao.insertLocation(params[0]);
            return null;
        }
    }
}
