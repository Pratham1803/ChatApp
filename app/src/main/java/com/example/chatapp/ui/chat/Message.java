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
import com.example.chatapp.PushNotification;
import com.example.chatapp.R;
import com.example.chatapp.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Message extends AppCompatActivity {
    private String FRIEND_USER_ID;
    private String FRIEND_FCM_TOKEN;
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
            chatModel.setUSER_ID(Params.getCurrentUserModel().getUserId());

            CURRENT_USER_REF.push().setValue(chatModel);

            FRIEND_USER_REF.push().setValue(chatModel);
            //chatAdapter.notifyDataSetChanged();
            edMsgBox.setText("");

            sendNotification(Msg);
        }else
            Toast.makeText(Message.this, "Enter the Message!!", Toast.LENGTH_SHORT).show();
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
        this.FRIEND_FCM_TOKEN = bundle.getString("fcmToken");
        txtFrndName.setText(bundle.getString("frndName"));
        if(bundle.getString("frndPic")!=null)
            Glide.with(this).load(bundle.getString("frndPic")).into(this.imgFrndUser);

        this.CURRENT_USER_REF = Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getCHAT()).child(FRIEND_USER_ID);
        this.FRIEND_USER_REF = Params.getREFERENCE().child(FRIEND_USER_ID).child(Params.getCHAT()).child(Params.getCurrentUserModel().getUserId());

        Log.d("chat", "onCreate: "+FRIEND_USER_ID);
        // adapter and recycler view settings
        arrChat = new ArrayList<>();
        chatAdapter = new MsgAdapter(arrChat,Message.this,txtFrndName.getText().toString());

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

    private void sendNotification(String Msg){
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", Params.getCurrentUserModel().getUserName());
            notificationObject.put("body", Msg);
            notificationObject.put("image", Params.getCurrentUserModel().getUserProfilePic());
            notificationObject.put("myicon", "@drawable/ic_stat_name");

            JSONObject dataObj = new JSONObject();
            dataObj.put("Screen","Chat");
            dataObj.put("userId",Params.getCurrentUserModel().getUserId());

            jsonObject.put("notification",notificationObject);
            jsonObject.put("data",dataObj);

            jsonObject.put("to",FRIEND_FCM_TOKEN);
            PushNotification.callApi(jsonObject);
        }catch (Exception e){
            Log.d("ErrorMsg", "sendNotification: "+e.toString());
        }
    }
}