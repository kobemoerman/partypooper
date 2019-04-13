package com.application.android.partypooper.Model;

public class Event {

  private String date;
  private String description;
  private String host;
  private String imageURL;
  private String location;
  private String name;
  private String theme;
  private String time;
  private String time_stamp;

  public Event(String date, String description, String host, String imageURL, String location, String name, String theme, String time, String time_stamp) {
    this.date = date;
    this.description = description;
    this.host = host;
    this.imageURL = imageURL;
    this.location = location;
    this.name = name;
    this.theme = theme;
    this.time = time;
    this.time_stamp = time_stamp;
  }

  public Event () {}

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

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTheme() {
    return theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getTime_stamp() {
    return time_stamp;
  }

  public void setTime_stamp(String time_stamp) {
    this.time_stamp = time_stamp;
  }
}
