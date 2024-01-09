package com.example.chatapp.ui.users;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.UserModel;
import com.example.chatapp.UsersModel;
import com.example.chatapp.databinding.FragmentUsersBinding;
import  com.example.chatapp.R;
import com.example.chatapp.ui.chat.CustAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    // Firebase
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    // usefulvarriables
    String UID = auth.getCurrentUser().getUid().toString();

    private FragmentUsersBinding binding;
    RecyclerView recyclerViewUsers;
    CustomAdapter customAdapter;
    List<UsersModel> users;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UsersViewModel notificationsViewModel =
                new ViewModelProvider(this).get(UsersViewModel.class);

        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        users= new ArrayList<>();
        customAdapter = new CustomAdapter(users,UID);
        recyclerViewUsers = root.findViewById(R.id.recyclerViewAllUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsers.setAdapter(customAdapter);

        db.getReference("tblUser").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot post : snapshot.getChildren()) {
                            UsersModel newUser = post.getValue(UsersModel.class);
                            newUser.setUserId(post.getKey());
                            Log.d("DataCollect", "onDataChange: "+post.getKey());
                            Log.d("DataCollect", "onDataChange: Name = "+newUser.getUserName());
                            Log.d("DataCollect", "onDataChange: Name = "+newUser.getUserProfilePic());

                            users.add(newUser);
                        }

                        customAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}