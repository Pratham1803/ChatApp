package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.chatapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        Params params = new Params();
        if (auth.getCurrentUser() == null){
            Intent i = new Intent(this, Login.class);
            startActivity(i);
            finish();
       }else {
            // fill user details
            Params.getREFERENCE().child(auth.getCurrentUser().getUid()).addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // user id, name and number set
                            UserModel user = dataSnapshot.getValue(UserModel.class);

                            if(dataSnapshot.child(Params.getFcmToken()).exists())
                                user.setFCM_USER_TOKEN(dataSnapshot.child(Params.getFcmToken()).getValue().toString());

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

            PushNotification pushNotification = new PushNotification();
            pushNotification.getFcmToken();

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            BottomNavigationView navView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_chat, R.id.navigation_profile, R.id.navigation_users)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);
        }
    }
}