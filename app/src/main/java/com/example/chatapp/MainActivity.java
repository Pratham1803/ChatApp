package com.example.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.chatapp.ui.chat.ChatFragment;
import com.example.chatapp.ui.profile.ProfileAdapter;
import com.example.chatapp.ui.profile.ProfileFragment;
import com.example.chatapp.ui.users.UsersFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    SearchView searchView;
    ProfileFragment profileFragment;
    ChatFragment chatFragment;
    UsersFragment usersFragment;

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
            profileFragment = new ProfileFragment();
            chatFragment = new ChatFragment();
            usersFragment = new UsersFragment();
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

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
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_view,profileFragment).commit();
                            }
                            if(item.getItemId() == R.id.navigation_chat){
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_view,chatFragment).commit();
                            }
                            if(item.getItemId() == R.id.navigation_users){
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame_view,usersFragment).commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.btnSearch);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                btnSearch_Clicked(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                btnSearch_Clicked(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menuLogOut) {
            logOut();
        }

        return true;
    }

    private void logOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Are you sure to LogOut ?");

        // Set Alert Title
        builder.setTitle("Log Out !");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Params.getAUTH().signOut();
                MainActivity.this.recreate();
            }
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }

    private void btnSearch_Clicked(String text){
        Fragment current_Fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_frame_view);

        if(current_Fragment instanceof ChatFragment){
            chatFragment.funSearch(text);
        } else if (current_Fragment instanceof UsersFragment) {
            usersFragment.funSearch(text);
        }
    }
}