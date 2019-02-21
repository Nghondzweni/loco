package com.project.loco;

public class LocationData {

    private Double latitude = 0d;
    private Double longitude = 0d;
    private String title = "";
    private String description = "";
    private String address = "";

    public LocationData(){

    }
    public LocationData(Double lat, Double lng, String add, String tle, String d){
        latitude = lat;
        longitude = lng;
        title = tle;
        description = d;
        address = add;
    }

    public Double getLatitude(){
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }
    public String getTitle(){
        return title;
    }
    public void setLatitude(Double lat){
        latitude = lat;
    }
    public void setLongitude(Double lng){
        longitude = lng;
    }
    public void setTitle(String tle){
        title = tle;
    }
    public void setDescription(String d){ description = d; }
    public void setAddress(String a){ description = a; }
    public String toString(){
        return "Location Name: " + title + "\nLatitude: " + latitude + "\nLongitude: " + longitude + "\nDescription: " + description;
    }
}
