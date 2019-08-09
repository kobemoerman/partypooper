package com.application.android.partypooper.Model;

public class Event {

  private String start_date;
  private String end_date;
  private String start_time;
  private String end_time;
  private String description;
  private String host;
  private String host_username;
  private String imageURL;
  private String number;
  private String street;
  private String city;
  private String name;
  private String theme;
  private String time;
  private String time_stamp;
  private String date_stamp;

  public Event(String start_date, String end_date, String start_time, String end_time, String description, String host, String host_username, String imageURL, String number, String street, String city, String name, String theme, String time, String time_stamp, String date_stamp) {
    this.start_date = start_date;
    this.end_date = end_date;
    this.start_time = start_time;
    this.end_time = end_time;
    this.description = description;
    this.host = host;
    this.host_username = host_username;
    this.imageURL = imageURL;
    this.number = number;
    this.street = street;
    this.city = city;
    this.name = name;
    this.theme = theme;
    this.time = time;
    this.time_stamp = time_stamp;
    this.date_stamp = date_stamp;
  }

  public Event () {}

  public String getStart_date() {
    return start_date;
  }

  public void setStart_date(String start_date) {
    this.start_date = start_date;
  }

  public String getEnd_date() {
    return end_date;
  }

  public void setEnd_date(String end_date) {
    this.end_date = end_date;
  }

  public String getStart_time() {
    return start_time;
  }

  public void setStart_time(String start_time) {
    this.start_time = start_time;
  }

  public String getEnd_time() {
    return end_time;
  }

  public void setEnd_time(String end_time) {
    this.end_time = end_time;
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

  public String getHost_username() {
    return host_username;
  }

  public void setHost_username(String host_username) {
    this.host_username = host_username;
  }

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
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

  public String getDate_stamp() {
    return date_stamp;
  }

  public void setDate_stamp(String date_stamp) {
    this.date_stamp = date_stamp;
  }
}
