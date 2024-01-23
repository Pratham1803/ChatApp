package com.example.chatapp.ui.users;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Params;
import com.example.chatapp.R;
import com.example.chatapp.UserModel;
import com.example.chatapp.databinding.FragmentUsersBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private Params params;
    private FragmentUsersBinding binding;
    private RecyclerView recyclerViewUsers;
    private UsersAdapter customAdapter;
    private List<UserModel> lsUSer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.lsUSer = new ArrayList<>();
        this.recyclerViewUsers = root.findViewById(R.id.recyclerViewAllUsers);
        this.recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        this.customAdapter = new UsersAdapter(this.lsUSer,getContext());
        this.recyclerViewUsers.setAdapter(customAdapter);

        Params.getREFERENCE().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lsUSer.clear();
                        for (DataSnapshot post : snapshot.getChildren()){
                            if(post.getKey().equals(Params.getCURRENT_USER()))
                                continue;
                            UserModel newUser = post.getValue(UserModel.class);
                            newUser.setUserId(post.getKey());

                            DataSnapshot s = post.child(Params.getFRIENDS());
                            ArrayList<String> lsFriends = new ArrayList<>();
                            if(s.exists()) {
                                Log.d("UserRecord", "onDataChange: " + s.getChildren());
                                for(DataSnapshot childSnap : s.getChildren())
                                    lsFriends.add(childSnap.getValue().toString());
                            }
                            newUser.setFriends(lsFriends);

                            ArrayList<String> lsRequests = new ArrayList<>();
                            s = post.child(Params.getREQUESTS());
                            if(s.exists()) {
                                Log.d("UserRecord", "onDataChange: " + s.getChildren());
                                for(DataSnapshot childSnap : s.getChildren())
                                    lsRequests.add(childSnap.getValue().toString());
                            }
                            newUser.setRequests(lsRequests);
                            lsUSer.add(newUser);
                        }
                        customAdapter.notifyItemInserted(lsUSer.size());
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