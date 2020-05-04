package com.developer.sagar.prevoice;

public class User {
    String image,name,uid,age,country;

    public User() {
    }

    public User(String image, String name, String uid, String age, String country) {
        this.image = image;
        this.name = name;
        this.uid = uid;
        this.age = age;
        this.country = country;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
