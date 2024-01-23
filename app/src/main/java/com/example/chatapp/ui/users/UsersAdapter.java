package com.example.chatapp.ui.users;

import android.annotation.SuppressLint;
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

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Params;
import com.example.chatapp.R;
import com.example.chatapp.UserModel;
import com.example.chatapp.UserType;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private final List<UserModel> localDataSet;
    Context context;
    UserType userType;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
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

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView
     */
    public UsersAdapter(List<UserModel> dataSet, Context con) {
        localDataSet = dataSet;
        context = con;
        userType = new UserType(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.all_users, viewGroup, false);

        return new UsersAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position).getUserName());
        if (localDataSet.get(position).getUserProfilePic() != null) {
            Glide.with(context).load(localDataSet.get(position).getUserProfilePic()).into(viewHolder.getImgUserProfile());
        }

        String frndUser = localDataSet.get(position).getUserId();

        if(localDataSet.get(position).getFriends().contains(Params.getCURRENT_USER())) {
            changeButtonFriend(viewHolder.getBtnAddUser(), frndUser,position);
        }
        else if (localDataSet.get(position).getRequests().contains(Params.getCURRENT_USER())) {
            changeButtonReuest(viewHolder.getBtnAddUser(), frndUser,position);
        }
        else {
            simpleBtn(viewHolder.getBtnAddUser(), localDataSet.get(position).getUserId(),position);
        }
        Log.d("frndLIST", "User : "+localDataSet.get(position).getUserName()+" Requests = "+localDataSet.get(position).getRequests());
    }

    void changeButtonReuest(Button btn,String UID,int pos){
        btn.setBackgroundColor(Color.GRAY);
        btn.setText("Request Sent");
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userType.rejectRequest(btn,UID);
                        simpleBtn(btn,UID,pos);
                    }
                }
        );
    }
    void changeButtonFriend(Button btn,String name,int pos){
        btn.setBackgroundColor(Color.GREEN);
        btn.setTextColor(Color.WHITE);
        btn.setText("Friend");
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userType.removeFrnd(btn,name);
                        simpleBtn(btn,name,pos);
                    }
                }
        );
    }

    void simpleBtn(Button btn,String frndUser,int pos){
        btn.setText("Add Friend");
        btn.setBackgroundColor(context.getResources().getColor(R.color.purple_500));
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Params.getREFERENCE().child(frndUser).child(Params.getREQUESTS()).push().setValue(Params.getCURRENT_USER()).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Request Sent!", Toast.LENGTH_SHORT).show();
                                        changeButtonReuest(btn,frndUser,pos);
                                    }
                                }
                        );
                    }
                }
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}