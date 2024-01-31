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

        profileFragment = new ProfileFragment();
        chatFragment = new ChatFragment();
        usersFragment = new UsersFragment();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                });
        bottomNavigationView.setSelectedItemId(R.id.navigation_chat);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure, to Log Out?");
            builder.setTitle("Log Out");
            builder.setIcon(R.drawable.app_icon);
            builder.setCancelable(false);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Params.getAUTH().signOut();
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return true;
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