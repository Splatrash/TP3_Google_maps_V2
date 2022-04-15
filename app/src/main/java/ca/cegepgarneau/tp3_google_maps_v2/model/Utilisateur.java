package ca.cegepgarneau.tp3_google_maps_v2.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "utilisateur_table")
public class Utilisateur {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "firstname_col")
    private String firstname;

    @NonNull
    @ColumnInfo(name = "lastname_col")
    private String lastname;

    @NonNull
    @ColumnInfo(name = "picture_col")
    private String picture;

    @NonNull
    @ColumnInfo(name = "latitude_col")
    private Double latitude;

    @NonNull
    @ColumnInfo(name = "longitude_col")
    private Double longitude;

    @NonNull
    @ColumnInfo(name = "message_col")
    private String message;

    /*public Utilisateur(@NonNull String firstname, @NonNull String lastname,
                       @NonNull String picture, @NonNull Float latitude,
                       @NonNull Float longitude, @NonNull String message){

        this.firstname = firstname;
        this.lastname = lastname;
        this.picture = picture;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
    }*/

    public Utilisateur() {

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(@NonNull String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public void setLastname(@NonNull String lastname) {
        this.lastname = lastname;
    }

    public String getPicture() {
        return picture;
    }
    public void setPicture(@NonNull String picture) { this.picture = picture; }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(@NonNull Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() { return longitude; }
    public void setLongitude(@NonNull Double longitude) {
        this.longitude = longitude;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(@NonNull String message) { this.message = message; }



}


