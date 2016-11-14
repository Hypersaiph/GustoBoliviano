package com.fuzzyapps.gustoboliviano;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Geovani on 09/09/2016
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String status;
    public String email;
    public String gender;
    public String phone;
    public String image_url;
    public int reviewsNumber;
    public int followingNumber;
    public int followersNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String status, String email, String gender, String phone) {
        this.name = name;
        this.status = status;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
    }

    public User(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getReviewsNumber() {
        return reviewsNumber;
    }

    public void setReviewsNumber(int reviewsNumber) {
        this.reviewsNumber = reviewsNumber;
    }

    public int getFollowingNumber() {
        return followingNumber;
    }

    public void setFollowingNumber(int followingNumber) {
        this.followingNumber = followingNumber;
    }

    public int getFollowersNumber() {
        return followersNumber;
    }

    public void setFollowersNumber(int followersNumber) {
        this.followersNumber = followersNumber;
    }
}