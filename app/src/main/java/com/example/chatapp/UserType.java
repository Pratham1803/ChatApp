package com.example.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

public class UserType {
    private final Params params;
    private Context context;

    public UserType(Context context){
        this.params = new Params();
        this.context = context;
    }
    public void approveFriend(Button btn, String frndUID){
        params.getREFERENCE().child(frndUID).child(params.getFRIENDS()).push().setValue(params.getCURRENT_USER()).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getFRIENDS()).push().setValue(frndUID).addOnSuccessListener(
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
        params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getREQUESTS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(frndUser)){
                                params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getREQUESTS()).child(post.getKey()).removeValue();
                            }
                        }
                    }
                }
        );
        params.getREFERENCE().child(frndUser).child(params.getREQUESTS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(params.getCURRENT_USER())){
                                params.getREFERENCE().child(frndUser).child(params.getREQUESTS()).child(post.getKey()).removeValue();
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
        params.getREFERENCE().child(FrndUID).child(params.getREQUESTS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post: dataSnapshot.getChildren()) {
                            if (post.getValue().toString().equals(params.getCURRENT_USER()))
                                params.getREFERENCE().child(FrndUID).child(params.getREQUESTS()).child(post.getKey()).removeValue();
                        }
                    }
                }
        );
    }

    // remove friend
    public void removeFrnd(Button btn,String frndUser){
        btn.setBackgroundColor(Color.GRAY);
        params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getFRIENDS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(frndUser)){
                                params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getFRIENDS()).child(post.getKey()).removeValue();
                            }
                        }
                    }
                }
        );
        params.getREFERENCE().child(frndUser).child(params.getFRIENDS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(params.getCURRENT_USER())){
                                params.getREFERENCE().child(frndUser).child(params.getFRIENDS()).child(post.getKey()).removeValue();
                                Toast.makeText(context, "Friend Removed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getCHAT()).child(frndUser).removeValue();
        params.getREFERENCE().child(frndUser).child(params.getCHAT()).child(params.getCURRENT_USER()).removeValue();

        btn.setText("Removed");
        btn.setEnabled(false);
    }
}