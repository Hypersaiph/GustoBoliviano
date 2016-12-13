package com.fuzzyapps.gustoboliviano;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Map;

/**
 * Created by Geovani on 19/10/2016
 */
@IgnoreExtraProperties
public class ReviewForm {
    public String userID;
    public String establishmentID;
    public String productID;
    public String title;
    public String description;
    public double rating;
    public Map<String, String> timestamp;
    public long postedOn;
    public String id;
    public boolean visible;
    public ReviewForm(){

    }
    public ReviewForm(String userID, String establishmentID, String title, String description, double rating, Map<String, String> timestamp, String productID, boolean visible) {
        this.userID = userID;
        this.establishmentID = establishmentID;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.timestamp = timestamp;
        this.productID = productID;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPostedOn() {
        return postedOn;
    }
    public void setPostedOn(long postedOn) {
        this.postedOn = postedOn;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEstablishmentID() {
        return establishmentID;
    }

    public void setEstablishmentID(String establishmentID) {
        this.establishmentID = establishmentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }
}
