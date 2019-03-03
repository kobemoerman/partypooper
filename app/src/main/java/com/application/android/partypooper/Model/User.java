package com.application.android.partypooper.Model;

public class User {

    private String id;
    private String username;
    private String age;
    private String status;
    private String gender;

    public User(String id, String username, String age, String status, String gender) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.status = status;
        this.gender = gender;
    }

    public User () {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
