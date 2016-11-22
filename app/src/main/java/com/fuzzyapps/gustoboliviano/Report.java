package com.fuzzyapps.gustoboliviano;

import java.util.Map;

/**
 * Created by Geovani on 21/11/2016.
 */

public class Report {
    public String link;
    public String userID;
    public String reportedUserID;
    public String reportType;
    public String itemType;
    public Map<String, String> timestamp;
    public String id;

    public Report(String link, String userID, String reportedUserID, String reportType, String itemType, Map<String, String> timestamp) {
        this.link = link;
        this.userID = userID;
        this.reportedUserID = reportedUserID;
        this.reportType = reportType;
        this.itemType = itemType;
        this.timestamp = timestamp;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
