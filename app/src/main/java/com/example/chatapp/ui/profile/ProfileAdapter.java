package com.example.chatapp.ui.profile;

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
import com.example.chatapp.UserType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{
    List<UserModel> localDataSet;
    Context context;
    final String CURRENT_BTN;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imgUserProfile;
        private final Button btnAddUser;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = view.findViewById(R.id.txtUserListName);
            imgUserProfile = view.findViewById(R.id.imgUserProfile);
            btnAddUser = view.findViewById(R.id.btnAddUserInList);
        }

        public ImageView getImgUserProfile() {
            return imgUserProfile;
        }

        public Button getBtnAddUser() {
            return btnAddUser;
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public ProfileAdapter(List<UserModel> dataSet, Context con,final String CURRENT_BTN){
        this.localDataSet = dataSet;
        this.context = con;
        this.CURRENT_BTN = CURRENT_BTN;
    }

    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_users, parent, false);

        return new ProfileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        Log.d("ReqFrnd", "ProfileAdapter: Name "+localDataSet.get(position));
        holder.getTextView().setText(localDataSet.get(position).getUserName());

        if(localDataSet.get(position).getUserProfilePic() != null)
            Glide.with(context).load(localDataSet.get(position).getUserProfilePic()).into(holder.getImgUserProfile());

        String frndUser = localDataSet.get(position).getUserId();
        UserType userType = new UserType(context);
        if(CURRENT_BTN.equals(Params.getREQUESTS())) {
            holder.getBtnAddUser().setText("Approve");
            holder.getBtnAddUser().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userType.approveFriend(holder.getBtnAddUser(), frndUser);
                    localDataSet.remove(position);
                    notifyItemRemoved(position);
                    //approveFriend(holder.getBtnAddUser(), frndUser);
                }
            });
        } else if (CURRENT_BTN.equals(Params.getFRIENDS())) {
            holder.getBtnAddUser().setText("Remove");
            holder.getBtnAddUser().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userType.removeFrnd(holder.getBtnAddUser(),frndUser);
                    localDataSet.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}