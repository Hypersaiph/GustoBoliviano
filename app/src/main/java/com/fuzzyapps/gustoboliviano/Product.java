package com.fuzzyapps.gustoboliviano;

/**
 * Created by Geovani on 25/10/2016.
 */

public class Product {
    public String id;
    public String image_url;        // url de la imagen
    public String description;        // url de la imagen
    public String name;             //nombre del producto
    public long date;             //nombre del producto
    public int likes;               //numero de likes
    public String price;               //Rango de 1 a 5
    public double rating;           //Rango de 0 a 5
    public int countRating;         //Rango de 0 a 5
    public boolean available;
    public Product(){

    }

    public Product(boolean available, String id, String image_url, String description, String name, long date, int likes, String price, double rating, int countRating) {
        this.available = available;
        this.id = id;
        this.image_url = image_url;
        this.description = description;
        this.name = name;
        this.date = date;
        this.likes = likes;
        this.price = price;
        this.rating = rating;
        this.countRating = countRating;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }



    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCountRating() {
        return countRating;
    }

    public void setCountRating(int countRating) {
        this.countRating = countRating;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
