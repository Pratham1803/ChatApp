package com.example.chatapp;

import java.util.ArrayList;
import java.util.List;
import java.util.List;

public class UserModel {
    private String userId;
    private String userContactNum;
    private String userName;
    private String userProfilePic;
    private ArrayList<String> requests;
    private ArrayList<String> friends;

    public UserModel(){}

    public UserModel(String userContactNum, String userName, String userProfilePic) {
        this.userContactNum = userContactNum;
        this.userName = userName;
        this.userProfilePic = userProfilePic;
    }

    public UserModel(String userId, String userContactNum, String userName, String userProfilePic, ArrayList<String> friends) {
        this.userId = userId;
        this.userContactNum = userContactNum;
        this.userName = userName;
        this.userProfilePic = userProfilePic;
        this.friends = friends;
    }
    public String getUserId() {
        return userId;
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
