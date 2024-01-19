package com.example.chatapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class Params {
    private final String  TABLE;
    private final String CURRENT_USER;
    private final String  CONTACT_NUM;
    private final String NAME;
    private final String PROFILE_PIC;
    private final String  REQUESTS;
    private final String  FRIENDS;
    private final FirebaseDatabase DATABASE;
    private final DatabaseReference REFERENCE;
    private final StorageReference STORAGE;
    private final FirebaseAuth AUTH;
    private final String CHAT;

    public Params(){
        this.TABLE = "tblUser";
        this.CONTACT_NUM = "userContactNum";
        this.NAME = "userName";
        this.PROFILE_PIC = "userProfilePic";
        this.FRIENDS = "friendList";
        this.REQUESTS = "requestsList";
        this.DATABASE = FirebaseDatabase.getInstance();
        this.REFERENCE = DATABASE.getReference(this.TABLE);
        this.CURRENT_USER = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.STORAGE = FirebaseStorage.getInstance().getReference();
        this.AUTH = FirebaseAuth.getInstance();
        this.CHAT = "Chat";
    }

    public String getCHAT() {
        return CHAT;
    }

    public FirebaseAuth getAUTH() {
        return AUTH;
    }

    public StorageReference getSTORAGE() {
        return STORAGE;
    }

    public FirebaseDatabase getDATABASE() {
        return DATABASE;
    }

    public DatabaseReference getREFERENCE() {
        return REFERENCE;
    }

    public String getCURRENT_USER() {
        return CURRENT_USER;
    }

    public String getTABLE() {
        return TABLE;
    }

    public String getCONTACT_NUM() {
        return CONTACT_NUM;
    }

    public String getNAME() {
        return NAME;
    }

    public String getPROFILE_PIC() {
        return PROFILE_PIC;
    }

    public String getREQUESTS() {
        return REQUESTS;
    }

    public String getFRIENDS() {
        return FRIENDS;
    }
}

