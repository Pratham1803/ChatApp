package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.chatapp.ui.chat.ChatFragment;
import com.example.chatapp.ui.profile.ProfileFragment;
import com.example.chatapp.ui.users.UsersFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.chatapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav_view);

        Params params = new Params();
        if (Params.getAUTH().getCurrentUser() == null){
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();
       }else {
            // fill user details
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

            bottomNavigationView.setOnItemSelectedListener(
                    new NavigationBarView.OnItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            if(item.getItemId() == R.id.navigation_profile){
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_view,new ProfileFragment()).commit();
                            }
                            if(item.getItemId() == R.id.navigation_chat){
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_view,new ChatFragment()).commit();
                            }
                            if(item.getItemId() == R.id.navigation_users){
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_view,new UsersFragment()).commit();
                            }
                            return true;
                        }
                    }
            );
            bottomNavigationView.setSelectedItemId(R.id.navigation_chat);
        }
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
}