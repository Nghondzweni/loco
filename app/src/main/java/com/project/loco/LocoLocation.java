package com.project.loco;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LocoLocation {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String locationName;

    @NonNull
    private String latitude;

    @NonNull
    private String longitude;

    LocoLocation(String locationName, String latitude, String longitude){
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //    getters/setters
    public void setId(int id){this.id = id;}
    public void setLocationName(@NonNull String loc){this.locationName = loc;}
    public void setLatitude(@NonNull String lat){this.latitude = lat;}
    public void setLongitude(@NonNull String lon){this.longitude = lon;}

    public int getId(){return this.id;}
    public String getLocationName(){return this.locationName;}
    public String getLatitude(){return this.latitude;}
    public String getLongitude(){return this.longitude;}
}
