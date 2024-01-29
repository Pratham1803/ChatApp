package com.example.chatapp.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MainActivity;
import com.example.chatapp.Params;
import com.example.chatapp.R;
import com.example.chatapp.UserModel;
import com.example.chatapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
     // required variables
     View root;
    private Uri imageUri;
    private ScrollView scrollView;
    private ImageView imgProfile;
    private EditText edName,edNum;
    private ProgressBar progressBar;
    private Button btnUpdate;
    private Button btnMyFriend;
    private Button btnRequest;
    private String ImageName;
    private RecyclerView recyclerView;
    private ProfileAdapter profileAdapter;
    private List<UserModel> lsUser;
    private TextView txtUsersHead;

    // Update button clicked
    public void btnUpdate_Clicked() {
        if(imageUri != null) {
            String fileName = System.currentTimeMillis() + "." + getFileExtendion(imageUri);
            Params.getSTORAGE().child(fileName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Params.getSTORAGE().child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (ImageName != null)
                                Params.getSTORAGE().child(ImageName).delete();
                            UserModel model = new UserModel();
                            model.setUserProfilePic(uri.toString());
                            model.setUserName(edName.getText().toString());
                            model.setUserContactNum(edNum.getText().toString());

                            Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getNAME()).setValue(model.getUserName());
                            Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getPROFILE_PIC()).setValue(model.getUserProfilePic());

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("errorUpload", "onFailure: " + e);
                    Toast.makeText(getContext(), "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else{
            UserModel model = new UserModel();
            model.setUserName(edName.getText().toString());

            Params.getREFERENCE().child(Params.getCurrentUserModel().getUserId()).child(Params.getNAME()).setValue(model.getUserName()).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }

    }

    // get the file type
    private String getFileExtendion(Uri mUri) {
        ContentResolver contentResolver = root.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(mUri));
    }

    public void imgProfilePic_Clicked() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i,2);
    }

    // friends button clicked, show all requests
    public void btnFriends_Clicked(){
        lsUser.clear();
        Params.getREFERENCE().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lsUser.clear();
                        for(DataSnapshot post : snapshot.getChildren()){
                            if(Params.getCurrentUserModel().getFriends().contains(post.getKey())){
                                UserModel newUser = post.getValue(UserModel.class);
                                newUser.setUserId(post.getKey());
                                lsUser.add(newUser);
                            }
                        }
                        if (lsUser.isEmpty()) {
                            txtUsersHead.setText("There is Not any Friends");
                            txtUsersHead.setVisibility(View.VISIBLE);
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }else
                            txtUsersHead.setVisibility(View.INVISIBLE);

                        profileAdapter.notifyDataSetChanged();
                        profileAdapter.setCURRENT_BTN(Params.getFRIENDS());

                        scrollView.fullScroll(View.FOCUS_DOWN);
                        recyclerView.scrollToPosition(lsUser.size()+(lsUser.size()+1));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    // requests button clicked, show all requests
    public void btnRequests_Clicked(){
        lsUser.clear();
        Params.getREFERENCE().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lsUser.clear();
                        for(DataSnapshot post : snapshot.getChildren()){
                            if(Params.getCurrentUserModel().getRequests().contains(post.getKey())){
                                UserModel newUser = post.getValue(UserModel.class);
                                newUser.setUserId(post.getKey());
                                lsUser.add(newUser);
                            }
                        }

                        if (lsUser.isEmpty()) {
                            txtUsersHead.setText("No new Requests!!");
                            scrollView.fullScroll(View.FOCUS_DOWN);
                            txtUsersHead.setVisibility(View.VISIBLE);
                        }else
                            txtUsersHead.setVisibility(View.INVISIBLE);

                        profileAdapter.notifyDataSetChanged();
                        profileAdapter.setCURRENT_BTN(Params.getREQUESTS());

                        scrollView.fullScroll(View.FOCUS_DOWN);
                        recyclerView.scrollToPosition(lsUser.size()+(lsUser.size()+1));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    // fill the details
    private void fillFields(){
        edNum.setText(Params.getCurrentUserModel().getUserContactNum());
        edName.setText(Params.getCurrentUserModel().getUserName());
        if(Params.getCurrentUserModel().getUserProfilePic() != null) {
            try {
                Glide.with(root.getContext()).load(Params.getCurrentUserModel().getUserProfilePic()).into(imgProfile);
                ImageName = Params.getSTORAGE().child(Params.getCurrentUserModel().getUserProfilePic()).getName();
                ImageName = ImageName.substring(0, ImageName.indexOf("?"));
            }catch  (Exception ignored){}
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_profile,container,false);

        // initilizing variables
        imgProfile = root.findViewById(R.id.imgProfilePic);
        edName = root.findViewById(R.id.edUserName);
        edNum = root.findViewById(R.id.edUserNum);
        btnUpdate = root.findViewById(R.id.btnUpdate);
        btnMyFriend = root.findViewById(R.id.btnMyFriends);
        btnRequest = root.findViewById(R.id.btnRequest);
        progressBar = root.findViewById(R.id.progressBar2);
        recyclerView = root.findViewById(R.id.recyclerViewRequests);
        scrollView = root.findViewById(R.id.scrollView2);
        txtUsersHead = root.findViewById(R.id.txtUsersHead);

        try {
            fillFields();
        }catch (Exception e){
            Toast.makeText(root.getContext(), "Counldn't Referesh", Toast.LENGTH_SHORT).show();
        }


        // seting recycler view
        lsUser = new ArrayList<>();

        profileAdapter = new ProfileAdapter(lsUser, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setAdapter(profileAdapter);

        // onclick listeners
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgProfilePic_Clicked();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdate_Clicked();
            }
        });
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRequests_Clicked();
            }
        });
        btnMyFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFriends_Clicked();
            }
        });

        return root;
    }

    // overriding method to see if the image file correct then set image on imageview
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }

    public Button getBtnRequest() {
        return btnRequest;
    }
}