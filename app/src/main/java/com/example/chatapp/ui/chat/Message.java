package com.example.chatapp.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.MainActivity;
import com.example.chatapp.Params;
import com.example.chatapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Message extends AppCompatActivity {
    private String FRIEND_USER_ID;
    private ImageView imgFrndUser;
    private TextView txtFrndName;
    private RecyclerView recyclerViewChat;
    private EditText edMsgBox;
    private ImageView btnSend;
    private MsgAdapter chatAdapter;
    private ArrayList<ChatModel> arrChat;
    private DatabaseReference CURRENT_USER_REF;
    private DatabaseReference FRIEND_USER_REF;

    public void btnSend_Clicked(View v){
        String Msg = edMsgBox.getText().toString();

        if(!Msg.isEmpty()) {
            ChatModel chatModel = new ChatModel();
            chatModel.setCHAT(Msg);
            chatModel.setUSER_ID(Params.getCURRENT_USER());

            CURRENT_USER_REF.push().setValue(chatModel);

            FRIEND_USER_REF.push().setValue(chatModel);
            chatAdapter.notifyDataSetChanged();
            edMsgBox.setText("");
        }else
            Toast.makeText(this, "Enter the Message!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // initilization
        this.imgFrndUser = findViewById(R.id.imgFrndUser);
        this.txtFrndName = findViewById(R.id.txtFrndUserName);
        this.recyclerViewChat = findViewById(R.id.recyclerViewChat);
        this.edMsgBox = findViewById(R.id.edSend);
        this.btnSend = findViewById(R.id.imgSend);

        // get frnd details and fill fields
        Bundle bundle = getIntent().getBundleExtra("userData");
        this.FRIEND_USER_ID = bundle.getString("frndUID");
        txtFrndName.setText(bundle.getString("frndName"));
        if(bundle.getString("frndPic")!=null)
            Glide.with(this).load(bundle.getString("frndPic")).into(this.imgFrndUser);

        this.CURRENT_USER_REF = Params.getREFERENCE().child(Params.getCURRENT_USER()).child(Params.getCHAT()).child(FRIEND_USER_ID);
        this.FRIEND_USER_REF = Params.getREFERENCE().child(FRIEND_USER_ID).child(Params.getCHAT()).child(Params.getCURRENT_USER());

        Log.d("chat", "onCreate: "+FRIEND_USER_ID);
        // adapter and recycler view settings
        arrChat = new ArrayList<>();
        chatAdapter = new MsgAdapter(arrChat,this,txtFrndName.getText().toString());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        this.recyclerViewChat.setLayoutManager(linearLayoutManager);
        recyclerViewChat.setAdapter(chatAdapter);

        // Collecting data
        CURRENT_USER_REF.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrChat.clear();
                            for (DataSnapshot post : snapshot.getChildren()) {
                                ChatModel chatModel = new ChatModel();
                                chatModel.setCHAT(post.child("chat").getValue().toString());
                                chatModel.setUSER_ID(post.child("user_ID").getValue().toString());
                                arrChat.add(chatModel);
                            }

                            chatAdapter.notifyItemInserted(arrChat.size());
                            recyclerViewChat.scrollToPosition(arrChat.size() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void pushNotification(){

    }
}