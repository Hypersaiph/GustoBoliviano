package com.fuzzyapps.gustoboliviano;

import java.util.Map;

/**
 * Created by Geovani on 22/11/2016.
 */

public class Favorites {
    String productID;
    String establishmentID;
    public Map<String, String> timestamp;
    public boolean active;
    public long postedOn;
    public String id;
    public Favorites(){

    }
    public Favorites(String productID, String establishmentID, Map<String, String> timestamp, boolean active) {
        this.productID = productID;
        this.establishmentID = establishmentID;
        this.timestamp = timestamp;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getEstablishmentID() {
        return establishmentID;
    }

    public void setEstablishmentID(String establishmentID) {
        this.establishmentID = establishmentID;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }

    public long getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(long postedOn) {
        this.postedOn = postedOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
