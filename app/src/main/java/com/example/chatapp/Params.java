package com.example.chatapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Params {
    private static String  TABLE;
    private static UserModel CURRENT_USER_MODEL;
    private static String CURRENT_USER;
    private static String  CONTACT_NUM;
    private static String NAME;
    private static String PROFILE_PIC;
    private static String  REQUESTS;
    private static String  FRIENDS;
    private static FirebaseDatabase DATABASE;
    private static DatabaseReference REFERENCE;
    private static StorageReference STORAGE;
    private static FirebaseAuth AUTH;
    private static String CHAT;
    private static String FCM_TOKEN;

    public Params(){
        TABLE = "tblUser";
        CONTACT_NUM = "userContactNum";
        NAME = "userName";
        PROFILE_PIC = "userProfilePic";
        FRIENDS = "friendList";
        REQUESTS = "requestsList";
        DATABASE = FirebaseDatabase.getInstance();
        REFERENCE = DATABASE.getReference(TABLE);
        CURRENT_USER = FirebaseAuth.getInstance().getCurrentUser().getUid();
        STORAGE = FirebaseStorage.getInstance().getReference();
        AUTH = FirebaseAuth.getInstance();
        CHAT = "Chat";
        FCM_TOKEN = "fcm_token";
    }

    public static UserModel getCurrentUserModel() {
        return CURRENT_USER_MODEL;
    }

    public static void setCurrentUserModel(UserModel currentUserModel) {
        CURRENT_USER_MODEL = currentUserModel;
    }

    public static String getCHAT() {
        return CHAT;
    }

    public static FirebaseAuth getAUTH() {
        return AUTH;
    }

    public static StorageReference getSTORAGE() {
        return STORAGE;
    }

    public static FirebaseDatabase getDATABASE() {
        return DATABASE;
    }

    public static DatabaseReference getREFERENCE() {
        return REFERENCE;
    }

    //public static String getCURRENT_USER() {
      //  return CURRENT_USER;
    //}

    public static String getTABLE() {
        return TABLE;
    }

    public static String getCONTACT_NUM() {
        return CONTACT_NUM;
    }

    public static String getNAME() {
        return NAME;
    }

    public static String getPROFILE_PIC() {
        return PROFILE_PIC;
    }

    public static String getREQUESTS() {
        return REQUESTS;
    }

    public static String getFRIENDS() {
        return FRIENDS;
    }

    public static String getFcmToken() {
        return FCM_TOKEN;
    }
}

