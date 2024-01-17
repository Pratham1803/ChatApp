package com.example.chatapp;

public class UsersModel {
    public String userId;
    public String userContactNum;
    public String userName;
    public String userProfilePic;

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public UsersModel(){}



    public UsersModel(String userContactNum, String userName) {
        this.userContactNum = userContactNum;
        this.userName = userName;
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
}
