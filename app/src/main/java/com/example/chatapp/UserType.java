package com.example.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

public class UserType {
    private final Context context;

    public UserType(Context context){
        this.context = context;
    }
    public void approveFriend(Button btn, String frndUID){
        Params.getREFERENCE().child(frndUID).child(Params.getFRIENDS()).push().setValue(Params.getCURRENT_USER()).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Params.getREFERENCE().child(Params.getCURRENT_USER()).child(Params.getFRIENDS()).push().setValue(frndUID).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        requestAccept(btn,frndUID);
                                    }
                                }
                        );
                    }
                }
        );
    }

    // friend request accepted
    private void requestAccept(Button btn,String frndUser){
        btn.setBackgroundColor(Color.GREEN);
        btn.setText("Added");

        Params.getREFERENCE().child(Params.getCURRENT_USER()).child(Params.getREQUESTS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(frndUser)){
                                Params.getREFERENCE().child(Params.getCURRENT_USER()).child(Params.getREQUESTS()).child(post.getKey()).removeValue();
                            }
                        }
                    }
                }
        );
        Params.getREFERENCE().child(frndUser).child(Params.getREQUESTS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(Params.getCURRENT_USER())){
                                Params.getREFERENCE().child(frndUser).child(Params.getREQUESTS()).child(post.getKey()).removeValue();
                                Toast.makeText(context, "New Friend Added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
        btn.setEnabled(false);
    }

    // request cancel
    public void rejectRequest(Button b,String FrndUID){
        Params.getREFERENCE().child(FrndUID).child(Params.getREQUESTS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post: dataSnapshot.getChildren()) {
                            if (post.getValue().toString().equals(Params.getCURRENT_USER()))
                                Params.getREFERENCE().child(FrndUID).child(Params.getREQUESTS()).child(post.getKey()).removeValue();
                        }
                    }
                }
        );
    }

    // remove friend
    public void removeFrnd(Button btn,String frndUser){
        btn.setBackgroundColor(Color.GRAY);
        Params.getREFERENCE().child(Params.getCURRENT_USER()).child(Params.getFRIENDS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(frndUser)){
                                Params.getREFERENCE().child(Params.getCURRENT_USER()).child(Params.getFRIENDS()).child(post.getKey()).removeValue();
                            }
                        }
                    }
                }
        );
        Params.getREFERENCE().child(frndUser).child(Params.getFRIENDS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(Params.getCURRENT_USER())){
                                Params.getREFERENCE().child(frndUser).child(Params.getFRIENDS()).child(post.getKey()).removeValue();
                                Toast.makeText(context, "Friend Removed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        Params.getREFERENCE().child(Params.getCURRENT_USER()).child(Params.getCHAT()).child(frndUser).removeValue();
        Params.getREFERENCE().child(frndUser).child(Params.getCHAT()).child(Params.getCURRENT_USER()).removeValue();

        btn.setText("Removed");
        btn.setEnabled(false);
    }
}