package com.project.loco;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {
    private LocationsRepository locationsRepository;
    private LiveData<List<LocoLocation>> allLocations;

    public LocationViewModel(Application application){
        super(application);
        locationsRepository = new LocationsRepository(application);
        allLocations = locationsRepository.getAllLocations();
    }

    public LiveData<List<LocoLocation>> getAllLocations(){ return allLocations; }

    public void insert(LocoLocation locoLocation){ locationsRepository.insert(locoLocation); }
}
