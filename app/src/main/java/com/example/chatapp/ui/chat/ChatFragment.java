package com.example.chatapp.ui.chat;

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

import com.example.chatapp.Params;
import com.example.chatapp.R;
import com.example.chatapp.UserModel;
import com.example.chatapp.databinding.FragmentChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private View root;
    private @NonNull FragmentChatBinding binding;

    // Data members
    RecyclerView recyclerView;
    List<UserModel> lsUsers;
    private ChatAdapter chatAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        this.root = binding.getRoot();

        // initilizing
        this.recyclerView = root.findViewById(R.id.recyclerViewChat);
        this.lsUsers = new ArrayList<>();

        this.chatAdapter = new ChatAdapter(lsUsers,root.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(chatAdapter);

        Params.getREFERENCE().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot post : snapshot.getChildren()){
                            Log.d("chatScreen", "addDetials: "+post.getKey());
                            if(Params.getCurrentUserModel().getFriends().contains(post.getKey())){
                                UserModel newUser = post.getValue(UserModel.class);
                                newUser.setUserId(post.getKey());
                                Log.d("chatScreen", "onDataChange: "+newUser.getUserName());
                                lsUsers.add(newUser);
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
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