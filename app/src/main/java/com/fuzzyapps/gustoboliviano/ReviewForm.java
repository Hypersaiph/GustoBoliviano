package com.fuzzyapps.gustoboliviano;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Map;

/**
 * Created by Geovani on 19/10/2016
 */
@IgnoreExtraProperties
public class ReviewForm {
    public String userID;
    public String restaurantID;
    public String title;
    public String description;
    public double rating;
    public Map<String, String> timestamp;
    public ReviewForm(){

    }
    public ReviewForm(String userID, String restaurantID, String title, String description, double rating, Map<String, String> timestamp) {
        super();
        this.userID = userID;
        this.restaurantID = restaurantID;
        this.title= title;
        this.description = description;
        this.rating = rating;
        this.timestamp = timestamp;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
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
