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
    private Timestamp timestamp;
    private String token;
    private long tstamp;

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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getToken() {
        return token;
    }

    public long getTstamp() {
        return tstamp;
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

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTstamp(long tstamp) {
        this.tstamp = tstamp;
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
        userData.setTimestamp(doc.getTimestamp("timestamp"));
        userData.setTstamp(doc.getLong("tstamp"));

        return userData;
    }
    public static UserData makeUserData(Map<String, String> data){
        UserData userData = new UserData();
        userData.setId(Long.valueOf(data.get("id")));
        userData.setName(data.get("name"));
        userData.setSurname(data.get("surname"));
        userData.setSex(data.get("sex"));
        userData.setAge(Long.valueOf(data.get("age")));

        return userData;
    }
}
