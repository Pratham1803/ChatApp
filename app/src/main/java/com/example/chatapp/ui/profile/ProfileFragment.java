package com.example.chatapp.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Path;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MainActivity;
import com.example.chatapp.Params;
import com.example.chatapp.R;
import com.example.chatapp.UserModel;
import com.example.chatapp.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
     // required variables
    private Params params;
    private UserModel CURRENT_USER = new UserModel();
     View root;
    private Uri imageUri;
    private ScrollView scrollView;
    private ImageView imgProfile;
    private EditText edName,edNum;
    private ProgressBar progressBar;
    private Button btnUpdate;
    private Button btnMyFriend;
    private Button btnLogOut;
    private Button btnRequest;
    private String ImageName;
    private RecyclerView recyclerView;
    private ProfileAdapter profileAdapter;
    private List<UserModel> lsUser;
    private @NonNull FragmentProfileBinding binding;

    // Update button clicked
    public void btnUpdate_Clicked() {
        if(imageUri != null) {
            String fileName = System.currentTimeMillis() + "." + getFileExtendion(imageUri);
            params.getSTORAGE().child(fileName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    params.getSTORAGE().child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (ImageName != null)
                                params.getSTORAGE().child(ImageName).delete();
                            UserModel model = new UserModel();
                            model.setUserProfilePic(uri.toString());
                            model.setUserName(edName.getText().toString());
                            model.setUserContactNum(edNum.getText().toString());

                            params.getREFERENCE().child(params.getCURRENT_USER()).setValue(model);
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

            params.getREFERENCE().child(params.getCURRENT_USER()).child(params.getNAME()).setValue(model.getUserName()).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
        Log.d("profile", "onSuccess: User name = " + CURRENT_USER.getUserName());
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
        Log.d("profile", "btnRequests_Clicked: "+CURRENT_USER.getRequests());
        lsUser.clear();
        params.getREFERENCE().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lsUser.clear();
                        for(DataSnapshot post : snapshot.getChildren()){
                            if(CURRENT_USER.getFriends().contains(post.getKey())){
                                UserModel newUser = post.getValue(UserModel.class);
                                newUser.setUserId(post.getKey());
                                lsUser.add(newUser);
                            }
                        }
                        profileAdapter.notifyItemInserted(lsUser.size()-1);
                        recyclerView.scrollToPosition(lsUser.size()-1);
                        scrollView.fullScroll(View.FOCUS_DOWN);
                        if (lsUser.isEmpty())
                            Toast.makeText(root.getContext(), "You have not any friends!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    // requests button clicked, show all requests
    public void btnRequests_Clicked(){
        Log.d("profile", "btnRequests_Clicked: "+CURRENT_USER.getRequests());
        lsUser.clear();
        params.getREFERENCE().addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lsUser.clear();
                        for(DataSnapshot post : snapshot.getChildren()){
                            if(CURRENT_USER.getRequests().contains(post.getKey())){
                                UserModel newUser = post.getValue(UserModel.class);
                                newUser.setUserId(post.getKey());
                                lsUser.add(newUser);
                            }
                        }
                        profileAdapter.notifyItemInserted(lsUser.size());
                        recyclerView.scrollToPosition(lsUser.size()-1);
                        scrollView.fullScroll(View.FOCUS_DOWN);
                        if (lsUser.isEmpty())
                            Toast.makeText(root.getContext(), "No new Requests", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    // fill the details
    private void fillFields(){
        Log.d("profile", "fillFields: drawable "+imgProfile.getDrawable());
        progressBar.setVisibility(View.VISIBLE);
        params.getREFERENCE().child(params.getCURRENT_USER()).get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        UserModel user = dataSnapshot.getValue(UserModel.class);

                        edName.setText(user.getUserName());
                        edNum.setText(user.getUserContactNum());

                        DataSnapshot s = dataSnapshot.child(params.getFRIENDS());
                        ArrayList<String> lsFriends = new ArrayList<>();
                        if(s.exists()) {
                            Log.d("UserRecord", "onDataChange: " + s.getChildren());
                            for(DataSnapshot childSnap : s.getChildren())
                                lsFriends.add(childSnap.getValue().toString());
                        }
                        user.setFriends(lsFriends);

                        ArrayList<String> lsRequests = new ArrayList<>();
                        s = dataSnapshot.child(params.getREQUESTS());
                        if(s.exists()) {
                            Log.d("UserRecord", "onDataChange: " + s.getChildren());
                            for(DataSnapshot childSnap : s.getChildren())
                                lsRequests.add(childSnap.getValue().toString());
                        }
                        user.setRequests(lsRequests);
                        progressBar.setVisibility(View.INVISIBLE);

                        if(user.getUserProfilePic() != null) {
                            Glide.with(getContext()).load(user.getUserProfilePic()).into(imgProfile);
                            ImageName = params.getSTORAGE().child(user.getUserProfilePic()).getName();
                            ImageName = ImageName.substring(0, ImageName.indexOf("?"));
                        }
                        CURRENT_USER.setUserId(user.getUserId());
                        CURRENT_USER.setUserName(user.getUserName());
                        CURRENT_USER.setUserProfilePic(user.getUserProfilePic());
                        CURRENT_USER.setFriends(user.getFriends());
                        CURRENT_USER.setRequests(user.getRequests());
                    }
                }
        );
        Log.d("profile", "fillFields: Requests = "+ CURRENT_USER.getUserName());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.params = new Params();

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        // initilizing variables
        imgProfile = root.findViewById(R.id.imgProfilePic);
        edName = root.findViewById(R.id.edUserName);
        edNum = root.findViewById(R.id.edUserNum);
        btnUpdate = root.findViewById(R.id.btnUpdate);
        btnMyFriend = root.findViewById(R.id.btnMyFriends);
        btnRequest = root.findViewById(R.id.btnRequest);
        btnLogOut = root.findViewById(R.id.btnLogOut);
        progressBar = root.findViewById(R.id.progressBar2);
        recyclerView = root.findViewById(R.id.recyclerViewRequests);
        scrollView = root.findViewById(R.id.scrollView2);
        fillFields();

        // seting recycler view
        lsUser = new ArrayList<>();
        profileAdapter = new ProfileAdapter(lsUser, root.getContext(),params.getFRIENDS());
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

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.getAUTH().signOut();
                Intent i = new Intent(root.getContext(), MainActivity.class);
                startActivity(i);
                getActivity().finish();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}