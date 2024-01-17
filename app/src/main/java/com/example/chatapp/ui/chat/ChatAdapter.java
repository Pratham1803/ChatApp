package com.example.chatapp.ui.chat;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Params;
import com.example.chatapp.R;
import com.example.chatapp.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    List<UserModel> localDataSet;
    Context context;
    Params params;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imgUserProfile;
        private final Button btnAddUser;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.txtUserListName);
            imgUserProfile = (ImageView) view.findViewById(R.id.imgUserProfile);
            btnAddUser = (Button) view.findViewById(R.id.btnAddUserInList);
            btnAddUser.setVisibility(View.INVISIBLE);
        }

        public ImageView getImgUserProfile() {
            return imgUserProfile;
        }
        public TextView getTextView() {
            return textView;
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
        holder.getTextView().setText(localDataSet.get(position).getUserName());

        if(localDataSet.get(position).getUserProfilePic() != null)
            Glide.with(context).load(localDataSet.get(position).getUserProfilePic()).into(holder.getImgUserProfile());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}