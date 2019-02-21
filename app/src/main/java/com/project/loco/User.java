package com.project.loco;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String email;


    User(int id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }
    //    getters/setters
    public void setId(int id){this.id = id;}
    public void setName(@NonNull String loc){this.name = loc;}
    public void setEmail(@NonNull String lat){this.email = lat;}

    public int getId(){return this.id;}
    public String getName(){return this.name;}
    public String getEmail(){return this.email;}
}
