package com.example.chatapp.ui.users;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PostProcessor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
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
import com.example.chatapp.UsersModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    public Params params = new Params();
    private List<UserModel> localDataSet;
    Context context;
    List<String> preFriends = new ArrayList<>();

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
    public CustomAdapter(List<UserModel> dataSet, Context con) {
        localDataSet = dataSet;
        context = con;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.all_users, viewGroup, false);

        return new CustomAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position).getUserName());
        if (localDataSet.get(position).getUserProfilePic() != null) {
            Glide.with(context).load(localDataSet.get(position).getUserProfilePic()).into(viewHolder.getImgUserProfile());
        }

        String frndUser = localDataSet.get(position).getUserName();
        if(localDataSet.get(position).getFriends().contains(params.getCURRENT_USER()))
            changeButtonFriend(viewHolder.getBtnAddUser(),frndUser);
        else if (localDataSet.get(position).getRequests().contains(params.getCURRENT_USER()))
            changeButtonReuest(viewHolder.getBtnAddUser());
        else
            simpleBtn(viewHolder.getBtnAddUser(),localDataSet.get(position).getUserId());
        Log.d("frndLIST", "User : "+localDataSet.get(position).getUserName()+" Requests = "+localDataSet.get(position).getRequests());
    }

    void changeButtonReuest(Button btn){
        btn.setBackgroundColor(Color.GRAY);
        btn.setText("Request Sent");
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Toast.makeText(context, "Request Already Sent", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    void changeButtonFriend(Button btn,String name){
        btn.setBackgroundColor(Color.GREEN);
        btn.setTextColor(Color.WHITE);
        btn.setText("Friend");

        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Already Friend "+name, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    void simpleBtn(Button btn,String frndUser){
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        params.getREFERENCE().child(frndUser).child(params.getREQUESTS()).push().setValue(params.getCURRENT_USER()).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Request Sent!", Toast.LENGTH_SHORT).show();
                                        changeButtonReuest(btn);
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