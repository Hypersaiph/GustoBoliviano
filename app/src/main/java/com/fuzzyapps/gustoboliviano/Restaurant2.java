package com.fuzzyapps.gustoboliviano;

/**
 * Created by Geovani on 18/10/2016.
 */

public class Restaurant2 {
    private String id;
    private String banner_url;      //url to banner image
    private String date;            //registration date
    private String description;     //restaurant´s description
    private String direction;       //restaurant´s direction
    private String email;           //restaurant´s email
    private String imagen_url;       //url to icon image
    private String licencia;            //restaurant´s name
    private String name;            //type of restaurant
    private String nit;     //first day of opening
    private String type;       //last day of opening
    private boolean validated;        //starting open time
    private Restaurant2() {}
    public Restaurant2(String banner_url, String date, String description, String direction, String email, String imagen_url, String licencia, String name, String nit, String type, boolean validated) {
        super();
        this.banner_url = banner_url;
        this.date = date;
        this.description = description;
        this.direction = direction;
        this.email = email;
        this.imagen_url = imagen_url;
        this.licencia = licencia;
        this.name = name;
        this.nit = nit;
        this.type = type;
        this.validated = validated;
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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagen_url() {
        return imagen_url;
    }

    public void setImagen_url(String imagen_url) {
        this.imagen_url = imagen_url;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}