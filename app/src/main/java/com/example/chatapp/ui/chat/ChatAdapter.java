package com.example.chatapp.ui.chat;
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

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    List<UserModel> localDataSet;
    Context context;
    Params params;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProfilePic;
        private final TextView txtUserName;
        private final Button button;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imgProfilePic = view.findViewById(R.id.imgUserProfile);
            txtUserName = view.findViewById(R.id.txtUserListName);
            button = view.findViewById(R.id.btnAddUserInList);
            button.setVisibility(View.INVISIBLE);
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