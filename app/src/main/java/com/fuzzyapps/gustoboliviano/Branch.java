package com.fuzzyapps.gustoboliviano;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Geovani on 09/09/2016
 */

@IgnoreExtraProperties
public class Branch {
    public String id;
    public String address;
    public String phone;
    public String latitude;
    public String longitude;
    public Branch(){

    }

    public Branch(String address, String phone, String latitude, String longitude) {
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}