package com.example.chatapp.ui.chat;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.MainActivity;
import com.example.chatapp.Params;
import com.example.chatapp.R;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    List<ChatModel> localDataSet;
    final String FRND_USER_NAME;
    Context context;
    Params params;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtFrndName;
        private final EditText edFrndMsg;
        private final EditText edUserMsg;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            txtFrndName = view.findViewById(R.id.txtFrndName);
            edFrndMsg = view.findViewById(R.id.edFrndMsg);
            edUserMsg = view.findViewById(R.id.edUserMsg);
        }

        public TextView getTxtFrndName() {
            return txtFrndName;
        }

        public EditText getEdFrndMsg() {
            return edFrndMsg;
        }

        public EditText getEdUserMsg() {
            return edUserMsg;
        }
    }

    public MsgAdapter(List<ChatModel> dataSet, Context con,String FRND_USER_NAME){
        Log.d("chatScreen", "ChatAdapter: "+dataSet);
        this.localDataSet = dataSet;
        this.context = con;
        this.params = new Params();
        this.FRND_USER_NAME = FRND_USER_NAME;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("ChatAdapter", "getItemViewType: "+localDataSet.get(position).getUSER_ID().equals(Params.getCurrentUserModel().getUserId()));
        Log.d("ChatAdapter", "getItemViewType: "+localDataSet.get(position).getUSER_ID());
        Log.d("ChatAdapter", "getItemViewType: "+ Params.getCurrentUserModel().getUserId());

        if(localDataSet.get(position).getUSER_ID().equals(Params.getCurrentUserModel().getUserId()))
            return 0;
        else
            return 1;
    }

    @NonNull
    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        if(viewType == 0) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_user_msg, parent, false);

            return new MsgAdapter.ViewHolder(view);
        }
        else if(viewType == 1){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_user_msg, parent, false);

            return new MsgAdapter.ViewHolder(view);
        }
        else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MsgAdapter.ViewHolder holder, int position) {
        try {
            if (localDataSet.get(position).getUSER_ID().equals(Params.getCurrentUserModel().getUserId())) {
                holder.getEdUserMsg().setText(localDataSet.get(position).getCHAT());
            } else {
                holder.getTxtFrndName().setText(FRND_USER_NAME);
                holder.getEdFrndMsg().setText(localDataSet.get(position).getCHAT());
            }
        }catch (IndexOutOfBoundsException ie){
            Log.d("Chat Error", "onBindViewHolder: "+ ie);
            context.startActivity(new Intent(context, MainActivity.class));
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}