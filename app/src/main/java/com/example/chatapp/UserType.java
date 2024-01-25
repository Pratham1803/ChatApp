package com.example.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONObject;

public class UserType {
    private final Context context;

    public UserType(Context context){
        this.context = context;
    }
    public void approveFriend(Button btn, String frndUID){
        Params.getREFERENCE().child(frndUID).child(Params.getFRIENDS()).push().setValue(Params.getCurrentUserModel().getUserId()).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getFRIENDS()).push().setValue(frndUID).addOnSuccessListener(
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

        Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getREQUESTS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(frndUser)){
                                Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getREQUESTS()).child(post.getKey()).removeValue();
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
                            if(post.getValue().toString().equals(Params.getCurrentUserModel().getUserId())){
                                Params.getREFERENCE().child(frndUser).child(Params.getREQUESTS()).child(post.getKey()).removeValue();
                                Toast.makeText(context, "New Friend Added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        btn.setEnabled(false);
        Params.getREFERENCE().child(frndUser).child(Params.getFcmToken()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String FCM_TOKEN = dataSnapshot.getValue().toString();
                        sentNotification(FCM_TOKEN);
                    }
                }
        );
    }

    // send notification of request accepted
    private void sentNotification(String FRND_FCM_TOKEN){
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title","New Friend Added!!");
            notificationObject.put("body", "Request Accepted by, "+Params.getCurrentUserModel().getUserName()+".\nStart Chatting Now!!");
            notificationObject.put("image", Params.getCurrentUserModel().getUserProfilePic());
            notificationObject.put("myicon", "@drawable/ic_stat_name");

            JSONObject dataObj = new JSONObject();
            dataObj.put("Screen","Chat");
            dataObj.put("userId",Params.getCurrentUserModel().getUserId());

            jsonObject.put("notification",notificationObject);
            jsonObject.put("data",dataObj);

            jsonObject.put("to",FRND_FCM_TOKEN);
            PushNotification.callApi(jsonObject);
        }catch (Exception e){
            Log.d("ErrorMsg", "sendNotification: "+e.toString());
        }
    }

    // request cancel
    public void rejectRequest(Button b,String FrndUID){
        Params.getREFERENCE().child(FrndUID).child(Params.getREQUESTS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post: dataSnapshot.getChildren()) {
                            if (post.getValue().toString().equals(Params.getCurrentUserModel().getUserId()))
                                Params.getREFERENCE().child(FrndUID).child(Params.getREQUESTS()).child(post.getKey()).removeValue();
                        }
                    }
                }
        );
    }

    // remove friend
    public void removeFrnd(Button btn,String frndUser){
        btn.setBackgroundColor(Color.GRAY);
        Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getFRIENDS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(frndUser)){
                                Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getFRIENDS()).child(post.getKey()).removeValue();
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
                            if(post.getValue().toString().equals(Params.getCurrentUserModel().getUserId())){
                                Params.getREFERENCE().child(frndUser).child(Params.getFRIENDS()).child(post.getKey()).removeValue();
                                Toast.makeText(context, "Friend Removed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getCHAT()).child(frndUser).removeValue();
        Params.getREFERENCE().child(frndUser).child(Params.getCHAT()).child(Params.getCurrentUserModel().getUserId()).removeValue();

        btn.setText("Removed");
        btn.setEnabled(false);
    }
}