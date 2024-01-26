package com.example.chatapp.ui.chat;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Params;
import com.example.chatapp.R;
import com.example.chatapp.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    List<UserModel> localDataSet;
    Context context;
    Params params;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProfilePic;
        private final TextView txtUserName;
        private final TextView txtMessage;
        private final Button button;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imgProfilePic = view.findViewById(R.id.imgUserProfile);
            txtUserName = view.findViewById(R.id.txtUserListName);
            txtMessage = view.findViewById(R.id.txtMessage);

            button = view.findViewById(R.id.btnAddUserInList);
            button.setVisibility(View.GONE);
        }

        public TextView getTxtMessage() {
            return txtMessage;
        }

        public ImageView getImgProfilePic() {
            return imgProfilePic;
        }

        public TextView getTxtUserName() {
            return txtUserName;
        }
    }

    public ChatAdapter(List<UserModel> dataSet, Context con){
        Log.d("chatScreen", "ChatAdapter: "+dataSet);
        this.localDataSet = dataSet;
        this.context = con;
        this.params = new Params();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_users, parent, false);

            return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.getTxtUserName().setText(localDataSet.get(position).getUserName());

        if(localDataSet.get(position).getUserProfilePic()!=null)
            Glide.with(context).load(localDataSet.get(position).getUserProfilePic()).into(holder.getImgProfilePic());

        Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getCHAT()).child(localDataSet.get(position).getUserId())
                .orderByKey().limitToLast(1)
                .addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot post: snapshot.getChildren()){
                            String msg = post.child("chat").getValue().toString();
                            String user = post.child("user_ID").getValue().toString();

                            if(user.equals(Params.getCurrentUserModel().getUserId()))
                                msg = "You : "+msg;
                            else
                                msg = localDataSet.get(position).getUserName() + " : "+msg;

                            holder.getTxtMessage().setText(msg);
                            holder.getTxtMessage().setVisibility(View.VISIBLE);
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("frndUID",localDataSet.get(position).getUserId());
                        bundle.putString("frndName",localDataSet.get(position).getUserName());
                        bundle.putString("frndPic",localDataSet.get(position).getUserProfilePic());
                        bundle.putString("fcmToken",localDataSet.get(position).getFCM_USER_TOKEN());

                        Log.d("chat", "onCreate: FCM = "+localDataSet.get(position).getFCM_USER_TOKEN());
                        Intent intent = new Intent(context, Message.class);
                        intent.putExtra("userData",bundle);
                        context.startActivity(intent);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}