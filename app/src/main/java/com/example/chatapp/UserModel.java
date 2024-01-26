package com.example.chatapp;

import java.util.ArrayList;

public class UserModel {
    private String userId;
    private String userContactNum;
    private String userName;
    private String userProfilePic;
    private ArrayList<String> requests;
    private ArrayList<String> friends;
    private String FCM_USER_TOKEN;

    public UserModel(){}

    public UserModel(String userContactNum, String userName) {
        this.userContactNum = userContactNum;
        this.userName = userName;
    }
    public String getUserId() {
        return this.userId;
    }

    public String getFCM_USER_TOKEN() {
        return FCM_USER_TOKEN;
    }

    public void setFCM_USER_TOKEN(String FCM_USER_TOKEN) {
        this.FCM_USER_TOKEN = FCM_USER_TOKEN;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserContactNum() {
        return userContactNum;
    }

    public void setUserContactNum(String userContactNum) {
        this.userContactNum = userContactNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public ArrayList<String> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<String> requests) {
        this.requests = requests;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }
}