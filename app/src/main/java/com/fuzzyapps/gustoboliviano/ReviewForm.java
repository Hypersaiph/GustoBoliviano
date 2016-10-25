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

}
