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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{
    List<UserModel> localDataSet;
    Context context;
    Params params;
    final String CURRENT_BTN;
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
        this.params = new Params();
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
        if(CURRENT_BTN.equals(params.getREQUESTS())) {
            holder.getBtnAddUser().setText("Approve");
            holder.getBtnAddUser().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveFriend(holder.getBtnAddUser(), frndUser);
                }
            });
        } else if (CURRENT_BTN.equals(params.getFRIENDS())) {
            holder.getBtnAddUser().setText("Remove");
            holder.getBtnAddUser().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFrnd(holder.getBtnAddUser(),frndUser);
                    localDataSet.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    // Approve Request
    private void approveFriend(Button btn,String frndUID){
        params.getREFERENCE().child(frndUID).child(params.getFRIENDS()).push().setValue(params.getCURRENT_USER()).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getFRIENDS()).push().setValue(frndUID).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        requestAccept(btn,frndUID);
                                    }
                                }
                        );
                    }
                }
        );
    }

    // friend request accepted
    private void requestAccept(Button btn,String frndUser){
        btn.setBackgroundColor(Color.GREEN);
        btn.setText("Added");
        params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getREQUESTS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(frndUser)){
                                params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getREQUESTS()).child(post.getKey()).removeValue();
                            }
                        }
                    }
                }
        );
        btn.setEnabled(false);
        Toast.makeText(context, "New Friend Added", Toast.LENGTH_SHORT).show();
    }

    // remove friend
    private void removeFrnd(Button btn,String frndUser){
        btn.setBackgroundColor(Color.GRAY);
        params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getFRIENDS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(frndUser)){
                                params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getFRIENDS()).child(post.getKey()).removeValue();
                            }
                        }
                    }
                }
        );
        params.getREFERENCE().child(frndUser).child(params.getFRIENDS()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot post : dataSnapshot.getChildren()){
                            if(post.getValue().toString().equals(params.getCURRENT_USER())){
                                params.getREFERENCE().child(frndUser).child(params.getFRIENDS()).child(post.getKey()).removeValue();
                            }
                        }
                    }
                }
        );

        params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getCHAT()).child(frndUser).removeValue();
        params.getREFERENCE().child(frndUser).child(params.getCHAT()).child(params.getCURRENT_USER()).removeValue();

        btn.setText("Removed");
        btn.setEnabled(false);
        Toast.makeText(context, "Friend Removed", Toast.LENGTH_SHORT).show();
    }
}