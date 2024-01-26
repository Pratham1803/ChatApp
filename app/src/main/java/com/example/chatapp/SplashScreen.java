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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
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
}