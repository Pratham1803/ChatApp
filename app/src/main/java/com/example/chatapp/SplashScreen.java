package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Space;

import com.example.chatapp.ui.chat.Message;
import com.example.chatapp.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        String ScreenToOpen;
        try {
        ScreenToOpen = getIntent().getStringExtra("Screen");
        }catch (Exception e){ScreenToOpen = null;}

        if(ScreenToOpen != null){
            try {
                String frndUserId = getIntent().getExtras().getString("userId");

                Log.d("DataObj", "onCreate: Splash id = "+ frndUserId);
                Log.d("DataObj", "onCreate: Splash Screen = "+ ScreenToOpen);

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();

                if (ScreenToOpen.equals("Chat"))
                    openChatScreen(frndUserId);
                else if (ScreenToOpen.equals("Profile"))
                    openProfileScreen();
            }catch (Exception e){
                Log.d("ErrorMsg", "onCreate: Splash Screen "+e.getMessage());
            }
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Params params = new Params();
                    if (Params.getAUTH().getCurrentUser() == null){
                        Intent i = new Intent(SplashScreen.this, Login.class);
                        startActivity(i);
                        finish();
                    }else {
                        fillCurrentUser();
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }
                }
            }, 1500);
        }
    }

    private void openChatScreen(String frndUserId){
        Params.getREFERENCE().child(frndUserId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            UserModel newUser = snapshot.getValue(UserModel.class);
                            newUser.setUserId(snapshot.getKey());

                            if(snapshot.child(Params.getFcmToken()).exists())
                                newUser.setFCM_USER_TOKEN(snapshot.child(Params.getFcmToken()).getValue().toString());

                            Bundle bundle = new Bundle();
                            bundle.putString("frndUID",newUser.getUserId());
                            bundle.putString("frndName",newUser.getUserName());
                            bundle.putString("frndPic",newUser.getUserProfilePic());
                            bundle.putString("fcmToken",newUser.getFCM_USER_TOKEN());

                            Intent intent = new Intent(SplashScreen.this, Message.class);
                            intent.putExtra("userData",bundle);
                            startActivity(intent);
                        }catch (Exception e){
                            Log.d("ErrorMsg", "onDataChange: Splash Screen "+e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }
    private void openProfileScreen(){
        Fragment fragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_view,fragment);
    }
    private void setFcmToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(
                new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Params.getCurrentUserModel().setFCM_USER_TOKEN(s);
                        Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getFcmToken()).setValue(s);
                    }
                }
        );
    }

    public void fillCurrentUser(){
        Params.getREFERENCE().child(Params.getAUTH().getCurrentUser().getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // user id, name and number set
                        UserModel user = dataSnapshot.getValue(UserModel.class);

                        setFcmToken();

                        user.setUserId(dataSnapshot.getKey());

                        // users friend list
                        DataSnapshot s = dataSnapshot.child(Params.getFRIENDS());
                        ArrayList<String> lsFriends = new ArrayList<>();
                        if(s.exists()) {
                            Log.d("UserRecord", "onDataChange: " + s.getChildren());
                            for(DataSnapshot childSnap : s.getChildren())
                                lsFriends.add(childSnap.getValue().toString());
                        }
                        user.setFriends(lsFriends);

                        // users requests list
                        ArrayList<String> lsRequests = new ArrayList<>();
                        s = dataSnapshot.child(Params.getREQUESTS());
                        if(s.exists()) {
                            Log.d("UserRecord", "onDataChange: " + s.getChildren());
                            for(DataSnapshot childSnap : s.getChildren())
                                lsRequests.add(childSnap.getValue().toString());
                        }
                        user.setRequests(lsRequests);

                        Params.setCurrentUserModel(user);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }
}