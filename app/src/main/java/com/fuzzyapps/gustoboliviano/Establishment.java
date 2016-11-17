package com.fuzzyapps.gustoboliviano;

/**
 * Created by Geovani on 18/10/2016.
 */

public class Establishment {
    public String id;              //User ID
    public String banner_url;      //url to banner image
    public String date;            //registration date
    public String description;     //restaurant´s description
    public String email;           //restaurant´s email
    public String image_url;       //url to icon image
    public String name;            //restaurant´s name
    public String type;            //type of restaurant
    public String openFromDay;     //first day of opening
    public String openToDay;       //last day of opening
    public String openTime;        //starting open time
    public String closeTime;       //final time when restaurants handles clients
    public String phone;           //restaurant´s phone number
    public String webPage;         //restaurant´s web page
    public String address;         //restaurant´s address
    public double rating;          //restaurant´s rating
    public int following;          //number of people that´s being followed by the restaurant
    public int followers;          //restaurant´s number of followers
    public boolean open;           //restaurant´s status open or close
    public boolean validated;       //validation mark

    public Establishment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Establishment(String id, String banner_url, String date, String description, String email, String image_url, String name, String type, String openFromDay, String openToDay, String openTime, String closeTime, String phone, String webPage, String address, double rating, int following, int followers, boolean open, boolean validated) {
        this.id = id;
        this.banner_url = banner_url;
        this.date = date;
        this.description = description;
        this.email = email;
        this.image_url = image_url;
        this.name = name;
        this.type = type;
        this.openFromDay = openFromDay;
        this.openToDay = openToDay;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.phone = phone;
        this.webPage = webPage;
        this.address = address;
        this.rating = rating;
        this.following = following;
        this.followers = followers;
        this.open = open;
        this.validated = validated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBanner_url() {
        return banner_url;
    }

    public void setBanner_url(String banner_url) {
        this.banner_url = banner_url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpenFromDay() {
        return openFromDay;
    }

    public void setOpenFromDay(String openFromDay) {
        this.openFromDay = openFromDay;
    }

    public String getOpenToDay() {
        return openToDay;
    }

    public void setOpenToDay(String openToDay) {
        this.openToDay = openToDay;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}