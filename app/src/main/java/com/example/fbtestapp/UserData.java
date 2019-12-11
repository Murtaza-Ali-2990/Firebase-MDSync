package com.example.fbtestapp;


import android.content.Intent;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class UserData {
    private long id;
    private String name;
    private String surname;
    private String sex;
    private long age;
    private String token;
    private long updates;

    UserData(){}
    UserData(String name, String surname, String sex, long age){
        this.age = age;
        this.sex = sex;
        this.name = name;
        this.surname = surname;
    }

    public long getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getSurname() {
        return surname;
    }

    public String getToken() {
        return token;
    }

    public long getUpdates() {
        return updates;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUpdates(long updates) {
        this.updates = updates;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static UserData makeUserData(DocumentSnapshot doc){
        UserData userData = new UserData();
        userData.setId(doc.getLong("id"));
        userData.setName(doc.getString("name"));
        userData.setSurname(doc.getString("surname"));
        userData.setSex(doc.getString("sex"));
        userData.setAge(doc.getLong("age"));
        userData.setToken(doc.getString("token"));
        userData.setUpdates(doc.getLong("updates"));

        return userData;
    }
    public static UserData makeUserData(Map<String, String> data){
        UserData userData = new UserData();
        userData.setId(Long.valueOf(data.get("id")));
        userData.setName(data.get("name"));
        userData.setSurname(data.get("surname"));
        userData.setSex(data.get("sex"));
        userData.setAge(Long.valueOf(data.get("age")));
        userData.setToken(data.get("token"));
        userData.setUpdates(Long.valueOf(data.get("updates")));

        return userData;
    }
}
